alter session set container = XEPDB1;

-- Special characters such as the ampersand will result in prompt without this directive.
SET DEFINE OFF;

-- REPO
insert into SIM_OWNER.REPOSITORY (REPOSITORY_ID, NAME, DESCRIPTION, HOME_URL) values (SIM_OWNER.REPOSITORY_ID.nextval, 'GITHUB', 'Microsoft GitHub Cloud Hosted Dev Platform with org=JeffersonLab and topic=acg', 'https://github.com/orgs/JeffersonLab/repositories?q=topic%3Aacg');
insert into SIM_OWNER.REPOSITORY (REPOSITORY_ID, NAME, DESCRIPTION, HOME_URL) values (SIM_OWNER.REPOSITORY_ID.nextval, 'CERTIFIED', 'Certified Software', 'https://devweb.acc.jlab.org/controls_web/cjs/CSD/csd.php');
insert into SIM_OWNER.REPOSITORY (REPOSITORY_ID, NAME, DESCRIPTION, HOME_URL) values (SIM_OWNER.REPOSITORY_ID.nextval, 'CSUE', 'Controls System UNIX Environment', 'https://opweb.acc.jlab.org/CSUEApps/csueTools/csueAppsWeb.php');
insert into SIM_OWNER.REPOSITORY (REPOSITORY_ID, NAME, DESCRIPTION, HOME_URL) values (SIM_OWNER.REPOSITORY_ID.nextval, 'LLAPP', 'Low Level Apps', 'https://devweb.acc.jlab.org/llapp.php');
insert into SIM_OWNER.REPOSITORY (REPOSITORY_ID, NAME, DESCRIPTION, HOME_URL) values (SIM_OWNER.REPOSITORY_ID.nextval, 'GITLAB', 'CNI GitLab install at code.jlab.org for group Accelerator', 'https://code.jlab.org/accelerator');
insert into SIM_OWNER.REPOSITORY (REPOSITORY_ID, NAME, DESCRIPTION, HOME_URL) values (SIM_OWNER.REPOSITORY_ID.nextval, 'HOME_DIR', 'Home directory software!', null);
insert into SIM_OWNER.REPOSITORY (REPOSITORY_ID, NAME, DESCRIPTION, HOME_URL) values (SIM_OWNER.REPOSITORY_ID.nextval, 'HLA_GIT', 'devl00:/usr/devsite/git local git repo (theo,ryans)', null);
insert into SIM_OWNER.REPOSITORY (REPOSITORY_ID, NAME, DESCRIPTION, HOME_URL) values (SIM_OWNER.REPOSITORY_ID.nextval, 'ACE_GIT', 'opsfs:/var/nis/git local git repo (adamc,cuffe,tsm)', null);

-- SOFTWARE
insert into SIM_OWNER.SOFTWARE (SOFTWARE_ID, NAME, TYPE, DESCRIPTION, REPOSITORY_ID, HOME_URL, MAINTAINER_USERNAME_CSV) values (SIM_OWNER.SOFTWARE_ID.nextval, 'sim', 'APP', 'Software Inventory Manager.  Aggregates software info into a phonebook-like directory', 1, 'https://github.com/jeffersonlab/sim', 'ryans');

-- TOPICS
insert into SIM_OWNER.TOPIC (TOPIC_ID, NAME) values (SIM_OWNER.TOPIC_ID.nextval, 'java');
insert into SIM_OWNER.TOPIC (TOPIC_ID, NAME) values (SIM_OWNER.TOPIC_ID.nextval, 'python');
insert into SIM_OWNER.TOPIC (TOPIC_ID, NAME) values (SIM_OWNER.TOPIC_ID.nextval, 'c++');
insert into SIM_OWNER.TOPIC (TOPIC_ID, NAME) values (SIM_OWNER.TOPIC_ID.nextval, 'app');
insert into SIM_OWNER.TOPIC (TOPIC_ID, NAME) values (SIM_OWNER.TOPIC_ID.nextval, 'lib');
insert into SIM_OWNER.TOPIC (TOPIC_ID, NAME) values (SIM_OWNER.TOPIC_ID.nextval, 'script');
insert into SIM_OWNER.TOPIC (TOPIC_ID, NAME) values (SIM_OWNER.TOPIC_ID.nextval, 'plugin');

-- SOFTWARE_TOPIC
insert into SIM_OWNER.SOFTWARE_TOPIC (SOFTWARE_ID, TOPIC_ID) values (1, 1);
insert into SIM_OWNER.SOFTWARE_TOPIC (SOFTWARE_ID, TOPIC_ID) values (1, 4);