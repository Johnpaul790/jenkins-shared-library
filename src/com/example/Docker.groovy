#!/usr/bin/env groovy

package com.example

class Docker implements Serializable {

    def script

    Docker(script) {
        this.script = script
    }

    def buildDockerImage(String imageName) {
        script.echo "building the docker image..."
        script.sh "docker build -t $imageName ."
    }

    def dockerLogin() {
        script.withCredentials([
            script.usernamePassword(
                credentialsId: 'docker-hub-repo',
                passwordVariable: 'PASS',
                usernameVariable: 'USER'
            )
        ]) {
            script.sh '''
                printf '%s' "$PASS" | docker login -u "$USER" --password-stdin
            '''
        }
    }

    def dockerPush(String imageName) {
        script.sh "docker push $imageName"
    }

   def deployToEC2(String imageName) {
    script.sshagent(credentials: ['ec2-deploy-key']) {
        script.sh """
            ssh \
                -o BatchMode=yes \
                -o ConnectTimeout=10 \
                -o StrictHostKeyChecking=yes \
                ec2-user@${script.env.EC2_HOST} '
                    set -e

                    echo "Pulling latest image..."
                    docker pull ${imageName}

                    echo "Stopping old container..."
                    docker stop java-maven-app 2>/dev/null || true

                    echo "Removing old container..."
                    docker rm java-maven-app 2>/dev/null || true

                    echo "Starting new container..."
                    docker run -d \
                        --name java-maven-app \
                        --restart unless-stopped \
                        -p 8080:8080 \
                        ${imageName}

                    echo "Checking application..."

                    for attempt in 1 2 3 4 5 6 7 8 9 10
                    do
                        if curl -fsS http://localhost:8080/ >/dev/null
                        then
                            echo "Application is running"
                            exit 0
                        fi

                        echo "Application not ready yet..."
                        sleep 3
                    done

                    echo "Application failed to start"
                    docker logs --tail 50 java-maven-app
                    exit 1
                '
        """
    }
}
}