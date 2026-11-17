-- Add the SQA elements each repository provides for all its software (JeffersonLab/sim#77). Run
-- once, as SIM_OWNER or a user that may create its tables, after 2.10.0_sqa_elements.sql, whose
-- SQA_ELEMENT table this refers to. The definition must match container/oracle/initdb.d/02_ddl.sql,
-- and the rows container/oracle/initdb.d/03_sqa_repository_elements.sql. A repository the database
-- doesn't have gets no rows.

SET DEFINE OFF;

-- The SQA elements a repository provides for all its software, such as version control for every
-- project on GitLab. A product's checklist shows such an element as met by its repository, unless
-- somebody recorded a status for the product. NOTE says on what basis the repository provides it.
CREATE TABLE SIM_OWNER.REPOSITORY_ELEMENT
(
    REPOSITORY_ID INTEGER NOT NULL,
    ELEMENT_ID    VARCHAR2(64 CHAR) NOT NULL,
    NOTE          VARCHAR2(512 CHAR) NOT NULL,
    LINK_URL      VARCHAR2(512 CHAR) NULL,
    CONSTRAINT REPOSITORY_ELEMENT_PK PRIMARY KEY (REPOSITORY_ID, ELEMENT_ID),
    CONSTRAINT REPOSITORY_ELEMENT_FK1 FOREIGN KEY (REPOSITORY_ID) REFERENCES SIM_OWNER.REPOSITORY (REPOSITORY_ID),
    CONSTRAINT REPOSITORY_ELEMENT_FK2 FOREIGN KEY (ELEMENT_ID) REFERENCES SIM_OWNER.SQA_ELEMENT (ELEMENT_ID)
);

insert into SIM_OWNER.REPOSITORY_ELEMENT (REPOSITORY_ID, ELEMENT_ID, NOTE, LINK_URL) select REPOSITORY_ID, 'version-control', 'Every project is a Git repository on GitHub.', null from SIM_OWNER.REPOSITORY where NAME = 'GITHUB';
insert into SIM_OWNER.REPOSITORY_ELEMENT (REPOSITORY_ID, ELEMENT_ID, NOTE, LINK_URL) select REPOSITORY_ID, 'issue-tracking', 'Every project has GitHub Issues.', null from SIM_OWNER.REPOSITORY where NAME = 'GITHUB';
insert into SIM_OWNER.REPOSITORY_ELEMENT (REPOSITORY_ID, ELEMENT_ID, NOTE, LINK_URL) select REPOSITORY_ID, 'version-control', 'Every project is a Git repository on code.jlab.org.', null from SIM_OWNER.REPOSITORY where NAME = 'GITLAB';
insert into SIM_OWNER.REPOSITORY_ELEMENT (REPOSITORY_ID, ELEMENT_ID, NOTE, LINK_URL) select REPOSITORY_ID, 'issue-tracking', 'Every project has GitLab issues.', null from SIM_OWNER.REPOSITORY where NAME = 'GITLAB';
insert into SIM_OWNER.REPOSITORY_ELEMENT (REPOSITORY_ID, ELEMENT_ID, NOTE, LINK_URL) select REPOSITORY_ID, 'version-control', 'CSUE applications are kept in CVS, used through wrapper scripts.', null from SIM_OWNER.REPOSITORY where NAME = 'CSUE';
insert into SIM_OWNER.REPOSITORY_ELEMENT (REPOSITORY_ID, ELEMENT_ID, NOTE, LINK_URL) select REPOSITORY_ID, 'version-control', 'Low Level Apps are kept in CVS, used through wrapper scripts.', null from SIM_OWNER.REPOSITORY where NAME = 'LLAPP';

COMMIT;
