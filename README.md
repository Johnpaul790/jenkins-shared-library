# Jenkins Shared Library — Maven and Docker

## Overview

A reusable [Jenkins Shared Library](https://github.com/Johnpaul790/jenkins-shared-library) for Maven builds and Docker image workflows. It has been successfully loaded and executed by Jenkins from the separate [Java Maven CI/CD application repository](https://github.com/Johnpaul790/jenkins-java-maven-cicd).

## Purpose

Centralize reusable CI/CD logic instead of duplicating it across application Jenkinsfiles. Application pipelines call shared functions for Maven packaging, Docker image builds, Docker Hub authentication, and image publishing.

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

Edit this library in Visual Studio Code with a Groovy or Jenkinsfile extension.
Follow the extension's setup instructions for any required development tools.

Jenkins provides the Groovy environment used to execute this shared library.
Bundled Groovy SDK JARs are not required for Jenkins execution and are not
tracked in this repository.

The local `lib/` directory was previously used by IntelliJ as a Groovy SDK and
is now ignored by Git. Existing local copies can remain, but new clones do not
include them. Install any tools required for local development separately.
IntelliJ metadata (`.idea/` and `*.iml`) and generated output (`out/`) are also
ignored.

Validate library changes by running the consuming application's pipeline in
Jenkins. The Jenkins agent needs Maven, Docker, and access to a Docker daemon.
Editor checks alone do not validate Jenkins pipeline behavior.

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
- Separation of application and CI/CD logic
- Maven build automation
- Docker image build and publishing
- Jenkins credential handling
- Cross-repository CI/CD reuse

## Related Project

[jenkins-java-maven-cicd](https://github.com/Johnpaul790/jenkins-java-maven-cicd) contains the application pipeline that consumes this library.

## Project Background

Implemented as a hands-on DevOps project during technical training and further developed to practice reusable CI/CD workflows.
