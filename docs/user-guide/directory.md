# Directory

[User Guide](README.md) › Directory

The **Directory** tab lists the software, ten to a page, and is where editors add, change and
assess it. SIM opens on this tab.

## The table

| Column | Shows |
|---|---|
| **Name** | The name, linked to its home URL if it has one, with 🕸 if it is [archived](concepts.md#archived-software). Under it, the type and the repository. |
| **Description** | The description, then the note, then the topics. When there are five or more topics, the first three are shown and the rest are behind **+N more**; select it to show them all, and **less** to hide them again. |
| **Maintainers** | The maintainers' usernames, one per line. |
| **Risk & SQA** | The Ops Impact, the SQA level and the attention, or **Not assessed**. An asterisk after the SQA level means it is overridden; hover over it for the derived level and the rationale. Select the cell to open the [assessment](risk-assessment.md). |

The software is in order of risk: assessed software first, by Ops Impact from highest to lowest,
then by attention from highest to lowest, then by name; then the software nobody has assessed
yet, by name.

The line above the table says what the filter selects and how many match, such as
`All Software {1 - 10 of 245}`. Select **Previous** and **Next** to page through, and **Max Per
Page** to show 10 or 100 at a time.

## Finding software

Select **Choose...** at the top left of the page to open the filter, set what you want, and
select **Apply**. The filters combine: software must match all of them. **Clear** empties the
form; select **Apply** after it to see everything again.

| Filter | Finds |
|---|---|
| **Software Name** | Software with this name, ignoring case. Use `*` as a wildcard: `epics*` finds names that start with `epics`, and `*epics*` names that contain it. |
| **Maintainer Username** | Software whose maintainers contain this text, ignoring case. |
| **Repository** | Software in this repository. |
| **Type** | Software of this type. |
| **Topics** | Software with all of the topics you pick. |
| **Include Archived** | Blank leaves archived software out, as it is by default; **YES** includes it, and **EXCLUSIVELY** shows only archived software. |
| **SQA Level** | Software at this SQA level: the override where there is one, else the derived level. |
| **Attention** | Software with this attention. |
| **Risk Factors** | **Any factor ≥ 4**: software with at least one of the five risk factors at 4 or 5, that is, a fragility of High or Fragile. |
| **Assessed** | Software that has been assessed, or that hasn't. |

The SQA Level, Attention and Risk Factors filters only find assessed software, since SIM shows no
level or attention for software nobody has assessed.

The filter is part of the page's address, so you can bookmark a search or send it to someone.
The **Software** link beside each repository on the [Repositories](sync.md#the-repositories-tab)
tab opens the Directory filtered to that repository.

## Editing software

Members of the `acg` and `sim-admin` [roles](concepts.md#roles) see **Add**, **Edit** and
**Remove** buttons above the table. To edit or remove software, select its row first; **Unselect**
clears the selection.

### Adding software

Select **Add**, fill in the dialog, and select **Save**:

- **Name**, **Type** and **Repo** (the repository) are required. A name already used in that
  repository is refused.
- **Topics**: pick from the list, or type a new topic and press Enter.
- **Maintainers**: usernames separated by commas, such as `jdoe,jsmith`.
- **Archived**: **No** for software in use.

New software starts [not assessed](risk-assessment.md#not-assessed). Software in a [synced
repository](sync.md) is usually added by its sync instead; software you add by hand to a synced
repository shows as **Remove** on its next sync unless the source lists it too.

### Editing

Select the row, then **Edit**. You can change everything but the name and the repository; to
change either, add the software again and remove the old entry. Saving replaces the topics with
the ones in the dialog.

A sync may later change what you edit: see [What a sync changes](sync.md#what-a-sync-changes).
The note is never synced, so it is the place for anything you want kept.

### Removing

Select the row, then **Remove**, and confirm. The software, with its topics and its assessment,
is deleted; it can't be restored, only added again.

## Embedding the Directory

`/sim/directory?loose=Y` shows the Directory on its own, without SIM's header and tabs, to embed
in another page, such as a wiki, in a frame. It is always read-only, its software links open in
the whole window rather than the frame, and it takes the same filters as the Directory: set them
on the Directory, then add `&loose=Y` to its address. The risk assessments still open, read-only.
