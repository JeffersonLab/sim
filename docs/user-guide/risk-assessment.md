# Risk and SQA assessment

[User Guide](README.md) › Risk and SQA assessment

Each piece of software has one assessment: six risk inputs, which give its Software Quality
Assurance (SQA) control level and its attention band. Select the software's **Risk & SQA** cell
in the [Directory](directory.md) to open the **Risk & SQA Assessment** dialog. Anyone can open it
to read; members of the `acg` and `sim-admin` [roles](concepts.md#roles) can change it.

The dialog links to the two documents that define the terms: **SQAP §4.3**, the Accelerator
Controls Software Quality Assurance Plan's section on software control levels, and **Guidance**,
the group's software risk guidance, which defines each input's values.

## Making an assessment

1. Set the six **Risk Inputs**. As you change them, the dialog shows the derived SQA level, the
   fragility, the factors that set it, and the attention.
2. If the SQAP allows a documented exception to the derived level, pick the level in
   **Override** and give the reason in **Rationale**, up to 512 characters. An override needs a
   rationale.
3. Select **Update**. SIM saves the inputs, records you and the time as the assessor, and reloads
   the page. **OK** closes the dialog without saving.

To drop an override, set **Override** to **None** and select **Update**; the rationale goes with
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

Ops Impact is how much the accelerator's operation depends on the software; Critical means its
failure could injure someone or damage the machine. The other five are risk factors: what makes
the software likely to fail, or slow to fix when it does. The **Guidance** link has their full
definitions.

## SQA control level

The SQA control level says how much process the SQAP asks for when the software changes: 0
**Minimal**, 1 **Partial**, 2 **Significant** or 3 **Full**. SIM derives it from Ops Impact and
Code Complexity:

| Ops Impact \ Code Complexity | 1 Simple | 2 Low | 3 Medium | 4 High | 5 Complex |
|---|---|---|---|---|---|
| **1 Marginal** | 0 | 0 | 1 | 2 | 2 |
| **2 Low** | 0 | 0 | 1 | 2 | 2 |
| **3 Medium** | 1 | 1 | 2 | 3 | 3 |
| **4 High** | 2 | 2 | 3 | 3 | 3 |
| **5 Critical** | 3 | 3 | 3 | 3 | 3 |

This is the SQAP's three-by-three table, with each input's 1 and 2 counted as low, 3 as
moderate, and 4 and 5 as high, and Critical impact always Full.

An **override** replaces the derived level with another, for an exception the SQAP allows. The
Directory then shows the override with an asterisk, and its SQA Level filter uses the override.
The derived level is kept, so the dialog shows both.

The level is meant to be stable: it changes only when the software's impact or complexity does.

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
attention. Reassess the software when that happens.

## Not assessed

New software has every input at 3, or **Some** for Past Downtime Rate, and shows **Not assessed**
until someone selects **Update** in its dialog, even if they keep those values. Not-assessed
software comes last in the Directory, and the SQA Level, Attention and Risk Factors filters leave
it out. To find the software still to be assessed, set the Directory's **Assessed** filter to
**Not assessed**.
