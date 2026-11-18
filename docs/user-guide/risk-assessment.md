# Risk and SQA assessment

[User Guide](README.md) › Risk and SQA assessment

Each piece of software has one assessment: six risk inputs, which give its Software Quality
Assurance (SQA) control level and its attention band. Select the software's **Risk & SQA** cell
in the [Directory](directory.md) to open the **Risk & SQA Assessment** dialog. Anyone can open it
to read; members of the `acg` and `sim-admin` [roles](concepts.md#roles) can change it. Everyone
else sees only **Close**.

The dialog has two links: **SQAP §4.3**, the Accelerator Controls Software Quality Assurance
Plan's section on software control levels, and **Guidance**, which opens [Rating the risk
inputs](#rating-the-risk-inputs) in this guide.

## Making an assessment

1. Set the six **Risk Inputs**. As you change them, the dialog shows the derived SQA level, the
   fragility, the factors that set it, and the attention.
2. If the SQAP allows an exception to the derived level, pick the level in **Override** and give
   the reason in **Rationale**, up to 512 characters. An override needs a rationale.
3. Select **Save**. SIM saves the inputs, records you and the time as the assessor, and reloads
   the page. **Cancel**, the × in the title bar, or Esc closes the dialog without saving.

To drop an override, set **Override** to **None** and select **Save**; the rationale goes with
it.

The footer of the dialog says who last assessed the software, and when.

## Risk inputs

Each input is from 1 to 5; the dialog shows each value's name and number.

| Input | 1 | 2 | 3 | 4 | 5 | Used for |
|---|---|---|---|---|---|---|
| **Ops Impact** | Marginal | Low | Medium | High | Critical | SQA level, attention |
| **Code Complexity** | Simple | Low | Medium | High | Complex | SQA level, fragility |
| **Past Downtime Rate** | Rare | Few | Some | Many | Frequent | Fragility |
| **Debug/Test Difficulty** | Easy | Low | Medium | High | Hard | Fragility |
| **Documentation Gaps** | Few | Low | Medium | High | Many | Fragility |
| **Esotericism** | Common | Low | Medium | High | Esoteric | Fragility |

Ops Impact is how much the accelerator's operation depends on the software. The other five are
risk factors: what makes the software likely to fail, or slow to fix when it does.

## Rating the risk inputs

The risk that matters most is downtime: the software not working as operations need it, during
a run or during Scheduled Accelerator Maintenance. Rate each input for the software as it is now,
and reassess when the software or its use changes. Software not yet written, or a new version
that a coming operations program needs, can be rated too.

The SQAP defines the two inputs that give the control level, so this guide doesn't repeat them.
Its links go to the SQAP revision this release of SIM loads, a draft (19bcfba) whose text is on
code.jlab.org; the SQAP's website has only released revisions.

- **Ops Impact**: [SQAP §4.3.1](https://code.jlab.org/acc/iac/docs/sqap/-/blob/19bcfba/docs/04-software-development-life-cycle.md#431-ops-impact).
- **Code Complexity**: [SQAP §4.3.2](https://code.jlab.org/acc/iac/docs/sqap/-/blob/19bcfba/docs/04-software-development-life-cycle.md#432-code-complexity).

Who rates the software, and when, is in [SQAP
§4.3.4](https://code.jlab.org/acc/iac/docs/sqap/-/blob/19bcfba/docs/04-software-development-life-cycle.md#434-recording-the-control-level).
The other four inputs are SIM's own, and only set fragility and attention.

### Past Downtime Rate

How often the software has failed in operations; past failures are the best guide to future
ones. 1 **Rare** is software that has hardly ever failed, 5 **Frequent** software that fails
often. The software's issues labeled `downtime` are the record to count.

### Debug/Test Difficulty

How well tested the software is, and how hard it is to test and debug. Consider whether it has
unit and integration tests and a log; whether its code is modular, so that a problem can be
isolated; whether it is easy to trace what it is doing; and whether debugging needs special tools
or beam studies. Software that has run in production for a long time can count as tested in
place; new, untested software rates higher. 1 **Easy** is well tested and easy to debug, 5
**Hard** is poorly tested or hard to debug.

### Documentation Gaps

What documentation the software is missing: a README, API documentation (such as Javadoc,
Doxygen or Sphinx), a user guide, on-call notes, technical notes such as a design or
administration guide, and requirements. Poorly defined requirements are a risk of their own:
software that doesn't do what operations need is downtime too. 1 **Few** is comprehensive
documentation, 5 **Many** is sparse. The aim is that no knowledge lives only in someone's head.

### Esotericism

How much specialized knowledge the software hides, beyond what its documentation can pass on:
its subject domain, its programming language and libraries, its development environment and
tools, and its procedures and customers. The fewer people in ACG who understand it well enough
to troubleshoot it, the higher the rating; it is relative to ACG's staff, not to the world. 1
**Common** is widely understood, 5 **Esoteric** is software only one person knows. The aim is at
least two experts, a primary maintainer and a backup, and a few others familiar with its
technology and subject.

## SQA control level

The SQA control level says how much process the SQAP asks for when the software changes: 0
**Minimal**, 1 **Partial**, 2 **Significant** or 3 **Full**. SIM derives it from Ops Impact and
Code Complexity with the SQAP's [control level
table](https://code.jlab.org/acc/iac/docs/sqap/-/blob/19bcfba/docs/04-software-development-life-cycle.md#433-control-level-table),
and the dialog shows the result as you change them.

An **override** replaces the derived level with another, for an exception the SQAP allows. The
Directory then shows the override with an asterisk, and its SQA Level filter uses the override.
The derived level is kept, so the dialog shows both.

## Fragility and attention

**Fragility** is the worst of the five risk factors, from 1 **Robust** to 5 **Fragile**; the
dialog names the factors that set it. A single bad factor makes software fragile, however good
the others are.

**Attention** says where effort would most reduce the risk to operations. SIM derives it from Ops
Impact and fragility:

| Ops Impact \ Fragility | 1 Robust | 2 Low | 3 Medium | 4 High | 5 Fragile |
|---|---|---|---|---|---|
| **1 Marginal** | Low | Low | Low | Moderate | Moderate |
| **2 Low** | Low | Low | Moderate | Moderate | High |
| **3 Medium** | Low | Moderate | Moderate | High | High |
| **4 High** | Moderate | Moderate | High | High | Severe |
| **5 Critical** | Moderate | High | High | Severe | Severe |

Unlike the SQA level, attention is meant to fall as the software improves: documenting it,
adding tests or training a second maintainer lowers a factor, and with it the fragility and the
attention. Reassess the software when that happens. To choose what to work on next, filter the
Directory by attention, or by **Risk Factors** for software with any factor at 4 or worse.

## Not assessed

New software has every input at 3, or **Some** for Past Downtime Rate, and shows **Not assessed**
until someone selects **Save** in its dialog, even if they keep those values. Not-assessed
software comes last in the Directory, and the SQA Level, Attention and Risk Factors filters leave
it out. To find the software still to be assessed, set the Directory's **Assessed** filter to
**Not assessed**.
