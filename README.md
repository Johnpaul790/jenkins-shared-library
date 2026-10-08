# Jenkins Shared Library — Maven, Docker and EC2 Deployment

## Overview

A reusable [Jenkins Shared Library](https://github.com/Johnpaul790/jenkins-shared-library) for Maven builds, Docker image workflows, and automated deployment to Amazon EC2.

The library is consumed by the separate [Java Maven CI/CD application repository](https://github.com/Johnpaul790/jenkins-java-maven-cicd), where it provides reusable pipeline steps for building, publishing, and deploying the application.

## Purpose

Centralize reusable Jenkins pipeline logic outside the application repository. Application pipelines call shared functions for Maven packaging, Docker image creation, Docker Hub authentication and publishing, and EC2 deployment.

## Repository Structure

```text
jenkins-shared-library/
├── vars/
│   ├── buildJar.groovy
│   ├── buildImage.groovy
│   ├── dockerLogin.groovy
│   ├── dockerPush.groovy
│   └── deployToEC2.groovy
├── src/com/example/
│   └── Docker.groovy
├── .gitignore
└── README.md
```

## Local Development

Library changes are edited locally and validated by running the consuming application's Jenkins pipeline.

The Jenkins environment must provide:

- Maven
- Docker CLI
- Access to a Docker daemon
- An SSH client
- The SSH Agent plugin
- The required Jenkins credentials
- The `EC2_HOST` environment variable

Editor or IDE validation can help with syntax, but the Shared Library is ultimately executed in the Jenkins runtime environment, so Jenkins pipeline execution is the final validation step.


## Shared Functions

| Function | Implemented behavior |
| --- | --- |
| `buildJar()` | Runs `mvn clean package`. |
| `buildImage(imageName)` | Builds the Docker image using `docker build -t <imageName> .`. |
| `dockerLogin()` | Authenticates to Docker Hub using the Jenkins credential `docker-hub-repo`. |
| `dockerPush(imageName)` | Pushes the Docker image to Docker Hub. |
| `deployToEC2(imageName)` | Uses SSH to connect to the EC2 deployment target, pulls the latest image, replaces the running application container, and verifies the deployment over HTTP. |

The Docker and deployment functions delegate to `src/com/example/Docker.groovy`, which receives the Jenkins pipeline context and executes the required Jenkins, SSH, and Docker operations.


## Example Usage

An application Jenkinsfile can load the Shared Library and use the reusable functions inside its pipeline stages:

```groovy
@Library('jenkins-shared-library') _

pipeline {
    agent any

    tools {
        maven 'maven-3.9.12'
    }

    environment {
        IMAGE_NAME = 'your-dockerhub-user/your-app:your-tag'
    }

    stages {
        stage('Build JAR') {
            steps {
                buildJar()
            }
        }

        stage('Build Docker Image') {
            steps {
                buildImage(env.IMAGE_NAME)
            }
        }

        stage('Docker Login') {
            steps {
                dockerLogin()
            }
        }

        stage('Push Docker Image') {
            steps {
                dockerPush(env.IMAGE_NAME)
            }
        }

        stage('Deploy to EC2') {
            steps {
                deployToEC2(env.IMAGE_NAME)
            }
        }
    }
}
```

The Jenkins environment must provide Maven, Docker CLI access, the required Jenkins credentials, and the `EC2_HOST` environment variable used by the deployment function.

## Credential Handling

Docker Hub authentication uses a Jenkins username/password credential with ID `docker-hub-repo`. Inside `withCredentials`, Jenkins binds the values to `USER` and `PASS`, and the password is passed securely to Docker through standard input:

```sh
printf '%s' "$PASS" | docker login -u "$USER" --password-stdin
```

EC2 deployment uses a Jenkins SSH credential with ID `ec2-deploy-key`. The private key is stored in Jenkins Credentials and loaded at runtime through the SSH Agent plugin.

The deployment target is supplied through the Jenkins environment variable `EC2_HOST`, so the EC2 address is not hardcoded in the repository.

SSH host verification remains enabled with `StrictHostKeyChecking=yes`, and the target host key is stored in the Jenkins user's `known_hosts` file.

Credential values and private keys are managed in Jenkins and are not stored in this repository.

## Technologies

Jenkins · Jenkins Shared Libraries · Groovy · Maven · Docker · Docker Hub · AWS EC2 · SSH · Jenkins Credentials · Git · GitHub

## What This Project Demonstrates

- Reusable Jenkins pipeline components with Groovy-based Shared Libraries
- Separation of application pipeline orchestration from reusable implementation logic
- Maven build automation with `mvn clean package`
- Docker image build and publishing
- Secure Jenkins credential handling
- SSH-based deployment to Amazon EC2
- Automated container replacement using Docker
- Post-deployment HTTP verification with retry logic
- Cross-repository CI/CD reuse

## Related Project

[jenkins-java-maven-cicd](https://github.com/Johnpaul790/jenkins-java-maven-cicd) contains the Java / Spring Boot application and Jenkinsfile that consume this Shared Library.

The application pipeline uses the library to build and test the application, build and publish the Docker image, deploy it to Amazon EC2, and verify the running application.

## Project Background

A hands-on Jenkins Shared Library project developed as part of a practical CI/CD implementation. The project demonstrates reusable pipeline logic for Maven builds, Docker image workflows, secure credential handling, and automated deployment to Amazon EC2.
