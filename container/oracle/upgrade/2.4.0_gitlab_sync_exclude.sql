-- Add the setting that lists the GitLab groups and projects the GitLab sync leaves out, starting
-- with the acc/scratch group. Run once, as SIM_OWNER or a user that may insert into its tables.
-- The row must match container/oracle/initdb.d/04_settings.sql. Until it runs, the sync leaves
-- out nothing, as before.

insert into SIM_OWNER.SETTING (KEY, VALUE, TYPE, DESCRIPTION, TAG, WEIGHT) values ('GITLAB_SYNC_EXCLUDE_CSV', 'acc/scratch', 'CSV', 'CSV of GitLab paths the GitLab sync leaves out, each a group (with its subgroups and projects) or a project, such as acc/scratch or acc/ops/demo.  Not case sensitive.  To leave out none, set a path no project has.', 'SYNC', 1);

commit;
