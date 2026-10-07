# Jenkins Shared Library — Maven and Docker

## Overview

A reusable [Jenkins Shared Library](https://github.com/Johnpaul790/jenkins-shared-library) for Maven builds and Docker image workflows. It has been successfully loaded and executed by Jenkins from the separate [Java Maven CI application repository](https://github.com/Johnpaul790/jenkins-java-maven-cicd).

## Purpose

Centralize reusable CI logic instead of duplicating it across application Jenkinsfiles. Application pipelines call shared functions for Maven packaging, Docker image builds, Docker Hub authentication, and image publishing.

## Repository Structure

```text
jenkins-shared-library/
├── vars/
│   ├── buildJar.groovy
│   ├── buildImage.groovy
│   ├── dockerLogin.groovy
│   └── dockerPush.groovy
├── src/com/example/
│   └── Docker.groovy       # Shared Docker implementation
├── .gitignore
└── README.md
```

## Local Development

Library changes are edited locally and validated by running the consuming application's Jenkins pipeline.

The Jenkins agent must provide:

Maven

Docker CLI

Access to a Docker daemon

Editor or IDE validation can help with syntax, but the Shared Library is ultimately executed in the Jenkins runtime environment, so Jenkins pipeline execution is the final validation step.

Local IDE metadata, generated output, and development-only dependencies are excluded from version control through .gitignore.

## Shared Functions

| Function | Implemented behavior |
| --- | --- |
| `buildJar()` | Runs `mvn clean package`. |
| `buildImage(imageName)` | Runs `docker build -t <imageName> .` using the current directory as the build context. |
| `dockerLogin()` | Authenticates to Docker Hub using Jenkins credential `docker-hub-repo`. |
| `dockerPush(imageName)` | Runs `docker push <imageName>`. |

The Docker functions delegate to `src/com/example/Docker.groovy`, which receives the pipeline script to invoke Jenkins steps.

## Example Usage

An application Jenkinsfile can use the configured shared library as follows. The Jenkins agent needs Maven, Docker, and access to a Docker daemon; the checked-out application must contain a Maven project and Dockerfile. Replace the example image name with the target Docker Hub repository and tag.

```groovy
@Library('jenkins-shared-library') _

pipeline {
    agent any
    environment {
        IMAGE_NAME = 'your-dockerhub-user/your-app:your-tag'
    }
    stages {
        stage('Build and Publish') {
            steps {
                buildJar()
                buildImage(env.IMAGE_NAME)
                dockerLogin()
                dockerPush(env.IMAGE_NAME)
            }
        }
    }
}
```

## Credential Handling

Docker Hub authentication uses a Jenkins username/password credential with ID `docker-hub-repo`. Inside `withCredentials`, Jenkins binds the values to `USER` and `PASS`. The shell expands these variables; Groovy does not interpolate the credential values:

```sh
printf '%s' "$PASS" | docker login -u "$USER" --password-stdin
```

Credential values are managed in Jenkins and are not stored in this repository.

## Technologies

Jenkins · Jenkins Shared Libraries · Groovy · Maven · Docker · Docker Hub · Jenkins Credentials · Git · GitHub

## What This Project Demonstrates

- Reusable Jenkins pipeline components with Groovy-based Shared Libraries
- Separation of application and CI logic
- Maven build automation
- Docker image build and publishing
- Jenkins credential handling
- Cross-repository CI reuse

## Related Project

[jenkins-java-maven-ci](https://github.com/Johnpaul790/jenkins-java-maven-cicd) contains the application pipeline that consumes this library.

## Project Background

A hands-on Jenkins Shared Library project focused on reusable CI logic, Maven build automation, Docker image workflows, and secure credential handling across separate repositories.
