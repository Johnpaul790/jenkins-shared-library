#!/usr/bin/env groovy

def call() {
    echo "building the application for branch $GIT_BRANCH"
    sh 'mvn clean package'
}