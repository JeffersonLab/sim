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
where there is one: first those it **requires**, then those it **recommends**, each in the SQAP's
order. Under each element's name are its phase and a link to the section of the SQAP that
describes it.

The checklist holds the elements a product has or lacks as a whole, such as a Software
Requirements Specification, a User Guide, version control and issue tracking. What the SQAP asks
of each change, such as a code review or a deployment plan, is recorded in the change's merge
request and in ATLis, not here.

The elements and the levels that require them come from the SQAP itself, which publishes them as
data with each revision. The line under the table names the revision SIM has.

When the control level changes, the checklist changes with it. What was recorded of an element
the new level doesn't ask for is kept, and comes back if the level does.

## Met, by the repository

Some elements come with where the software lives, not with the software: every project on GitLab
is under version control and has an issue tracker. Such an element is kept once, for the
[repository](sync.md#the-repositories-tab), and is met for all the software in it without anyone
recording it. The Repositories tab lists what each repository provides.

In the dialog, the status of such an element reads **Met, by** and the repository's name, such
as **Met, by GITLAB**, where another reads **Not recorded**, with the basis under it.

A status recorded for the software replaces the repository's. If a project has its issue tracker
switched off, record a **Gap**; the dialog then says **Otherwise met by** the repository. To go
back to the repository's, set the status to **Met, by** the repository again.

**Met, by** a repository means the repository provides the element for everything in it, on the
basis shown. Nobody has looked at this software's.

## Recording an element

For each element, set:

| Field | Means |
|---|---|
| **Status** | **Met**: the software has the element. **Gap**: it should have it, and doesn't. **Not applicable**: the element doesn't apply to this software. **Not recorded**: nobody has said. |
| **Link** | For an element that is met, where it is: the document, the README, the guide's website. For a gap, the issue that tracks it. A web address that starts with `http://` or `https://`, up to 512 characters. Optional. |
| **Note** | Anything else, up to 512 characters. **Not applicable** needs a note that says why. |

Then select **Save**. SIM saves the checklist and reloads the page. **Cancel**, the × in the
title bar, or Esc closes the dialog without saving. A link or a note needs a status: to clear an
element, set it to **Not recorded**, or to **Met, by** its repository, and empty its link and
note.

Under each status is who last recorded the element, and when. Saving the checklist doesn't
change that for the elements you left as they were.

**Met** means somebody says the software has the element, not that the element is good enough:
SIM doesn't read the document behind the link.

## Not recorded is not a gap

The SQAP brings existing software under the plan at its next planned change, and asks for nothing
to be written before then for its own sake. So an empty checklist is not a finding: the Directory
shows **Not recorded**, as it shows **Not assessed** for software nobody has rated, and counts no
gaps. Record a **Gap** when the software has come under the plan and lacks the element.

What a repository provides doesn't change this: software whose checklist nobody has touched
shows **Not recorded**, though the dialog shows the elements its repository provides as met.

## In the Directory

The **Risk & SQA** cell shows, under the assessment, how many of the required elements are met,
such as **5 of 7**, and the number of gaps if there are any:

- The second number is the required elements that apply: those recorded as **Not applicable**
  are left out.
- Recommended elements are outside both numbers. A gap in one still counts as a gap.
- An element met by the repository counts as met.
- **Not recorded** means nobody has recorded any element of the checklist.

To find software with a gap, set the Directory's **Gap in SQA Element** filter to an element, or
to **Any element**. It finds software where the element is recorded as a gap and is on the
checklist at the software's level.

## Documentation Gaps

The assessment's **Documentation Gaps** input is still a rating that a person sets. The checklist
doesn't set it or change the fragility; use the checklist's gaps to judge it.
