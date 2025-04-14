# Template Usage

## Steps

### 1. Create new Repo with Template
When viewing the `smoothness-template` repo in GitHub, click the "Use this template" button to create a new repo

### 2. Search and Replace app name
Search the code of your new repo for `template` and replace with your app name.

### 3. Configure GitHub Repo Secrets

#### A. DockerHub
The included GitHub Action workflow by default will attempt to publish an image to the JeffersonLab DockerHub on app release.  For this to work you need to add two Action Secrets via the Repo Settings on GitHub for your new repo.  The names are:

- DOCKERHUB_USER
- DOCKERHUB_TOKEN

You must have a DockerHub account and be a member of the JeffersonLab DockerHub organization.   Use DockerHub web page and generate a Personal Access Token.

#### B. acctest.acc.jlab.org
The included GitHub Action workflow by default will attempt to automatically deploy your app to the Jefferson Lab `acctest.acc.jlab.org` test server on app release.  For this to work you need to add two Action Secrets via the Repo Settings on GitHub for your new repo.  The names are:

- DEPLOYER_USERNAME
- DEPLOYER_TOKEN

Reach out to me obtain these.  There is no self-service at the moment.  We use Red Hat Keycloak for security and the auto-deploy action authenticates with Keycloak (which doesn't currently provide self-service of Personal Access Tokens), so an admin must do this.  We'll need to perform some setup on the acctest.acc.jlab.org server as well at this time. 

### 4. Create Database Schema
The smoothness web apps generally rely on a Jefferson Lab provided Oracle database in the test environment (acctest.acc.jlab.org) and another in the production environment (ace.jlab.org).  Make a request to an admin to set these up.

### 5. Customize App Logo
Web apps are required to provide a favicon, which is that little icon that shows up in browser tabs.   It's a good idea to also have a matching logo, and Smoothness weblib simply requires as much.  If you do nothing you'll inherit the template bulls-eye icon, but ideally you create your own.

These are the files in question:
- https://github.com/JeffersonLab/smoothness-template/blob/main/src/main/webapp/resources/img/favicon.ico
- https://github.com/JeffersonLab/smoothness-template/blob/main/src/main/webapp/resources/img/logo32x32.png

I've often just used Microsoft Paint to draw a 32x32 pixel png image for the logo and then a free online favicion generator can be used to create an `.ico` file from that.  Here is an example: https://www.xiconeditor.com/

### 6. Customize README
Update the Overview and description in the README to explain what your app does.

Take a screenshot of your app and replace the file Screenshot.png that is referenced in the README.  The inherited screenshot is of the template app!  The file in question:

- https://github.com/JeffersonLab/smoothness-template/blob/main/Screenshot.png

