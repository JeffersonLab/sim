# Concepts

[User Guide](README.md) › Concepts

## Software

Each entry in SIM is a piece of **software**, with:

| Field | What it is |
|---|---|
| **Name** | Its name, up to 128 characters. A name is unique within its repository; two repositories can each have software of the same name. |
| **Type** | `APP`, `LIB` (a library), `SCRIPT` or `PLUGIN`; see [Types](#types). |
| **Repository** | Where its code or release lives; see [Repositories](#repositories). |
| **Workflow** | How it is released, or none; see [Workflows](#workflows). |
| **Description** | What it does, up to 256 characters. |
| **Note** | Anything else worth knowing, up to 256 characters. A sync never changes it. |
| **Topics** | Tags such as `java`, `epics` or `web`; see [Topics](#topics). |
| **Maintainers** | The usernames of the people who look after it, separated by commas. |
| **Home URL** | Its home page, such as its GitHub or GitLab project; the Directory links its name there. |
| **Archived** | Whether it is retired; see [Archived software](#archived-software). |

Each piece of software also has a [risk assessment](#risk-and-sqa-assessment).

The name and repository identify the software: they can't be changed once it is added. To move
software to another repository or rename it, add it again and remove the old entry.

## Types

Software is hard to sort into types, and a type is useful only if everyone sorts the same way,
so take it as a best effort, decided by who the software is for and how it runs:

| Type | What it is |
|---|---|
| `APP` | Software for end users: a program with an entry point you run. Software started by a shared container to share resources, such as a web app, is an app too. |
| `SCRIPT` | A kind of app: small, narrowly focused, usually a single interpreted source file, run easily by power users. The line between an app and a script is blurry; when in doubt, choose `APP`. |
| `LIB` | Software for developers: code with no entry point of its own, meant to be reused by other software through its API. |
| `PLUGIN` | A kind of library that is added to or removed from an app easily, often by its users at run time, to customize it. |

How ACG sorts common cases:

| Software | Type | Why |
|---|---|---|
| A Java web app in Wildfly, or PHP in Apache | `APP` | It is for end users. Strictly it is a plugin to its server, but it is used as an app. |
| A desktop program started from JMenu | `APP` | It is for end users, and it is a program. |
| A Low Level Apps IOC "glue" app | `APP` | It combines libraries into a program. |
| A Java JAR that isn't executable, such as the jlog logbook API | `LIB` | It is for developers, to include in their code. |
| Low Level Apps code that other software links | `LIB` | It is reusable code for developers. |

Where one repository holds more than one kind of code, choose the type of its main purpose: an
app with a container build file for a quick start is still an `APP`. A repository that holds only
a container build file, or other infrastructure as code, is probably a `SCRIPT`.

## Repositories

A **repository** is a place software lives: a code host such as GitHub or GitLab, one of the
lab's software lists, or a shared directory. The **Repositories** tab lists them.

Some repositories can be **synced**: SIM reads the list of software from the source and shows how
it differs from SIM's, and an editor applies the differences. These are GitHub, GitLab, and the
lab's Certified, CSUE and Low Level Apps lists; see [What each sync reads](sync.md#what-each-sync-reads).
Software in other repositories is entered and kept up to date by hand. `ACE_GIT` and `HLA_GIT`,
old local Git repositories, keep the few entries they have: add no new software to them.

The same software can be in more than one repository, because some lab lists record where
software is deployed as well as where its code is kept: an app can be in CSUE for its deployment
and on GitHub for its code and issues, such as `fe_daq` in CSUE and `AI_SRF_OPS_fe_daq` on
GitHub. Each is a separate entry in SIM.

Administrators add repositories; see
[Repositories, workflows and topics](administration.md#repositories-workflows-and-topics).

## Workflows

A **workflow** is how software is released: how it is built, given a version and installed. Where
software lives and how it is released are two things: a project on GitLab may be released with
the certified workflow or without it. The **Workflows** tab lists them:

| Workflow | Is |
|---|---|
| **Certified** | The certified workflow on code.jlab.org: the release-workflow components, deployed to `/cs/certified` by ADM. |
| **GitHub web application** | Web applications on GitHub, released through GitHub Actions with the shared workflows. |
| **CSUE** | The Controls System UNIX Environment's release procedure. |
| **EPICS build system** | The EPICS build system, for low level apps. |
| **Legacy Certified** | The legacy Certified repository: tar-file releases, installed in versioned directories on NFS. |

Software has one workflow, or none: none means nobody has said, not that it has no release
procedure. An editor sets it in the Directory's [edit dialog](directory.md#editing), and a
[sync](sync.md#adding-software) proposes it for the software it adds where the repository tells:
CSUE, Low Level Apps and the Certified list each release their software one way, and a GitLab
project in the certified group uses the certified workflow. Other GitLab projects and GitHub
repositories start with none.

Each row of the Workflows tab has the workflow's name, with a **Software** link that opens the
[Directory](directory.md) filtered to it, its description, and the
[SQA elements it provides](sqa-elements.md#met-by-the-workflow-or-the-repository) for all the
software released with it, each with its basis. The last row, **None**, links to the software
whose workflow nobody has set.

Software that moves from the legacy Certified repository to code.jlab.org is added in `GITLAB`,
with the Certified workflow; archive its old entry in `CERTIFIED`, which keeps Legacy Certified.

Administrators add workflows; see
[Repositories, workflows and topics](administration.md#repositories-workflows-and-topics).

## Topics

**Topics** are lower-case tags that group software across repositories, such as the language it
is written in or the system it serves. A piece of software can have any number of topics, and the
Directory's filter finds software that has all of the topics you pick.

GitHub and GitLab projects bring their own topics with them when synced. In the Directory's edit
dialog you can pick existing topics or type a new one, up to 32 characters, which SIM then adds
to the list.

For software synced from GitHub or GitLab, the topics `app`, `lib`, `script` and `plugin` also
set its type when it is added; see [Adding software from a sync](sync.md#adding-software).

## Maintainers

**Maintainers** are usernames, such as `jdoe`, separated by commas. The Directory shows one per
line, and its **Maintainer Username** filter finds software whose maintainers contain what you
type, so `jdoe` finds `jdoe` alone or among others. SIM doesn't check that the usernames exist.

## Archived software

**Archived** software is retired or no longer maintained, but kept for the record. The Directory
leaves it out unless the **Include Archived** filter asks for it, and marks it with a 🕸 after
its name. A sync from GitHub or GitLab sets it from the project's archived flag; for other
software, an editor sets it.

## Risk and SQA assessment

Each piece of software has six **risk inputs**, each from 1 to 5: how much operations depend on
it (**Ops Impact**), and five factors that make it harder to keep running: **Code Complexity**,
**Past Downtime Rate**, **Debug/Test Difficulty**, **Documentation Gaps** and **Esotericism**.
From them SIM works out:

- the **SQA control level**, 0 Minimal to 3 Full, from Ops Impact and Code Complexity. This is the
  level of the Accelerator Controls Software Quality Assurance Plan (SQAP), which says how much
  process a change to the software needs. A documented exception can **override** it.
- **Fragility**, the worst of the five factors.
- **Attention**, Low, Moderate, High or Severe, from Ops Impact and Fragility: where improving
  documentation, testing or cross-training would most reduce the risk to operations.

Software starts **Not assessed**, with every input at its middle value, until someone saves an
assessment. See [Risk and SQA assessment](risk-assessment.md).

## SQA elements

The SQAP asks for certain **SQA elements** at each control level, such as a User Guide or version
control. Assessed software has a checklist of the elements its level requires or recommends, each
**Met**, a **Gap**, **Not applicable** or not recorded. An element that the software's workflow
or repository provides for all its software, such as version control, is met without being
recorded. See [SQA elements](sqa-elements.md).

## Roles

| Who | Can |
|---|---|
| Anyone, logged in or not | View the Directory, the Repositories and Workflows tabs, the sync pages, each assessment and checklist of SQA elements, and the Help tab. |
| Members of the `acg` role | Also add, edit and remove software, save assessments and checklists, and apply syncs. |
| Members of the `sim-admin` role | All of the above, and the **Setup** tab. |

Roles come from the lab's identity provider (Keycloak). Log in with the link at the top right of
any page. If a role was just given to you, log out and in again.
