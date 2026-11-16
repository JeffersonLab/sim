# Concepts

[User Guide](README.md) › Concepts

## Software

Each entry in SIM is a piece of **software**, with:

| Field | What it is |
|---|---|
| **Name** | Its name, up to 128 characters. A name is unique within its repository; two repositories can each have software of the same name. |
| **Type** | `APP`, `LIB` (a library), `SCRIPT` or `PLUGIN`. |
| **Repository** | Where its code or release lives; see [Repositories](#repositories). |
| **Description** | What it does, up to 256 characters. |
| **Note** | Anything else worth knowing, up to 256 characters. A sync never changes it. |
| **Topics** | Tags such as `java`, `epics` or `web`; see [Topics](#topics). |
| **Maintainers** | The usernames of the people who look after it, separated by commas. |
| **Home URL** | Its home page, such as its GitHub or GitLab project; the Directory links its name there. |
| **Archived** | Whether it is retired; see [Archived software](#archived-software). |

Each piece of software also has a [risk assessment](#risk-and-sqa-assessment).

The name and repository identify the software: they can't be changed once it is added. To move
software to another repository or rename it, add it again and remove the old entry.

## Repositories

A **repository** is a place software lives: a code host such as GitHub or GitLab, one of the
lab's software lists, or a shared directory. The **Repositories** tab lists them.

Some repositories can be **synced**: SIM reads the list of software from the source and shows how
it differs from SIM's, and an editor applies the differences. These are GitHub, GitLab, and the
lab's Certified, CSUE and Low Level Apps lists; see [What each sync reads](sync.md#what-each-sync-reads).
Software in other repositories is entered and kept up to date by hand.

Administrators add repositories; see [Repositories and topics](administration.md#repositories-and-topics).

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

## Roles

| Who | Can |
|---|---|
| Anyone, logged in or not | View the Directory, the Repositories tab, the sync pages, each assessment, and the Help tab. |
| Members of the `acg` role | Also add, edit and remove software, save assessments, and apply syncs. |
| Members of the `sim-admin` role | All of the above, and the **Setup** tab. |

Roles come from the lab's identity provider (Keycloak). Log in with the link at the top right of
any page. If a role was just given to you, log out and in again.
