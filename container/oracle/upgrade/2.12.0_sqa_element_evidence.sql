-- Add what a product's record of each SQA element links to, the SQAP's "evidence" for it, which the
-- SQA Elements dialog shows under the link, and load the SQAP's elements of revision draft 0e98a07
-- after 1.0.0 (acc/iac/docs/sqap!24), which gives it. That revision changes nothing else of the
-- elements: each keeps its name, label, phase, order and levels, and links to its section at the
-- new revision.
--
-- Run once, as SIM_OWNER, after 2.11.1_sqap_dependencies_merged.sql, which removes the element that
-- has no evidence. The definition must match container/oracle/initdb.d/02_ddl.sql, and afterwards
-- each SQA_ELEMENT row matches container/oracle/initdb.d/03_sqa_elements.sql, which
-- container/oracle/sqa_elements_sql.py writes.

SET DEFINE OFF;

ALTER TABLE SIM_OWNER.SQA_ELEMENT ADD EVIDENCE VARCHAR2(128 CHAR) NULL;

update SIM_OWNER.SQA_ELEMENT set EVIDENCE = 'The product''s assessment in the Software Directory, which records this itself', SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/0e98a07/docs/04-software-development-life-cycle.md#441-requirements', SQAP_VERSION = 'draft 0e98a07 after 1.0.0' where ELEMENT_ID = 'ratings-recorded';
update SIM_OWNER.SQA_ELEMENT set EVIDENCE = 'The specification, in the product''s repository or on its documentation website', SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/0e98a07/docs/04-software-development-life-cycle.md#441-requirements', SQAP_VERSION = 'draft 0e98a07 after 1.0.0' where ELEMENT_ID = 'requirements-specification';
update SIM_OWNER.SQA_ELEMENT set EVIDENCE = 'The README, or wherever else the product''s purpose and use are written', SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/0e98a07/docs/04-software-development-life-cycle.md#442-design', SQAP_VERSION = 'draft 0e98a07 after 1.0.0' where ELEMENT_ID = 'design-documented';
update SIM_OWNER.SQA_ELEMENT set EVIDENCE = 'The design document, in the product''s repository or on its documentation website', SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/0e98a07/docs/04-software-development-life-cycle.md#442-design', SQAP_VERSION = 'draft 0e98a07 after 1.0.0' where ELEMENT_ID = 'design-specification';
update SIM_OWNER.SQA_ELEMENT set EVIDENCE = 'The published API documentation, or the source directory whose code carries it', SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/0e98a07/docs/04-software-development-life-cycle.md#443-development', SQAP_VERSION = 'draft 0e98a07 after 1.0.0' where ELEMENT_ID = 'source-documentation';
update SIM_OWNER.SQA_ELEMENT set EVIDENCE = 'The product''s repository', SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/0e98a07/docs/04-software-development-life-cycle.md#443-development', SQAP_VERSION = 'draft 0e98a07 after 1.0.0' where ELEMENT_ID = 'version-control';
update SIM_OWNER.SQA_ELEMENT set EVIDENCE = 'The test plan, in the product''s repository or on its documentation website; not the pipeline or the tests', SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/0e98a07/docs/04-software-development-life-cycle.md#444-testing-and-verification', SQAP_VERSION = 'draft 0e98a07 after 1.0.0' where ELEMENT_ID = 'test-plan';
update SIM_OWNER.SQA_ELEMENT set EVIDENCE = 'The guide''s website, or the guide in the product''s repository', SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/0e98a07/docs/04-software-development-life-cycle.md#445-deployment', SQAP_VERSION = 'draft 0e98a07 after 1.0.0' where ELEMENT_ID = 'user-guide';
update SIM_OWNER.SQA_ELEMENT set EVIDENCE = 'Where an instance shows its version: About page, --version output, startup log, PV or install path', SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/0e98a07/docs/04-software-development-life-cycle.md#445-deployment', SQAP_VERSION = 'draft 0e98a07 after 1.0.0' where ELEMENT_ID = 'installed-version';
update SIM_OWNER.SQA_ELEMENT set EVIDENCE = 'The deployment script or procedure, which shows the account that installs each version and where', SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/0e98a07/docs/04-software-development-life-cycle.md#445-deployment', SQAP_VERSION = 'draft 0e98a07 after 1.0.0' where ELEMENT_ID = 'installed-read-only';
update SIM_OWNER.SQA_ELEMENT set EVIDENCE = 'The product''s issue tracker, such as its issues on code.jlab.org or GitHub', SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/0e98a07/docs/04-software-development-life-cycle.md#446-maintenance', SQAP_VERSION = 'draft 0e98a07 after 1.0.0' where ELEMENT_ID = 'issue-tracking';
update SIM_OWNER.SQA_ELEMENT set EVIDENCE = 'The product''s maintainers in the Software Directory, which records this itself', SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/0e98a07/docs/04-software-development-life-cycle.md#446-maintenance', SQAP_VERSION = 'draft 0e98a07 after 1.0.0' where ELEMENT_ID = 'two-maintainers';
update SIM_OWNER.SQA_ELEMENT set EVIDENCE = 'The build file that lists the dependencies, or the README''s section on them', SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/0e98a07/docs/06-reviews-and-audits.md#615-required-sqa-elements', SQAP_VERSION = 'draft 0e98a07 after 1.0.0' where ELEMENT_ID = 'dependencies-recorded';
update SIM_OWNER.SQA_ELEMENT set EVIDENCE = 'The lock file; where the build tool has none, the build file or README that gives exact versions', SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/0e98a07/docs/06-reviews-and-audits.md#615-required-sqa-elements', SQAP_VERSION = 'draft 0e98a07 after 1.0.0' where ELEMENT_ID = 'versions-pinned';
update SIM_OWNER.SQA_ELEMENT set EVIDENCE = 'The README''s section on dependencies, or the design document''s, with the four facts for each', SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/0e98a07/docs/06-reviews-and-audits.md#615-required-sqa-elements', SQAP_VERSION = 'draft 0e98a07 after 1.0.0' where ELEMENT_ID = 'acquired-choice-recorded';
update SIM_OWNER.SQA_ELEMENT set EVIDENCE = 'The Dependabot or scanning settings; without them, a note on who follows upstream advisories', SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/0e98a07/docs/06-reviews-and-audits.md#615-required-sqa-elements', SQAP_VERSION = 'draft 0e98a07 after 1.0.0' where ELEMENT_ID = 'advisories-watched';

ALTER TABLE SIM_OWNER.SQA_ELEMENT MODIFY EVIDENCE NOT NULL;

COMMIT;
