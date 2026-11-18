-- Load the SQAP's elements of revision draft 19bcfba after 1.0.0 (acc/iac/docs/sqap!22), and link each to its
-- section in the SQAP's source at that revision on code.jlab.org (JeffersonLab/sim#91), in place of
-- the website's "latest", the released 1.0.0, which lacks most of the draft's elements.
--
-- The SQAP merged "Dependencies on other ACS products recorded ..." (acs-dependencies-recorded) into
-- "Dependencies recorded ..." (dependencies-recorded), which now covers acquired software and other
-- ACS products, and named the group of dependency elements "Dependencies". What was recorded of the
-- retired element moves to dependencies-recorded for a product with nothing recorded there; the
-- query below first lists the products that had both recorded, with different statuses, to review.
--
-- Run once, as SIM_OWNER, on a database at 2.11.0. Afterwards each SQA_ELEMENT row matches
-- container/oracle/initdb.d/03_sqa_elements.sql, which container/oracle/sqa_elements_sql.py writes.

SET DEFINE OFF;

update SIM_OWNER.SQA_ELEMENT set NAME = 'Ops Impact and Code Complexity recorded in the Software Directory', SHORT_NAME = 'Ratings recorded', PHASE = 'Requirements', SECTION_NUMBER = '4.4.1', SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/19bcfba/docs/04-software-development-life-cycle.md#441-requirements', WEIGHT = 1, LEVEL_0 = 'REQUIRED', LEVEL_1 = 'REQUIRED', LEVEL_2 = 'REQUIRED', LEVEL_3 = 'REQUIRED', SQAP_VERSION = 'draft 19bcfba after 1.0.0' where ELEMENT_ID = 'ratings-recorded';
update SIM_OWNER.SQA_ELEMENT set NAME = 'Software Requirements Specification', SHORT_NAME = 'Requirements specification', PHASE = 'Requirements', SECTION_NUMBER = '4.4.1', SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/19bcfba/docs/04-software-development-life-cycle.md#441-requirements', WEIGHT = 2, LEVEL_0 = null, LEVEL_1 = 'RECOMMENDED', LEVEL_2 = 'REQUIRED', LEVEL_3 = 'REQUIRED', SQAP_VERSION = 'draft 19bcfba after 1.0.0' where ELEMENT_ID = 'requirements-specification';
update SIM_OWNER.SQA_ELEMENT set NAME = 'Purpose and use documented in source code, README file or similar', SHORT_NAME = 'Purpose and use documented', PHASE = 'Design', SECTION_NUMBER = '4.4.2', SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/19bcfba/docs/04-software-development-life-cycle.md#442-design', WEIGHT = 3, LEVEL_0 = 'REQUIRED', LEVEL_1 = 'REQUIRED', LEVEL_2 = 'REQUIRED', LEVEL_3 = 'REQUIRED', SQAP_VERSION = 'draft 19bcfba after 1.0.0' where ELEMENT_ID = 'design-documented';
update SIM_OWNER.SQA_ELEMENT set NAME = 'Software Design Specification', SHORT_NAME = 'Design specification', PHASE = 'Design', SECTION_NUMBER = '4.4.2', SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/19bcfba/docs/04-software-development-life-cycle.md#442-design', WEIGHT = 4, LEVEL_0 = null, LEVEL_1 = 'RECOMMENDED', LEVEL_2 = 'REQUIRED', LEVEL_3 = 'REQUIRED', SQAP_VERSION = 'draft 19bcfba after 1.0.0' where ELEMENT_ID = 'design-specification';
update SIM_OWNER.SQA_ELEMENT set NAME = 'Source code documentation', SHORT_NAME = 'Source code documentation', PHASE = 'Development', SECTION_NUMBER = '4.4.3', SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/19bcfba/docs/04-software-development-life-cycle.md#443-development', WEIGHT = 5, LEVEL_0 = 'RECOMMENDED', LEVEL_1 = 'REQUIRED', LEVEL_2 = 'REQUIRED', LEVEL_3 = 'REQUIRED', SQAP_VERSION = 'draft 19bcfba after 1.0.0' where ELEMENT_ID = 'source-documentation';
update SIM_OWNER.SQA_ELEMENT set NAME = 'Version control', SHORT_NAME = 'Version control', PHASE = 'Development', SECTION_NUMBER = '4.4.3', SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/19bcfba/docs/04-software-development-life-cycle.md#443-development', WEIGHT = 6, LEVEL_0 = 'RECOMMENDED', LEVEL_1 = 'REQUIRED', LEVEL_2 = 'REQUIRED', LEVEL_3 = 'REQUIRED', SQAP_VERSION = 'draft 19bcfba after 1.0.0' where ELEMENT_ID = 'version-control';
update SIM_OWNER.SQA_ELEMENT set NAME = 'Software Test Plan', SHORT_NAME = 'Test plan', PHASE = 'Testing and Verification', SECTION_NUMBER = '4.4.4', SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/19bcfba/docs/04-software-development-life-cycle.md#444-testing-and-verification', WEIGHT = 7, LEVEL_0 = null, LEVEL_1 = null, LEVEL_2 = 'RECOMMENDED', LEVEL_3 = 'REQUIRED', SQAP_VERSION = 'draft 19bcfba after 1.0.0' where ELEMENT_ID = 'test-plan';
update SIM_OWNER.SQA_ELEMENT set NAME = 'User Guide', SHORT_NAME = 'User guide', PHASE = 'Deployment', SECTION_NUMBER = '4.4.5', SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/19bcfba/docs/04-software-development-life-cycle.md#445-deployment', WEIGHT = 8, LEVEL_0 = null, LEVEL_1 = null, LEVEL_2 = 'REQUIRED', LEVEL_3 = 'REQUIRED', SQAP_VERSION = 'draft 19bcfba after 1.0.0' where ELEMENT_ID = 'user-guide';
update SIM_OWNER.SQA_ELEMENT set NAME = 'Version number, found from the installed instance', SHORT_NAME = 'Installed version shown', PHASE = 'Deployment', SECTION_NUMBER = '4.4.5', SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/19bcfba/docs/04-software-development-life-cycle.md#445-deployment', WEIGHT = 9, LEVEL_0 = null, LEVEL_1 = 'REQUIRED', LEVEL_2 = 'REQUIRED', LEVEL_3 = 'REQUIRED', SQAP_VERSION = 'draft 19bcfba after 1.0.0' where ELEMENT_ID = 'installed-version';
update SIM_OWNER.SQA_ELEMENT set NAME = 'Installed software read-only to its users', SHORT_NAME = 'Read-only install', PHASE = 'Deployment', SECTION_NUMBER = '4.4.5', SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/19bcfba/docs/04-software-development-life-cycle.md#445-deployment', WEIGHT = 10, LEVEL_0 = null, LEVEL_1 = 'REQUIRED', LEVEL_2 = 'REQUIRED', LEVEL_3 = 'REQUIRED', SQAP_VERSION = 'draft 19bcfba after 1.0.0' where ELEMENT_ID = 'installed-read-only';
update SIM_OWNER.SQA_ELEMENT set NAME = 'Issue Tracking', SHORT_NAME = 'Issue tracking', PHASE = 'Maintenance', SECTION_NUMBER = '4.4.6', SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/19bcfba/docs/04-software-development-life-cycle.md#446-maintenance', WEIGHT = 11, LEVEL_0 = null, LEVEL_1 = 'RECOMMENDED', LEVEL_2 = 'REQUIRED', LEVEL_3 = 'REQUIRED', SQAP_VERSION = 'draft 19bcfba after 1.0.0' where ELEMENT_ID = 'issue-tracking';
update SIM_OWNER.SQA_ELEMENT set NAME = 'At least two maintainers listed in the Software Directory', SHORT_NAME = 'Two maintainers', PHASE = 'Maintenance', SECTION_NUMBER = '4.4.6', SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/19bcfba/docs/04-software-development-life-cycle.md#446-maintenance', WEIGHT = 12, LEVEL_0 = null, LEVEL_1 = null, LEVEL_2 = 'REQUIRED', LEVEL_3 = 'REQUIRED', SQAP_VERSION = 'draft 19bcfba after 1.0.0' where ELEMENT_ID = 'two-maintainers';
update SIM_OWNER.SQA_ELEMENT set NAME = 'Dependencies recorded in the build files or README: acquired software with its versions, and other ACS products, including their servers, schemas and web services', SHORT_NAME = 'Dependencies recorded', PHASE = 'Dependencies', SECTION_NUMBER = '6.1.5', SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/19bcfba/docs/06-reviews-and-audits.md#615-required-sqa-elements', WEIGHT = 13, LEVEL_0 = 'RECOMMENDED', LEVEL_1 = 'REQUIRED', LEVEL_2 = 'REQUIRED', LEVEL_3 = 'REQUIRED', SQAP_VERSION = 'draft 19bcfba after 1.0.0' where ELEMENT_ID = 'dependencies-recorded';
update SIM_OWNER.SQA_ELEMENT set NAME = 'Versions pinned exactly, in a lock file where the build tool has one', SHORT_NAME = 'Dependency versions pinned', PHASE = 'Dependencies', SECTION_NUMBER = '6.1.5', SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/19bcfba/docs/06-reviews-and-audits.md#615-required-sqa-elements', WEIGHT = 14, LEVEL_0 = null, LEVEL_1 = 'RECOMMENDED', LEVEL_2 = 'REQUIRED', LEVEL_3 = 'REQUIRED', SQAP_VERSION = 'draft 19bcfba after 1.0.0' where ELEMENT_ID = 'versions-pinned';
update SIM_OWNER.SQA_ELEMENT set NAME = 'Choice recorded: purpose, source, license, maintenance status', SHORT_NAME = 'Dependency choices recorded', PHASE = 'Dependencies', SECTION_NUMBER = '6.1.5', SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/19bcfba/docs/06-reviews-and-audits.md#615-required-sqa-elements', WEIGHT = 15, LEVEL_0 = null, LEVEL_1 = 'RECOMMENDED', LEVEL_2 = 'REQUIRED', LEVEL_3 = 'REQUIRED', SQAP_VERSION = 'draft 19bcfba after 1.0.0' where ELEMENT_ID = 'acquired-choice-recorded';
update SIM_OWNER.SQA_ELEMENT set NAME = 'Advisories and end of support watched by the maintainer', SHORT_NAME = 'Dependency advisories watched', PHASE = 'Dependencies', SECTION_NUMBER = '6.1.5', SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/19bcfba/docs/06-reviews-and-audits.md#615-required-sqa-elements', WEIGHT = 16, LEVEL_0 = null, LEVEL_1 = 'RECOMMENDED', LEVEL_2 = 'REQUIRED', LEVEL_3 = 'REQUIRED', SQAP_VERSION = 'draft 19bcfba after 1.0.0' where ELEMENT_ID = 'advisories-watched';

-- Products that recorded both, differently: dependencies-recorded keeps its own status, so review these
select s.NAME, a.STATUS as ACS_DEPENDENCIES, b.STATUS as DEPENDENCIES
  from SIM_OWNER.SOFTWARE_ELEMENT a
  join SIM_OWNER.SOFTWARE_ELEMENT b on b.SOFTWARE_ID = a.SOFTWARE_ID and b.ELEMENT_ID = 'dependencies-recorded'
  join SIM_OWNER.SOFTWARE s on s.SOFTWARE_ID = a.SOFTWARE_ID
 where a.ELEMENT_ID = 'acs-dependencies-recorded' and a.STATUS <> b.STATUS;

insert into SIM_OWNER.SOFTWARE_ELEMENT (SOFTWARE_ID, ELEMENT_ID, STATUS, LINK_URL, NOTE, RECORDED_BY, RECORDED_DATE)
select a.SOFTWARE_ID, 'dependencies-recorded', a.STATUS, a.LINK_URL, a.NOTE, a.RECORDED_BY, a.RECORDED_DATE
  from SIM_OWNER.SOFTWARE_ELEMENT a
 where a.ELEMENT_ID = 'acs-dependencies-recorded'
   and not exists (select 1 from SIM_OWNER.SOFTWARE_ELEMENT b
                    where b.SOFTWARE_ID = a.SOFTWARE_ID and b.ELEMENT_ID = 'dependencies-recorded');

delete from SIM_OWNER.SOFTWARE_ELEMENT where ELEMENT_ID = 'acs-dependencies-recorded';
delete from SIM_OWNER.REPOSITORY_ELEMENT where ELEMENT_ID = 'acs-dependencies-recorded';
delete from SIM_OWNER.WORKFLOW_ELEMENT where ELEMENT_ID = 'acs-dependencies-recorded';
delete from SIM_OWNER.SQA_ELEMENT where ELEMENT_ID = 'acs-dependencies-recorded';

COMMIT;
