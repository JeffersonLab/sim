alter session set container = XEPDB1;

-- Special characters such as the ampersand will result in prompt without this directive.
SET DEFINE OFF;

-- Settings
insert into SIM_OWNER.SETTING (KEY, VALUE, TYPE, DESCRIPTION, TAG, WEIGHT) values ('ADMIN_ROLE_NAME', 'sim-admin', 'STRING', 'App-specific Admin Role Name', 'AUTH', 1);
insert into SIM_OWNER.SETTING (KEY, VALUE, TYPE, DESCRIPTION, TAG, WEIGHT) values ('SMOOTHNESS_CDN', 'N', 'BOOLEAN', 'Smoothness weblib resources from CDN.  Defaults to No = serve files locally. CDN is for minified/combined files on shared Content Delivery Network (CDN) server - Nice for when multiple apps use same resources to have wsim cache.', 'CDN', 1);
insert into SIM_OWNER.SETTING (KEY, VALUE, TYPE, DESCRIPTION, TAG, WEIGHT) values ('SMOOTHNESS_SERVER', 'ace.jlab.org/cdn', 'STRING', 'Host name and port of content delivery network host for shared smoothness resources. Only used if SMOOTHNESS_LOCATION=CDN', 'CDN', 2);
insert into SIM_OWNER.SETTING (KEY, VALUE, TYPE, DESCRIPTION, TAG, WEIGHT) values ('SMOOTHNESS_VERSION', '4.11.0', 'STRING', 'Version of smoothness lib on CDN.  Only used if SMOOTHNESS_LOCATION=CDN', 'CDN', 3);

-- REPO
insert into SIM_OWNER.REPO (REPO_ID, NAME, DESCRIPTION, REPO_URL) values (SIM_OWNER.REPO_ID.nextval, 'GITHUB', 'Microsoft GitHub Cloud Hosted Dev Platform', 'https://github.com/jeffersonlab');
insert into SIM_OWNER.REPO (REPO_ID, NAME, DESCRIPTION, REPO_URL) values (SIM_OWNER.REPO_ID.nextval, 'CERTIFIED', 'Certified Software', 'https://devweb.acc.jlab.org/controls_web/cjs/CSD/csd.php');
insert into SIM_OWNER.REPO (REPO_ID, NAME, DESCRIPTION, REPO_URL) values (SIM_OWNER.REPO_ID.nextval, 'CSUE', 'Controls System UNIX Environment', 'https://opweb.acc.jlab.org/CSUEApps/csueTools/csueAppsWeb.php');
insert into SIM_OWNER.REPO (REPO_ID, NAME, DESCRIPTION, REPO_URL) values (SIM_OWNER.REPO_ID.nextval, 'LLAPP', 'Low Level Apps', '');

-- SOFTWARE
insert into SIM_OWNER.SOFTWARE (SOFTWARE_ID, NAME, TYPE, DESCRIPTION, REPO_ID, SOFTWARE_URL, MAINTAINER_USERNAME_CSV) values (SIM_OWNER.SOFTWARE_ID.nextval, 'SIM', 'APP', 'Aggregates software info into a directory', 1, 'https://github.com/jeffersonlab/sim', 'ryans');