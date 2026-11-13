# Repositories and sync

[User Guide](README.md) › Repositories and sync

## The Repositories tab

The **Repositories** tab lists the places software lives. Each row has:

- the repository's name, linked to its home page if it has one, and a **Software** link that
  opens the [Directory](directory.md) filtered to the repository;
- its description;
- a **Sync** arrow (→), which opens the repository's sync. The arrow is disabled for repositories
  that can't be synced, whose software is entered by hand.

## What a sync does

A sync reads the list of software from the repository's source, compares it with the software
SIM has in that repository, archived software included, and lists the differences for an editor
to apply. Nothing changes in SIM until someone applies them.

Opening the sync page reads the source again, so it shows the source as it is now, and a large
source takes a while to load. If SIM can't read the source, the page shows an error instead of
the table.

Software is matched by name, within the repository. The line above the table counts the records
read from the source and those in SIM, and how many are matched, to add, to remove and to update.
The rows come in that order:

| Row | Means | Button |
|---|---|---|
| Green | In the source but not in SIM. | **Add** |
| Remote value (green) above SIM's (red) | In both, but the source has changed. | **Update** |
| Red | In SIM but no longer in the source. | **Remove** |
| Plain | In both, and the same. | None |

Members of the `acg` and `sim-admin` [roles](concepts.md#roles) can apply each row with its
button, or all of them at once with **Apply All**, which first asks you to confirm how many it
will update, remove and add. Each applied row shows **Success!**, and **Apply All** shows
**Done!** when it has finished. Open the sync again to check that nothing is left.

Anyone else sees the same page, with the buttons disabled.

**Check the Remove rows before applying them.** **Remove** deletes the software from SIM, with
its note and its [risk assessment](risk-assessment.md), and can't be undone. A project that was
renamed shows as one Remove and one Add, and applying both loses the old entry's assessment and
note: add the note and assessment to the new entry before removing the old one. A project that
left the source, such as a GitHub repository that lost its `acg` topic, shows as Remove, and so
does software added to a synced repository by hand.

## What a sync changes

### Adding software

Added software gets the name, description, topics, home URL and archived flag the source gives,
and maintainers from the sources that list them (CSUE and Low Level Apps). Its type is:

- for GitHub and GitLab, from its topics: `app`, `lib`, `script` or `plugin`, the first of these
  it has in that order, or `APP` if it has none of them;
- for the Certified list, the section it is listed in (apps, libraries or scripts);
- for CSUE and Low Level Apps, `APP`.

Like any new software, it starts [not assessed](risk-assessment.md#not-assessed).

### Updating software

A sync compares, and **Update** copies from the source:

- the **description**. The comparison skips it when the source has none, but **Update** copies
  it anyway, so updating software for another reason, such as its topics, empties a description
  that only SIM has;
- the **topics**, when the source has any;
- the **home URL**, when the source has one;
- the **archived** flag, from GitHub and GitLab only.

It never changes the **type**, the **maintainers**, the **note** or the assessment, so edits to
those in SIM are kept. An edit to a field the source does have, such as a description, shows as
**Update** on the next sync, and applying it puts the source's value back: change those at the
source instead.

## What each sync reads

| Repository | Reads | Gives |
|---|---|---|
| `GITHUB` | The JeffersonLab organization's GitHub repositories with the topic `acg`. | Name, description, topics, home URL, archived. |
| `GITLAB` | The projects in the `acc` group on [code.jlab.org](https://code.jlab.org), with its subgroups, except the groups and projects an administrator [leaves out](administration.md#leaving-gitlab-projects-out-of-the-sync). | Name, description, topics, home URL, archived. |
| `CERTIFIED` | The Certified Software list's apps, libraries and scripts. | Name, description, home URL. |
| `CSUE` | The Controls System UNIX Environment apps page. | Name, description, maintainer (the directory owner), home URL. |
| `LLAPP` | The Low Level Apps listing. | Name and maintainer (the file owner) only. |

Topics are made lower case. To bring a GitHub repository into SIM, give it the `acg` topic; to
take a GitLab project out, an administrator adds its path to the sync's exclusions.
