-- Add each product's release workflow, and the SQA elements a workflow provides for all the
-- software released with it (JeffersonLab/sim#80). Run once, as SIM_OWNER or a user that may
-- create and alter its tables, after 2.10.0_sqa_elements.sql, whose SQA_ELEMENT table this refers
-- to, and 2.10.0_sqa_repository_elements.sql. The definitions must match
-- container/oracle/initdb.d/02_ddl.sql, and the rows container/oracle/initdb.d/03_workflows.sql.

SET DEFINE OFF;

CREATE SEQUENCE SIM_OWNER.WORKFLOW_ID
    INCREMENT BY 1
    START WITH 1
    NOCYCLE
    NOCACHE
    ORDER;

-- How software is released: the toolchains the ACS SQAP names in its section 1.1, and the legacy
-- Certified list's. Where software lives is its repository; a GitLab project may or may not use
-- the certified workflow. The syncs propose a workflow by its NAME (SyncService), and WEIGHT
-- orders the lists.
CREATE TABLE SIM_OWNER.WORKFLOW
(
    WORKFLOW_ID INTEGER NOT NULL,
    NAME        VARCHAR2(64 CHAR) NOT NULL,
    DESCRIPTION VARCHAR2(256 CHAR) NULL,
    WEIGHT      INTEGER NOT NULL,
    CONSTRAINT WORKFLOW_PK PRIMARY KEY (WORKFLOW_ID),
    CONSTRAINT WORKFLOW_AK1 UNIQUE (NAME)
);

-- How it is released; null if nobody has said and its sync couldn't tell
ALTER TABLE SIM_OWNER.SOFTWARE ADD (WORKFLOW_ID INTEGER NULL);
ALTER TABLE SIM_OWNER.SOFTWARE ADD CONSTRAINT SOFTWARE_FK2 FOREIGN KEY (WORKFLOW_ID) REFERENCES SIM_OWNER.WORKFLOW (WORKFLOW_ID);

-- The SQA elements a workflow provides for all the software released with it, such as a folder for
-- each installed version. A product's checklist shows such an element as met by its workflow, as it
-- does one its repository provides, unless somebody recorded a status for the product. NOTE says on
-- what basis the workflow provides it.
CREATE TABLE SIM_OWNER.WORKFLOW_ELEMENT
(
    WORKFLOW_ID INTEGER NOT NULL,
    ELEMENT_ID  VARCHAR2(64 CHAR) NOT NULL,
    NOTE        VARCHAR2(512 CHAR) NOT NULL,
    LINK_URL    VARCHAR2(512 CHAR) NULL,
    CONSTRAINT WORKFLOW_ELEMENT_PK PRIMARY KEY (WORKFLOW_ID, ELEMENT_ID),
    CONSTRAINT WORKFLOW_ELEMENT_FK1 FOREIGN KEY (WORKFLOW_ID) REFERENCES SIM_OWNER.WORKFLOW (WORKFLOW_ID),
    CONSTRAINT WORKFLOW_ELEMENT_FK2 FOREIGN KEY (ELEMENT_ID) REFERENCES SIM_OWNER.SQA_ELEMENT (ELEMENT_ID)
);

-- WORKFLOW
insert into SIM_OWNER.WORKFLOW (WORKFLOW_ID, NAME, DESCRIPTION, WEIGHT) values (SIM_OWNER.WORKFLOW_ID.nextval, 'Certified', 'The certified workflow on code.jlab.org: the release-workflow components, deployed to /cs/certified by ADM', 1);
insert into SIM_OWNER.WORKFLOW (WORKFLOW_ID, NAME, DESCRIPTION, WEIGHT) values (SIM_OWNER.WORKFLOW_ID.nextval, 'GitHub web application', 'Web applications on GitHub, released through GitHub Actions with the shared workflows', 2);
insert into SIM_OWNER.WORKFLOW (WORKFLOW_ID, NAME, DESCRIPTION, WEIGHT) values (SIM_OWNER.WORKFLOW_ID.nextval, 'CSUE', 'The Controls System UNIX Environment''s release procedure', 3);
insert into SIM_OWNER.WORKFLOW (WORKFLOW_ID, NAME, DESCRIPTION, WEIGHT) values (SIM_OWNER.WORKFLOW_ID.nextval, 'EPICS build system', 'The EPICS build system, for low level apps', 4);
insert into SIM_OWNER.WORKFLOW (WORKFLOW_ID, NAME, DESCRIPTION, WEIGHT) values (SIM_OWNER.WORKFLOW_ID.nextval, 'Legacy Certified', 'The legacy Certified repository: tar-file releases, installed in versioned directories on NFS', 5);

-- WORKFLOW_ELEMENT: one line for each element a workflow provides. To add one, copy a line and
-- change the element's id, the note and the workflow's name. 'versions-pinned' is left out on
-- purpose: whether a workflow guarantees it is not clear-cut, so it is recorded on each product.
insert into SIM_OWNER.WORKFLOW_ELEMENT (WORKFLOW_ID, ELEMENT_ID, NOTE, LINK_URL) select WORKFLOW_ID, 'installed-version', 'Each release is installed under /cs/certified in a folder for its version.', 'https://pages.jlab.org/acc/iac/docs/sqap/latest/08-configuration-management-and-change-control/' from SIM_OWNER.WORKFLOW where NAME = 'Certified';
insert into SIM_OWNER.WORKFLOW_ELEMENT (WORKFLOW_ID, ELEMENT_ID, NOTE, LINK_URL) select WORKFLOW_ID, 'installed-read-only', 'Releases are installed under /cs/certified by the deploying account, so they are read-only to their users.', 'https://pages.jlab.org/acc/iac/docs/sqap/latest/08-configuration-management-and-change-control/' from SIM_OWNER.WORKFLOW where NAME = 'Certified';
insert into SIM_OWNER.WORKFLOW_ELEMENT (WORKFLOW_ID, ELEMENT_ID, NOTE, LINK_URL) select WORKFLOW_ID, 'installed-version', 'Each version is a GitHub release.', null from SIM_OWNER.WORKFLOW where NAME = 'GitHub web application';
insert into SIM_OWNER.WORKFLOW_ELEMENT (WORKFLOW_ID, ELEMENT_ID, NOTE, LINK_URL) select WORKFLOW_ID, 'installed-read-only', 'A service account owns the installed software, so regular users see it as read-only.', null from SIM_OWNER.WORKFLOW where NAME = 'GitHub web application';
insert into SIM_OWNER.WORKFLOW_ELEMENT (WORKFLOW_ID, ELEMENT_ID, NOTE, LINK_URL) select WORKFLOW_ID, 'installed-version', 'Each release is installed in a directory named for its version; the create-release script tags the version in CVS.', null from SIM_OWNER.WORKFLOW where NAME = 'CSUE';
insert into SIM_OWNER.WORKFLOW_ELEMENT (WORKFLOW_ID, ELEMENT_ID, NOTE, LINK_URL) select WORKFLOW_ID, 'installed-version', 'Each release is installed in a directory named for its version, which the build tags in CVS.', null from SIM_OWNER.WORKFLOW where NAME = 'EPICS build system';
insert into SIM_OWNER.WORKFLOW_ELEMENT (WORKFLOW_ID, ELEMENT_ID, NOTE, LINK_URL) select WORKFLOW_ID, 'installed-version', 'Each version is installed in a directory of its own on NFS.', null from SIM_OWNER.WORKFLOW where NAME = 'Legacy Certified';
insert into SIM_OWNER.WORKFLOW_ELEMENT (WORKFLOW_ID, ELEMENT_ID, NOTE, LINK_URL) select WORKFLOW_ID, 'installed-read-only', 'A service account owns the installed software, so regular users see it as read-only.', null from SIM_OWNER.WORKFLOW where NAME = 'Legacy Certified';

-- Existing software gets the workflow its sync would propose for it, archived software included:
-- by its repository, and for GitLab by the certified group in its home URL, which is the project's
-- address. Other GitLab projects, GitHub's and software entered by hand keep none, for an editor
-- to set.
UPDATE SIM_OWNER.SOFTWARE
   SET WORKFLOW_ID = (SELECT WORKFLOW_ID FROM SIM_OWNER.WORKFLOW WHERE NAME = 'CSUE')
 WHERE REPOSITORY_ID = (SELECT REPOSITORY_ID FROM SIM_OWNER.REPOSITORY WHERE NAME = 'CSUE');
UPDATE SIM_OWNER.SOFTWARE
   SET WORKFLOW_ID = (SELECT WORKFLOW_ID FROM SIM_OWNER.WORKFLOW WHERE NAME = 'EPICS build system')
 WHERE REPOSITORY_ID = (SELECT REPOSITORY_ID FROM SIM_OWNER.REPOSITORY WHERE NAME = 'LLAPP');
UPDATE SIM_OWNER.SOFTWARE
   SET WORKFLOW_ID = (SELECT WORKFLOW_ID FROM SIM_OWNER.WORKFLOW WHERE NAME = 'Legacy Certified')
 WHERE REPOSITORY_ID = (SELECT REPOSITORY_ID FROM SIM_OWNER.REPOSITORY WHERE NAME = 'CERTIFIED');
UPDATE SIM_OWNER.SOFTWARE
   SET WORKFLOW_ID = (SELECT WORKFLOW_ID FROM SIM_OWNER.WORKFLOW WHERE NAME = 'Certified')
 WHERE REPOSITORY_ID = (SELECT REPOSITORY_ID FROM SIM_OWNER.REPOSITORY WHERE NAME = 'GITLAB')
   AND LOWER(HOME_URL) LIKE 'https://code.jlab.org/acc/ops/acs/certified/%';

COMMIT;
