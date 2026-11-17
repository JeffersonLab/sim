alter session set container = XEPDB1;

-- The workflows software is released with, and the SQA elements each provides for all the software
-- released with it (JeffersonLab/sim#80). The certified workflow's are from the SQAP's section 8;
-- the others' are as the maintainer gave them on 2026-11-17. CSUE and the EPICS build system install
-- under developers' usernames, so they don't provide 'installed-read-only'. To change the
-- workflows or what one provides, change the rows here and in the database: SIM has no page for
-- it, as it has none for repositories. The syncs propose a workflow by its name (SyncService), so
-- a renamed workflow is proposed no more until the code follows.
SET DEFINE OFF;

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

-- The default data's one product is a web application on GitHub
update SIM_OWNER.SOFTWARE set WORKFLOW_ID = (select WORKFLOW_ID from SIM_OWNER.WORKFLOW where NAME = 'GitHub web application') where NAME = 'sim' and REPOSITORY_ID = (select REPOSITORY_ID from SIM_OWNER.REPOSITORY where NAME = 'GITHUB');

commit;
