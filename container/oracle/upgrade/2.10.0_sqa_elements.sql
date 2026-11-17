-- Add each product's checklist of SQA elements (JeffersonLab/sim#76): the elements the ACS SQAP
-- requires of a product, and what somebody recorded of each for a product. Run once, as SIM_OWNER
-- or a user that may create its tables. The definitions must match
-- container/oracle/initdb.d/02_ddl.sql, and the rows container/oracle/initdb.d/03_sqa_elements.sql,
-- which container/oracle/sqa_elements_sql.py writes from the SQAP's elements.json.

SET DEFINE OFF;

-- The SQA elements the ACS SQAP requires of a product as a whole, and at which control levels
-- (LEVEL_0 Minimal to LEVEL_3 Full). The rows come from the SQAP's elements.json
-- (03_sqa_elements.sql, written by container/oracle/sqa_elements_sql.py); ELEMENT_ID is the
-- SQAP's id for the element, which stays the same across its revisions.
CREATE TABLE SIM_OWNER.SQA_ELEMENT
(
    ELEMENT_ID     VARCHAR2(64 CHAR) NOT NULL,
    NAME           VARCHAR2(256 CHAR) NOT NULL,
    PHASE          VARCHAR2(64 CHAR) NOT NULL,
    SECTION_NUMBER VARCHAR2(16 CHAR) NOT NULL,
    SECTION_URL    VARCHAR2(256 CHAR) NOT NULL,
    WEIGHT         INTEGER NOT NULL,
    LEVEL_0        VARCHAR2(16 CHAR) NULL,
    LEVEL_1        VARCHAR2(16 CHAR) NULL,
    LEVEL_2        VARCHAR2(16 CHAR) NULL,
    LEVEL_3        VARCHAR2(16 CHAR) NULL,
    SQAP_VERSION   VARCHAR2(64 CHAR) NOT NULL,
    CONSTRAINT SQA_ELEMENT_PK PRIMARY KEY (ELEMENT_ID),
    CONSTRAINT SQA_ELEMENT_CK1 CHECK (LEVEL_0 IN ('REQUIRED', 'RECOMMENDED')),
    CONSTRAINT SQA_ELEMENT_CK2 CHECK (LEVEL_1 IN ('REQUIRED', 'RECOMMENDED')),
    CONSTRAINT SQA_ELEMENT_CK3 CHECK (LEVEL_2 IN ('REQUIRED', 'RECOMMENDED')),
    CONSTRAINT SQA_ELEMENT_CK4 CHECK (LEVEL_3 IN ('REQUIRED', 'RECOMMENDED'))
);

-- Whether a product has an element, as somebody recorded it. No row means not recorded, which is
-- not a gap: existing software comes under the SQAP at its next planned change.
CREATE TABLE SIM_OWNER.SOFTWARE_ELEMENT
(
    SOFTWARE_ID   INTEGER NOT NULL,
    ELEMENT_ID    VARCHAR2(64 CHAR) NOT NULL,
    STATUS        VARCHAR2(16 CHAR) NOT NULL,
    LINK_URL      VARCHAR2(512 CHAR) NULL,
    NOTE          VARCHAR2(512 CHAR) NULL,
    RECORDED_BY   VARCHAR2(64 CHAR) NOT NULL,
    RECORDED_DATE DATE NOT NULL,
    CONSTRAINT SOFTWARE_ELEMENT_PK PRIMARY KEY (SOFTWARE_ID, ELEMENT_ID),
    CONSTRAINT SOFTWARE_ELEMENT_FK1 FOREIGN KEY (SOFTWARE_ID) REFERENCES SIM_OWNER.SOFTWARE (SOFTWARE_ID) ON DELETE CASCADE,
    CONSTRAINT SOFTWARE_ELEMENT_FK2 FOREIGN KEY (ELEMENT_ID) REFERENCES SIM_OWNER.SQA_ELEMENT (ELEMENT_ID),
    CONSTRAINT SOFTWARE_ELEMENT_CK1 CHECK (STATUS IN ('MET', 'GAP', 'NOT_APPLICABLE')),
    -- Not applicable needs its reason
    CONSTRAINT SOFTWARE_ELEMENT_CK2 CHECK (STATUS <> 'NOT_APPLICABLE' OR NOTE IS NOT NULL)
);

insert into SIM_OWNER.SQA_ELEMENT (ELEMENT_ID, NAME, PHASE, SECTION_NUMBER, SECTION_URL, WEIGHT, LEVEL_0, LEVEL_1, LEVEL_2, LEVEL_3, SQAP_VERSION) values ('ratings-recorded', 'Ops Impact and Code Complexity recorded in the Software Directory', 'Requirements', '4.4.1', 'https://pages.jlab.org/acc/iac/docs/sqap/latest/04-software-development-life-cycle/#441-requirements', 1, 'REQUIRED', 'REQUIRED', 'REQUIRED', 'REQUIRED', 'draft 2d7bbf5 after 1.0.0');
insert into SIM_OWNER.SQA_ELEMENT (ELEMENT_ID, NAME, PHASE, SECTION_NUMBER, SECTION_URL, WEIGHT, LEVEL_0, LEVEL_1, LEVEL_2, LEVEL_3, SQAP_VERSION) values ('requirements-specification', 'Software Requirements Specification', 'Requirements', '4.4.1', 'https://pages.jlab.org/acc/iac/docs/sqap/latest/04-software-development-life-cycle/#441-requirements', 2, null, 'RECOMMENDED', 'REQUIRED', 'REQUIRED', 'draft 2d7bbf5 after 1.0.0');
insert into SIM_OWNER.SQA_ELEMENT (ELEMENT_ID, NAME, PHASE, SECTION_NUMBER, SECTION_URL, WEIGHT, LEVEL_0, LEVEL_1, LEVEL_2, LEVEL_3, SQAP_VERSION) values ('design-documented', 'Design, purpose and use documented in source code, README file or similar', 'Design', '4.4.2', 'https://pages.jlab.org/acc/iac/docs/sqap/latest/04-software-development-life-cycle/#442-design', 3, 'REQUIRED', 'REQUIRED', 'REQUIRED', 'REQUIRED', 'draft 2d7bbf5 after 1.0.0');
insert into SIM_OWNER.SQA_ELEMENT (ELEMENT_ID, NAME, PHASE, SECTION_NUMBER, SECTION_URL, WEIGHT, LEVEL_0, LEVEL_1, LEVEL_2, LEVEL_3, SQAP_VERSION) values ('design-specification', 'Software Design Specification', 'Design', '4.4.2', 'https://pages.jlab.org/acc/iac/docs/sqap/latest/04-software-development-life-cycle/#442-design', 4, null, 'RECOMMENDED', 'REQUIRED', 'REQUIRED', 'draft 2d7bbf5 after 1.0.0');
insert into SIM_OWNER.SQA_ELEMENT (ELEMENT_ID, NAME, PHASE, SECTION_NUMBER, SECTION_URL, WEIGHT, LEVEL_0, LEVEL_1, LEVEL_2, LEVEL_3, SQAP_VERSION) values ('acs-dependencies-recorded', 'Dependencies on other ACS products recorded in the build files or README', 'Design', '4.4.2', 'https://pages.jlab.org/acc/iac/docs/sqap/latest/04-software-development-life-cycle/#442-design', 5, null, 'REQUIRED', 'REQUIRED', 'REQUIRED', 'draft 2d7bbf5 after 1.0.0');
insert into SIM_OWNER.SQA_ELEMENT (ELEMENT_ID, NAME, PHASE, SECTION_NUMBER, SECTION_URL, WEIGHT, LEVEL_0, LEVEL_1, LEVEL_2, LEVEL_3, SQAP_VERSION) values ('source-documentation', 'Source code documentation', 'Development', '4.4.3', 'https://pages.jlab.org/acc/iac/docs/sqap/latest/04-software-development-life-cycle/#443-development', 6, 'RECOMMENDED', 'REQUIRED', 'REQUIRED', 'REQUIRED', 'draft 2d7bbf5 after 1.0.0');
insert into SIM_OWNER.SQA_ELEMENT (ELEMENT_ID, NAME, PHASE, SECTION_NUMBER, SECTION_URL, WEIGHT, LEVEL_0, LEVEL_1, LEVEL_2, LEVEL_3, SQAP_VERSION) values ('version-control', 'Version control', 'Development', '4.4.3', 'https://pages.jlab.org/acc/iac/docs/sqap/latest/04-software-development-life-cycle/#443-development', 7, 'RECOMMENDED', 'REQUIRED', 'REQUIRED', 'REQUIRED', 'draft 2d7bbf5 after 1.0.0');
insert into SIM_OWNER.SQA_ELEMENT (ELEMENT_ID, NAME, PHASE, SECTION_NUMBER, SECTION_URL, WEIGHT, LEVEL_0, LEVEL_1, LEVEL_2, LEVEL_3, SQAP_VERSION) values ('test-plan', 'Software Test Plan', 'Testing and Verification', '4.4.4', 'https://pages.jlab.org/acc/iac/docs/sqap/latest/04-software-development-life-cycle/#444-testing-and-verification', 8, null, null, 'RECOMMENDED', 'REQUIRED', 'draft 2d7bbf5 after 1.0.0');
insert into SIM_OWNER.SQA_ELEMENT (ELEMENT_ID, NAME, PHASE, SECTION_NUMBER, SECTION_URL, WEIGHT, LEVEL_0, LEVEL_1, LEVEL_2, LEVEL_3, SQAP_VERSION) values ('user-guide', 'User Guide', 'Deployment', '4.4.5', 'https://pages.jlab.org/acc/iac/docs/sqap/latest/04-software-development-life-cycle/#445-deployment', 9, null, null, 'REQUIRED', 'REQUIRED', 'draft 2d7bbf5 after 1.0.0');
insert into SIM_OWNER.SQA_ELEMENT (ELEMENT_ID, NAME, PHASE, SECTION_NUMBER, SECTION_URL, WEIGHT, LEVEL_0, LEVEL_1, LEVEL_2, LEVEL_3, SQAP_VERSION) values ('installed-version', 'Version number, found from the installed instance', 'Deployment', '4.4.5', 'https://pages.jlab.org/acc/iac/docs/sqap/latest/04-software-development-life-cycle/#445-deployment', 10, null, 'REQUIRED', 'REQUIRED', 'REQUIRED', 'draft 2d7bbf5 after 1.0.0');
insert into SIM_OWNER.SQA_ELEMENT (ELEMENT_ID, NAME, PHASE, SECTION_NUMBER, SECTION_URL, WEIGHT, LEVEL_0, LEVEL_1, LEVEL_2, LEVEL_3, SQAP_VERSION) values ('installed-read-only', 'Installed software read-only to its users', 'Deployment', '4.4.5', 'https://pages.jlab.org/acc/iac/docs/sqap/latest/04-software-development-life-cycle/#445-deployment', 11, null, 'REQUIRED', 'REQUIRED', 'REQUIRED', 'draft 2d7bbf5 after 1.0.0');
insert into SIM_OWNER.SQA_ELEMENT (ELEMENT_ID, NAME, PHASE, SECTION_NUMBER, SECTION_URL, WEIGHT, LEVEL_0, LEVEL_1, LEVEL_2, LEVEL_3, SQAP_VERSION) values ('issue-tracking', 'Issue Tracking', 'Maintenance', '4.4.6', 'https://pages.jlab.org/acc/iac/docs/sqap/latest/04-software-development-life-cycle/#446-maintenance', 12, null, 'RECOMMENDED', 'REQUIRED', 'REQUIRED', 'draft 2d7bbf5 after 1.0.0');
insert into SIM_OWNER.SQA_ELEMENT (ELEMENT_ID, NAME, PHASE, SECTION_NUMBER, SECTION_URL, WEIGHT, LEVEL_0, LEVEL_1, LEVEL_2, LEVEL_3, SQAP_VERSION) values ('two-maintainers', 'At least two maintainers listed in the Software Directory', 'Maintenance', '4.4.6', 'https://pages.jlab.org/acc/iac/docs/sqap/latest/04-software-development-life-cycle/#446-maintenance', 13, null, null, 'REQUIRED', 'REQUIRED', 'draft 2d7bbf5 after 1.0.0');
insert into SIM_OWNER.SQA_ELEMENT (ELEMENT_ID, NAME, PHASE, SECTION_NUMBER, SECTION_URL, WEIGHT, LEVEL_0, LEVEL_1, LEVEL_2, LEVEL_3, SQAP_VERSION) values ('dependencies-recorded', 'Dependencies and their versions recorded in the build files or README', 'Acquired Software', '6.1.5', 'https://pages.jlab.org/acc/iac/docs/sqap/latest/06-reviews-and-audits/#615-required-sqa-elements', 14, 'RECOMMENDED', 'REQUIRED', 'REQUIRED', 'REQUIRED', 'draft 2d7bbf5 after 1.0.0');
insert into SIM_OWNER.SQA_ELEMENT (ELEMENT_ID, NAME, PHASE, SECTION_NUMBER, SECTION_URL, WEIGHT, LEVEL_0, LEVEL_1, LEVEL_2, LEVEL_3, SQAP_VERSION) values ('versions-pinned', 'Versions pinned exactly, in a lock file where the build tool has one', 'Acquired Software', '6.1.5', 'https://pages.jlab.org/acc/iac/docs/sqap/latest/06-reviews-and-audits/#615-required-sqa-elements', 15, null, 'RECOMMENDED', 'REQUIRED', 'REQUIRED', 'draft 2d7bbf5 after 1.0.0');
insert into SIM_OWNER.SQA_ELEMENT (ELEMENT_ID, NAME, PHASE, SECTION_NUMBER, SECTION_URL, WEIGHT, LEVEL_0, LEVEL_1, LEVEL_2, LEVEL_3, SQAP_VERSION) values ('acquired-choice-recorded', 'Choice recorded: purpose, source, license, maintenance status', 'Acquired Software', '6.1.5', 'https://pages.jlab.org/acc/iac/docs/sqap/latest/06-reviews-and-audits/#615-required-sqa-elements', 16, null, 'RECOMMENDED', 'REQUIRED', 'REQUIRED', 'draft 2d7bbf5 after 1.0.0');
insert into SIM_OWNER.SQA_ELEMENT (ELEMENT_ID, NAME, PHASE, SECTION_NUMBER, SECTION_URL, WEIGHT, LEVEL_0, LEVEL_1, LEVEL_2, LEVEL_3, SQAP_VERSION) values ('advisories-watched', 'Advisories and end of support watched by the maintainer', 'Acquired Software', '6.1.5', 'https://pages.jlab.org/acc/iac/docs/sqap/latest/06-reviews-and-audits/#615-required-sqa-elements', 17, null, 'RECOMMENDED', 'REQUIRED', 'REQUIRED', 'draft 2d7bbf5 after 1.0.0');

COMMIT;
