#!/usr/bin/env groovy
package com.example

class Docker implements Serializable {

    def script

    Docker (script) {
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
}