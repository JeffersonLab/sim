alter session set container = XEPDB1;

-- The SQA elements each repository provides for all its software (JeffersonLab/sim#77). A repository
-- that isn't here, or isn't in the database, provides none: CERTIFIED, the legacy Certified list,
-- waits on the SQAP saying whether its tar-file releases and Jira count, and HOME_DIR has nothing.
-- To change what a repository provides, change its rows here and in the database: SIM has no page
-- for it, as it has none for repositories.
SET DEFINE OFF;

insert into SIM_OWNER.REPOSITORY_ELEMENT (REPOSITORY_ID, ELEMENT_ID, NOTE, LINK_URL) select REPOSITORY_ID, 'version-control', 'Every project is a Git repository on GitHub.', null from SIM_OWNER.REPOSITORY where NAME = 'GITHUB';
insert into SIM_OWNER.REPOSITORY_ELEMENT (REPOSITORY_ID, ELEMENT_ID, NOTE, LINK_URL) select REPOSITORY_ID, 'issue-tracking', 'Every project has GitHub Issues.', null from SIM_OWNER.REPOSITORY where NAME = 'GITHUB';
insert into SIM_OWNER.REPOSITORY_ELEMENT (REPOSITORY_ID, ELEMENT_ID, NOTE, LINK_URL) select REPOSITORY_ID, 'version-control', 'Every project is a Git repository on code.jlab.org.', null from SIM_OWNER.REPOSITORY where NAME = 'GITLAB';
insert into SIM_OWNER.REPOSITORY_ELEMENT (REPOSITORY_ID, ELEMENT_ID, NOTE, LINK_URL) select REPOSITORY_ID, 'issue-tracking', 'Every project has GitLab issues.', null from SIM_OWNER.REPOSITORY where NAME = 'GITLAB';
insert into SIM_OWNER.REPOSITORY_ELEMENT (REPOSITORY_ID, ELEMENT_ID, NOTE, LINK_URL) select REPOSITORY_ID, 'version-control', 'CSUE applications are kept in CVS, used through wrapper scripts.', null from SIM_OWNER.REPOSITORY where NAME = 'CSUE';
insert into SIM_OWNER.REPOSITORY_ELEMENT (REPOSITORY_ID, ELEMENT_ID, NOTE, LINK_URL) select REPOSITORY_ID, 'version-control', 'Low Level Apps are kept in CVS, used through wrapper scripts.', null from SIM_OWNER.REPOSITORY where NAME = 'LLAPP';

commit;
