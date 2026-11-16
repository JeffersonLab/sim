# Administration

[User Guide](README.md) › Administration

This page is for SIM administrators: members of the `sim-admin` role, who see the **Setup** tab.
Members of the `acg` role can edit software, assessments and syncs, but not the Setup tab; see
[Roles](concepts.md#roles).

## Setup tab

| Page | For |
|---|---|
| **Settings** | The app's settings; see [Settings](#settings). Select a row, then **Edit**, to change its value. |
| **Directory Cache** | SIM caches users' names, emails and roles from Keycloak, and clears the cache every night at midnight. Clear it, or look up a user or role, after a change in Keycloak. Users must log out and in again for a change to their own roles to apply. |

## Settings

**Setup › Settings** lists the settings with their descriptions, by tag. Some changes can break
the app or the Settings page itself, so that they can only be undone in the database. Changes
made in the database directly take effect after **Refresh Cache**, at the top right of the page,
or a redeploy; `IP_READ_FILTER_ENABLED` and `IP_READ_URL_PATTERN` always need a redeploy.

| Setting | Effect |
|---|---|
| `GITLAB_SYNC_EXCLUDE_CSV` | The GitLab groups and projects the GitLab sync leaves out; see [below](#leaving-gitlab-projects-out-of-the-sync). |
| `SECRET_STORE_FILE_PATH` | The file with the syncs' access tokens; see [Access tokens](#access-tokens). |
| `NOTIFICATION_ENABLED`, `NOTIFICATION_MESSAGE`, `NOTIFICATION_LINK_NAME`, `NOTIFICATION_LINK_URL` | The banner at the top of every page. |
| `HELP_CONTENT_CONTACT`, `HELP_TECHNICAL_CONTACT` | The contacts on the Help tab. |
| `HELP_DOC_CSV` | The Help tab's documentation links: `URL\|Label` items separated by commas (neither can contain a comma). |
| `ADMIN_ROLE_NAME` | The role whose members the Help tab lists as admins, to logged-in users, and who get its feedback emails. It doesn't decide who sees the Setup tab, which is always the `sim-admin` role. |
| `EMAIL_ENABLED` | Turns on the Help tab's feedback emails, SIM's only email. |
| `EMAIL_TESTING_ENABLED` | Sends emails to the members of the `testlead` role instead, for testing. |
| `EMAIL_SENDER_ADDRESS` | The sender of feedback emails; the from address is the user's, from Keycloak. |
| `EMAIL_DOMAIN_NAME` | Unused: emails go to the addresses in Keycloak. |
| `IP_READ_FILTER_ENABLED`, `IP_READ_ALLOWLIST_PATTERN`, `IP_READ_URL_PATTERN` | Whether users outside the allowed addresses must log in to view pages. |
| `SMOOTHNESS_CDN_ENABLED`, `SMOOTHNESS_SERVER` | Where the pages' shared scripts and styles come from. |

Server settings, such as the database connection and Keycloak, are environment variables and the
application server's configuration; see the [README](https://github.com/JeffersonLab/sim#configure).

### Leaving GitLab projects out of the sync

The GitLab sync reads every project in code.jlab.org's `acc` group and its subgroups, except
those under the paths in `GITLAB_SYNC_EXCLUDE_CSV`. Each path is a group, which leaves out its
subgroups and projects too, or a single project, such as `acc/scratch` or `acc/ops/demo`, as in
the project's address on code.jlab.org. Paths are not case sensitive. To leave nothing out, set
a path no project has, since a setting can't be empty.

Software already in SIM from a project you leave out shows as **Remove** on the next GitLab
sync.

## Access tokens

The GitHub and GitLab syncs call those sites' APIs with access tokens, which SIM reads from a
properties file, not the database:

```
GITHUB_ACCESS_TOKEN=...
GITLAB_ACCESS_TOKEN=...
```

`SECRET_STORE_FILE_PATH` gives the file's path; a relative path is relative to the home
directory of the user the application server runs as. SIM reads the file when it starts, so
restart or redeploy it after changing the file or the setting. Without a token, its sync shows
`GITHUB_ACCESS_TOKEN is not set` or `GITLAB_ACCESS_TOKEN is not set`; with one that has expired
or lacks access, it shows `Request failed with status code: 401`.

The other three syncs read the lab's web pages without a token, so they work only where the
application server can reach those pages.

## Repositories and topics

SIM has no page to add or change repositories or topics.

- **Repositories** are rows in the database's `REPOSITORY` table: a name, description and home
  URL, and whether it can be synced (`SYNC_YN`). A repository can only be synced if SIM has code
  for it, which it has for the five named in [What each sync reads](sync.md#what-each-sync-reads);
  any other repository with `SYNC_YN = 'Y'` shows an `Unknown Repository` error on its sync.
- **Topics** are added when someone types a new one in the Directory's dialog or a sync brings
  one in. A topic no software uses any more stays in the lists; remove it in the database.

## Help tab

The Help tab shows the version, the contacts, the administrators (to logged-in users), the
`HELP_DOC_CSV` links and a feedback form, which emails the members of the `ADMIN_ROLE_NAME`
role. To link this guide from it, add an item such as
`https://jeffersonlab.github.io/sim/latest/|User Guide`, the guide's website, or
`https://github.com/JeffersonLab/sim/blob/main/docs/user-guide/README.md|User Guide`, its copy on
GitHub, to `HELP_DOC_CSV`.
