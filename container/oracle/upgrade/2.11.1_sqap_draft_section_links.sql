-- Link each SQA element to its section in the SQAP's source at the loaded draft revision
-- (8905d54) on code.jlab.org, in place of the website's "latest", which is the last released
-- revision (1.0.0) and lacks most of the draft's elements. Run once, as SIM_OWNER, on a database at
-- 2.11.0. The links match container/oracle/initdb.d/03_sqa_elements.sql, which
-- container/oracle/sqa_elements_sql.py writes; nothing else changes.

SET DEFINE OFF;

update SIM_OWNER.SQA_ELEMENT set SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/8905d54/docs/04-software-development-life-cycle.md#441-requirements' where ELEMENT_ID = 'ratings-recorded';
update SIM_OWNER.SQA_ELEMENT set SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/8905d54/docs/04-software-development-life-cycle.md#441-requirements' where ELEMENT_ID = 'requirements-specification';
update SIM_OWNER.SQA_ELEMENT set SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/8905d54/docs/04-software-development-life-cycle.md#442-design' where ELEMENT_ID = 'design-documented';
update SIM_OWNER.SQA_ELEMENT set SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/8905d54/docs/04-software-development-life-cycle.md#442-design' where ELEMENT_ID = 'design-specification';
update SIM_OWNER.SQA_ELEMENT set SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/8905d54/docs/04-software-development-life-cycle.md#442-design' where ELEMENT_ID = 'acs-dependencies-recorded';
update SIM_OWNER.SQA_ELEMENT set SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/8905d54/docs/04-software-development-life-cycle.md#443-development' where ELEMENT_ID = 'source-documentation';
update SIM_OWNER.SQA_ELEMENT set SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/8905d54/docs/04-software-development-life-cycle.md#443-development' where ELEMENT_ID = 'version-control';
update SIM_OWNER.SQA_ELEMENT set SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/8905d54/docs/04-software-development-life-cycle.md#444-testing-and-verification' where ELEMENT_ID = 'test-plan';
update SIM_OWNER.SQA_ELEMENT set SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/8905d54/docs/04-software-development-life-cycle.md#445-deployment' where ELEMENT_ID = 'user-guide';
update SIM_OWNER.SQA_ELEMENT set SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/8905d54/docs/04-software-development-life-cycle.md#445-deployment' where ELEMENT_ID = 'installed-version';
update SIM_OWNER.SQA_ELEMENT set SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/8905d54/docs/04-software-development-life-cycle.md#445-deployment' where ELEMENT_ID = 'installed-read-only';
update SIM_OWNER.SQA_ELEMENT set SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/8905d54/docs/04-software-development-life-cycle.md#446-maintenance' where ELEMENT_ID = 'issue-tracking';
update SIM_OWNER.SQA_ELEMENT set SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/8905d54/docs/04-software-development-life-cycle.md#446-maintenance' where ELEMENT_ID = 'two-maintainers';
update SIM_OWNER.SQA_ELEMENT set SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/8905d54/docs/06-reviews-and-audits.md#615-required-sqa-elements' where ELEMENT_ID = 'dependencies-recorded';
update SIM_OWNER.SQA_ELEMENT set SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/8905d54/docs/06-reviews-and-audits.md#615-required-sqa-elements' where ELEMENT_ID = 'versions-pinned';
update SIM_OWNER.SQA_ELEMENT set SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/8905d54/docs/06-reviews-and-audits.md#615-required-sqa-elements' where ELEMENT_ID = 'acquired-choice-recorded';
update SIM_OWNER.SQA_ELEMENT set SECTION_URL = 'https://code.jlab.org/acc/iac/docs/sqap/-/blob/8905d54/docs/06-reviews-and-audits.md#615-required-sqa-elements' where ELEMENT_ID = 'advisories-watched';

COMMIT;
