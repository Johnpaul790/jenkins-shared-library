# Jenkins Shared Library for Maven and Docker

Personal Jenkins CI/CD project implementing reusable Groovy pipeline components for Maven packaging and Docker image workflows.

The library is used by the related [Java Maven CI/CD project](https://github.com/Johnpaul790/jenkins-java-maven-cicd).

## Purpose

This project demonstrates how common Jenkins pipeline logic can be extracted into a Shared Library instead of repeating shell commands across Jenkins pipelines.

The library provides reusable functions for:

- Maven packaging
- Docker image builds
- Docker Hub authentication
- Docker image publishing

The application pipeline and deployment logic are maintained in the separate application repository.


## Repository Structure

```text
jenkins-shared-library/
├── vars/
│   ├── buildJar.groovy       # Runs Maven packaging
│   ├── buildImage.groovy     # Wraps Docker.buildDockerImage
│   ├── dockerLogin.groovy    # Wraps Docker.dockerLogin
│   └── dockerPush.groovy     # Wraps Docker.dockerPush
├── src/
│   ├── com/example/Docker.groovy  # Shared Docker implementation
│   └── Main.groovy                # Standalone Hello world entry point
├── lib/                          # Bundled Groovy 5.0.0 JARs and source JARs
└── .gitignore
```

## Shared Pipeline Functions

| Function | Purpose |
| --- | --- |
| `buildJar()` | Packages the Maven application |
| `buildImage(imageName)` | Builds a Docker image |
| `dockerLogin()` | Authenticates to Docker Hub using Jenkins Credentials |
| `dockerPush(imageName)` | Pushes the Docker image to Docker Hub |

The `com.example.Docker` class contains reusable Docker functionality and receives the Jenkins pipeline script so that it can invoke Jenkins steps such as `sh`, `echo`, and `withCredentials`.

## Example Usage

After configuring this repository as a Jenkins Shared Library, the functions can be called from an application pipeline:

```groovy
buildJar()
buildImage('namespace/image:tag')
dockerLogin()
dockerPush('namespace/image:tag')
```

The Jenkins agent requires Maven and Docker to execute these operations.

## Technologies

- Jenkins
- Jenkins Shared Libraries
- Groovy
- Maven
- Docker
- Docker Hub
- Jenkins Credentials

## Credentials

Docker Hub credentials are managed through Jenkins Credentials and referenced through a Jenkins credential ID.

No Docker Hub usernames, passwords, or access tokens are stored directly in this repository.

## What This Project Demonstrates

- Creating reusable Jenkins Shared Library functions
- Structuring CI/CD logic with Groovy
- Separating reusable pipeline logic from application code
- Automating Maven packaging and Docker image workflows
- Integrating Jenkins Credentials with Docker Hub authentication
- Sharing CI/CD functionality across repositories

## Related Project

[jenkins-java-maven-cicd](https://github.com/Johnpaul790/jenkins-java-maven-cicd)

The related repository contains the Java/Maven application and the CI/CD pipeline that consumes this shared library.