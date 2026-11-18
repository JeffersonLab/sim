-- Add each SQA element's short label, which the Directory's checklist and filter, and the
-- Repositories and Workflows pages, show in place of the SQAP's full row. Run once, as SIM_OWNER, after
-- 2.10.0_sqa_elements.sql. The definition must match container/oracle/initdb.d/02_ddl.sql, and the
-- labels container/oracle/initdb.d/03_sqa_elements.sql, which container/oracle/sqa_elements_sql.py
-- writes from the SQAP's elements.json. The SQAP's tables are unchanged, and so are the other columns.

SET DEFINE OFF;

ALTER TABLE SIM_OWNER.SQA_ELEMENT ADD SHORT_NAME VARCHAR2(64 CHAR) NULL;

update SIM_OWNER.SQA_ELEMENT set SHORT_NAME = 'Ratings recorded', SQAP_VERSION = 'draft 152b43c after 1.0.0' where ELEMENT_ID = 'ratings-recorded';
update SIM_OWNER.SQA_ELEMENT set SHORT_NAME = 'Requirements specification', SQAP_VERSION = 'draft 152b43c after 1.0.0' where ELEMENT_ID = 'requirements-specification';
update SIM_OWNER.SQA_ELEMENT set SHORT_NAME = 'Design documented', SQAP_VERSION = 'draft 152b43c after 1.0.0' where ELEMENT_ID = 'design-documented';
update SIM_OWNER.SQA_ELEMENT set SHORT_NAME = 'Design specification', SQAP_VERSION = 'draft 152b43c after 1.0.0' where ELEMENT_ID = 'design-specification';
update SIM_OWNER.SQA_ELEMENT set SHORT_NAME = 'ACS dependencies recorded', SQAP_VERSION = 'draft 152b43c after 1.0.0' where ELEMENT_ID = 'acs-dependencies-recorded';
update SIM_OWNER.SQA_ELEMENT set SHORT_NAME = 'Source code documentation', SQAP_VERSION = 'draft 152b43c after 1.0.0' where ELEMENT_ID = 'source-documentation';
update SIM_OWNER.SQA_ELEMENT set SHORT_NAME = 'Version control', SQAP_VERSION = 'draft 152b43c after 1.0.0' where ELEMENT_ID = 'version-control';
update SIM_OWNER.SQA_ELEMENT set SHORT_NAME = 'Test plan', SQAP_VERSION = 'draft 152b43c after 1.0.0' where ELEMENT_ID = 'test-plan';
update SIM_OWNER.SQA_ELEMENT set SHORT_NAME = 'User guide', SQAP_VERSION = 'draft 152b43c after 1.0.0' where ELEMENT_ID = 'user-guide';
update SIM_OWNER.SQA_ELEMENT set SHORT_NAME = 'Installed version shown', SQAP_VERSION = 'draft 152b43c after 1.0.0' where ELEMENT_ID = 'installed-version';
update SIM_OWNER.SQA_ELEMENT set SHORT_NAME = 'Read-only install', SQAP_VERSION = 'draft 152b43c after 1.0.0' where ELEMENT_ID = 'installed-read-only';
update SIM_OWNER.SQA_ELEMENT set SHORT_NAME = 'Issue tracking', SQAP_VERSION = 'draft 152b43c after 1.0.0' where ELEMENT_ID = 'issue-tracking';
update SIM_OWNER.SQA_ELEMENT set SHORT_NAME = 'Two maintainers', SQAP_VERSION = 'draft 152b43c after 1.0.0' where ELEMENT_ID = 'two-maintainers';
update SIM_OWNER.SQA_ELEMENT set SHORT_NAME = 'Dependencies recorded', SQAP_VERSION = 'draft 152b43c after 1.0.0' where ELEMENT_ID = 'dependencies-recorded';
update SIM_OWNER.SQA_ELEMENT set SHORT_NAME = 'Versions pinned', SQAP_VERSION = 'draft 152b43c after 1.0.0' where ELEMENT_ID = 'versions-pinned';
update SIM_OWNER.SQA_ELEMENT set SHORT_NAME = 'Acquired: choice recorded', SQAP_VERSION = 'draft 152b43c after 1.0.0' where ELEMENT_ID = 'acquired-choice-recorded';
update SIM_OWNER.SQA_ELEMENT set SHORT_NAME = 'Advisories watched', SQAP_VERSION = 'draft 152b43c after 1.0.0' where ELEMENT_ID = 'advisories-watched';

-- An element the SQAP no longer has, which no row above named, keeps its full name as its label
update SIM_OWNER.SQA_ELEMENT set SHORT_NAME = SUBSTR(NAME, 1, 64) where SHORT_NAME is null;

ALTER TABLE SIM_OWNER.SQA_ELEMENT MODIFY SHORT_NAME NOT NULL;

COMMIT;
