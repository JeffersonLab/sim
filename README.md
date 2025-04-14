# smoothness-template [![CI](https://github.com/JeffersonLab/smoothness-template/actions/workflows/ci.yaml/badge.svg)](https://github.com/JeffersonLab/smoothness-template/actions/workflows/ci.yaml) [![Docker](https://img.shields.io/docker/v/jeffersonlab/smoothness-template?sort=semver&label=DockerHub)](https://hub.docker.com/r/jeffersonlab/smoothness-template)
A [Java EE 8](https://en.wikipedia.org/wiki/Jakarta_EE) web application repo template, relying on the [Smoothness](https://github.com/JeffersonLab/smoothness) web lib.

![Screenshot](https://github.com/JeffersonLab/smoothness-template/raw/main/Screenshot.png?raw=true "Screenshot")

---
- [Overview](https://github.com/JeffersonLab/smoothness-template#overview)
- [Quick Start with Compose](https://github.com/JeffersonLab/smoothness-template#quick-start-with-compose)
- [Install](https://github.com/JeffersonLab/smoothness-template#install)
- [Configure](https://github.com/JeffersonLab/smoothness-template#configure)
- [Build](https://github.com/JeffersonLab/smoothenss-template#build)
- [Develop](https://github.com/JeffersonLab/smoothness-template#develop)
- [Release](https://github.com/JeffersonLab/smoothness-template#release)
- [Deploy](https://github.com/JeffersonLab/smoothness-template#deploy)
- [See Also](https://github.com/JeffersonLab/smoothness-template#see-also)
---

## Overview
A Repo Template for web apps at JLab.  This repo is intended to be used at new repository creation time to bootstrap new web app projects.  Once a new repo is created from this one, a bunch of search and replace is used to rename everything needed for the new app.  When viewing this repo in GitHub, click the "Use this template" button to create a new repo.  More [Template Usage](https://github.com/JeffersonLab/smoothness-template/blob/main/TEMPLATE-USAGE.md)

## Quick Start with Compose
1. Grab project
```
git clone https://github.com/JeffersonLab/smoothness-template
cd smoothenss-template
```
2. Launch [Compose](https://github.com/docker/compose)
```
docker compose up
```
3. Navigate to page
```
http://localhost:8080/smoothness-template
```

**Note**: Login with demo username "tbrown" and password "password".

See: [Docker Compose Strategy](https://gist.github.com/slominskir/a7da801e8259f5974c978f9c3091d52c)

## Install
This application requires a Java 11+ JVM and standard library to run, plus a Java EE 8+ application server (developed with Wildfly).


1. Install service [dependencies](https://github.com/JeffersonLab/smoothness-template/blob/main/deps.yaml)
2. Download [Wildfly 26.1.3](https://www.wildfly.org/downloads/)
3. [Configure](https://github.com/JeffersonLab/smoothness-template#configure) Wildfly and start it
4. Download [template.war](https://github.com/JeffersonLab/smoothness-template/releases) and deploy it to Wildfly
5. Navigate your web browser to [localhost:8080/template](http://localhost:8080/template)

## Configure

### Configtime
Wildfly must be pre-configured before the first deployment of the app. The [wildfly bash scripts](https://github.com/JeffersonLab/wildfly#configure) can be used to accomplish this. See the [Dockerfile](https://github.com/JeffersonLab/smoothness-template/blob/main/Dockerfile) for an example.

### Runtime
Uses the [Smoothness Environment Variables](https://github.com/JeffersonLab/smoothness#environment-variables).

## Build
This project is built with [Java 17](https://adoptium.net/) (compiled to Java 11 bytecode), and uses the [Gradle 7](https://gradle.org/) build tool to automatically download dependencies and build the project from source:

```
git clone https://github.com/JeffersonLab/smoothness-template
cd smoothness-template
gradlew build
```
**Note**: If you do not already have Gradle installed, it will be installed automatically by the wrapper script included in the source

**Note for JLab On-Site Users**: Jefferson Lab has an intercepting [proxy](https://gist.github.com/slominskir/92c25a033db93a90184a5994e71d0b78)

See: [Docker Development Quick Reference](https://gist.github.com/slominskir/a7da801e8259f5974c978f9c3091d52c#development-quick-reference)

## Develop
In order to iterate rapidly when making changes it's often useful to run the app directly on the local workstation, perhaps leveraging an IDE.  In this scenario run the service dependencies with:
```
docker compose -f deps.yaml up
```
**Note**: The local install of Wildfly should be [configured](https://github.com/JeffersonLab/cnm#configure) to proxy connections to services via localhost and therefore the environment variables should contain:
```
KEYCLOAK_BACKEND_SERVER_URL=http://localhost:8081
FRONTEND_SERVER_URL=https://localhost:8443
```
Further, the local DataSource must also leverage localhost port forwarding so the `standalone.xml` connection-url field should be: `jdbc:oracle:thin:@//localhost:1521/xepdb1`.

The [server](https://github.com/JeffersonLab/wildfly/blob/main/scripts/server-setup.sh) and [app](https://github.com/JeffersonLab/wildfly/blob/main/scripts/app-setup.sh) setup scripts can be used to setup a local instance of Wildfly.

## Release
1. Bump the version number in the VERSION file and commit and push to GitHub (using [Semantic Versioning](https://semver.org/)).
2. The [CD](https://github.com/JeffersonLab/smoothness-template/blob/main/.github/workflows/cd.yaml) GitHub Action should run automatically invoking:
    - The [Create release](https://github.com/JeffersonLab/java-workflows/blob/main/.github/workflows/gh-release.yaml) GitHub Action to tag the source and create release notes summarizing any pull requests.   Edit the release notes to add any missing details.  A war file artifact is attached to the release.
    - The [Publish docker image](https://github.com/JeffersonLab/container-workflows/blob/main/.github/workflows/docker-publish.yaml) GitHub Action to create a new demo Docker image.
    - The [Deploy to JLab](https://github.com/JeffersonLab/general-workflows/blob/main/.github/workflows/jlab-deploy-app.yaml) GitHub Action to deploy to the JLab test environment.

## Deploy
The deploy to JLab's acctest is handled automatically via the release workflow.

At JLab this app is found internally at [acctest.acc.jlab.org/template](https://acctest.acc.jlab.org/template).  However, the server is a proxy for `wildflytest5.acc.jlab.org`.   A [deploy script](https://github.com/JeffersonLab/wildfly/blob/main/scripts/deploy.sh) is provided on each server to automate wget and deploy.  Example:

```
/root/setup/deploy.sh template v1.2.3
```

**JLab Internal Docs**:  [InstallGuideWildflyRHEL9](https://accwiki.acc.jlab.org/do/view/SysAdmin/InstallGuideWildflyRHEL9)

## See Also
- [JLab ACE management-app list](https://github.com/search?q=org%3Ajeffersonlab+topic%3Aace+topic%3Amanagement-app&type=repositories)
