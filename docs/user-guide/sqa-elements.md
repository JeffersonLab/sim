# SQA elements

[User Guide](README.md) › SQA elements

The Accelerator Controls Software Quality Assurance Plan (SQAP) asks different things of software
at each SQA control level: a User Guide at Significant and Full, version control at Partial and
above, and so on. SIM keeps a checklist of these **SQA elements** for each piece of software, so
that you can see which of them it has, and find the software that lacks one.

The checklist follows the control level, so only [assessed](risk-assessment.md) software has
one. Select **SQA Elements** in the software's **Risk & SQA** cell in the
[Directory](directory.md) to open the **SQA Elements** dialog. Anyone can open it to read;
members of the `acg` and `sim-admin` [roles](concepts.md#roles) can change it. Everyone else sees
only **Close**.

## What is on the checklist

The dialog lists the elements the SQAP asks for at the software's control level, the override
where there is one: first those it **requires**, under a **Required** heading, then those it
**recommends**, under **Recommended**, each in the SQAP's order. Each heading counts its
elements, and so does the line above the table, so a change of level shows in the numbers.

Each element has the SQAP's short label, such as **Dependency choices recorded**; hover over it
for the SQAP's full wording, such as "Choice recorded: purpose, source, license, maintenance
status". Under the label are the element's phase and a link to the section of the SQAP that
describes it, in the revision SIM loaded the elements from. While that is a draft, the link opens
the SQAP's source on code.jlab.org at that revision: the SQAP's website publishes only released
revisions, and the latest of those may not have the draft's elements.

The checklist holds the elements a product has or lacks as a whole, such as a Software
Requirements Specification, a User Guide, version control and issue tracking. What the SQAP asks
of each change, such as a code review or a deployment plan, is recorded in the change's merge
request and in ATLis, not here.

The elements and the levels that require them come from the SQAP itself, which publishes them as
data with each revision. The line under the table names the revision SIM has.

When the control level changes, the checklist changes with it. What was recorded of an element
the new level doesn't ask for is kept, and comes back if the level does.

## Met, by the workflow or the repository

Some elements come with how the software is released or where it lives, not with the software:
the certified workflow installs each release in a folder for its version, and every project on
GitLab is under version control and has an issue tracker. Such an element is kept once, for the
[workflow](concepts.md#workflows) or the [repository](sync.md#the-repositories-tab), and is met
for all its software without anyone recording it. The Workflows and Repositories tabs list what
each provides.

In the dialog, the status of such an element reads **Met, by** and what provides it, such as
**Met, by the Certified workflow** or **Met, by GITLAB**, where another reads **Not recorded**,
with the basis under it. An element's status is, in order: what was recorded for the software;
otherwise met by its workflow or its repository; otherwise not recorded. Where both provide an
element, the dialog names the workflow.

A status recorded for the software replaces the provider's. If a project has its issue tracker
switched off, record a **Gap**; the dialog then says **Otherwise met by** the provider. To go
back to the provider's, set the status to **Met, by** the provider again.

**Met, by** a workflow or a repository means it provides the element for all its software, on
the basis shown. Nobody has looked at this software's.

What a workflow doesn't provide is not a gap. CSUE and the EPICS build system install software
under developers' usernames, so they don't provide **Read-only install** (installed software
read-only to its users): for software released with them the element stays **Not recorded** until
somebody records it.

Software gets what its workflow provides only once it has one: set it in the Directory's
[edit dialog](directory.md#editing), unless its sync did.

To keep a link or a note on such an element, type it: the status changes to **Met**, recorded for
the software, with the provider as **Otherwise met by**.

## Set by SIM

SIM sets two elements itself, from what it already records, and nobody records them:

- **Ratings recorded** (Ops Impact and Code Complexity recorded in the Software Directory) is met
  by the [assessment](risk-assessment.md), which every checklist comes from. The dialog names who
  assessed the software, and when.
- **Two maintainers** (at least two maintainers listed in the Software Directory) is met when the
  Directory's **Maintainers** lists two or more, and a gap when it lists fewer. Add a maintainer in
  the [edit dialog](directory.md#editing) to meet it.

Their status reads **Set by SIM** and can't be changed in the dialog. They count like any other
element, and the **Gap in SQA Element** filter finds software with fewer than two maintainers where
its level asks for two.

## Recording an element

For each element, set:

| Field | Means |
|---|---|
| **Status** | **Met**: the software has the element. **Gap**: it should have it, and doesn't. **Not applicable**: the element doesn't apply to this software. **Not recorded**: nobody has said. |
| **Link** | For an element that is met, where it is: the line under the link says what the SQAP expects there, such as "The lock file; where the build tool has none, the build file or README that gives exact versions". For a gap, the issue that tracks it. A web address that starts with `http://` or `https://`, up to 512 characters. Optional. |
| **Note** | Anything else, up to 512 characters. **Not applicable** needs a note that says why. |

Then select **Save**. SIM saves the checklist and reloads the page. **Cancel**, the × in the
title bar, or Esc closes the dialog without saving. A link or a note needs a status: to clear an
element, set it to **Not recorded**, or to **Met, by** its workflow or repository, and empty its
link and note.

Under each status is who last recorded the element, and when. Saving the checklist doesn't
change that for the elements you left as they were.

Once an element has a link, **Open** beside it opens the link in a new window, for anyone reading
the checklist, logged in or not.

The line under each link comes from the SQAP with the elements, and says where to look, not what
the element must contain: follow the element's section link for that. Several elements can have
the same link, as the four about dependencies do when one section of the README covers them. The
two elements SIM sets have no link, and no line.

**Met** means somebody says the software has the element, not that the element is good enough:
SIM doesn't read the document behind the link.

## Not recorded is not a gap

The SQAP brings existing software under the plan at its next planned change, and asks for nothing
to be written before then for its own sake. So an empty checklist is not a finding: the Directory
shows **Not recorded**, as it shows **Not assessed** for software nobody has rated, and counts no
gaps. Record a **Gap** when the software has come under the plan and lacks the element.

What a workflow or a repository provides doesn't change this: software whose checklist nobody
has touched shows **Not recorded**, though the dialog shows the elements they provide as met.

## In the Directory

The **Risk & SQA** cell shows, under the assessment, how many of the required elements are met,
such as **5 of 7**, and the number of gaps if there are any:

- The second number is the required elements that apply: those recorded as **Not applicable**
  are left out.
- Recommended elements are outside both numbers. A gap in one still counts as a gap.
- An element met by the workflow or the repository counts as met.
- **Not recorded** means nobody has recorded any element of the checklist.

To find software with a gap, set the Directory's **Gap in SQA Element** filter to an element, or
to **Any element**. It finds software where the element is recorded as a gap and is on the
checklist at the software's level.

## Documentation Gaps

The assessment's **Documentation Gaps** input is still a rating that a person sets. The checklist
doesn't set it or change the fragility; use the checklist's gaps to judge it.
