# Software Inventory Manager User Guide

Software Inventory Manager (SIM) is a directory of the software that Jefferson Lab's Accelerator
Controls Group (ACG) builds and maintains: apps, libraries, scripts and plugins, wherever their
code lives. For each piece of software it records what it is, who maintains it and where to find
it, and an assessment of its risk to operations that gives its Software Quality Assurance (SQA)
control level. Software in GitHub, GitLab and the lab's other software lists is copied in by a
**sync**; the rest is entered by hand.

At JLab, SIM is at [ace.jlab.org/sim](https://ace.jlab.org/sim). Anyone can view it; log in to
make changes, if you have an [editor role](concepts.md#roles).

The [guide's website](https://jeffersonlab.github.io/sim/) has a copy for each release; pick the
version the Help tab shows. On GitHub, this guide describes the `main` branch, which may be newer
than the version you are using.

## Contents

1. [Concepts](concepts.md): software, repositories, topics, maintainers, archived software, the
   risk assessment and roles.
2. [Directory](directory.md): finding software, and adding, editing and removing it.
3. [Risk and SQA assessment](risk-assessment.md): the six risk inputs, and how they give the SQA
   control level and the attention band.
4. [Repositories and sync](sync.md): the Repositories tab, and copying software in from GitHub,
   GitLab and the lab's other lists.
5. [Administration](administration.md): the Setup tab, settings and the sync's access tokens. For
   administrators.

## Tabs at a glance

| Tab | What it is for |
|---|---|
| **Directory** | All the software, with filters; add, edit and assess it here. SIM opens on this tab. |
| **Repositories** | The places software lives, each with a link to its software and, if it can be synced, to its sync. |
| **Setup** | Administrators only: settings and the directory cache. |
| **Help** | Version, contacts, documentation and a feedback form. |

## Quick start

- **Find software.** On **Directory**, open the filter, give a name (`*` is a wildcard, as in
  `*epics*`), a maintainer's username, a topic or any of the other filters, and select
  **Apply**. Select a name to go to the software's home page. See [Finding
  software](directory.md#finding-software).
- **Record software that isn't synced.** On **Directory**, select **Add**, then give its name,
  type and repository, and anything else you know. See [Adding
  software](directory.md#adding-software).
- **Assess its risk.** Select the software's **Risk & SQA** cell, set the six inputs, and select
  **Update**. See [Risk and SQA assessment](risk-assessment.md).
- **Bring in new software from GitHub or GitLab.** On **Repositories**, select the arrow on the
  repository's row, check the differences, and apply them. See [Repositories and
  sync](sync.md).

## Questions and changes

Use the feedback form on the Help tab, or contact the content and technical contacts listed
there. To suggest a change to this guide, open an issue or pull request on
[GitHub](https://github.com/JeffersonLab/sim).
