package org.jlab.sim.persistence.view;

import static org.jlab.sim.persistence.enumeration.ElementRequirement.RECOMMENDED;
import static org.jlab.sim.persistence.enumeration.ElementRequirement.REQUIRED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.jlab.sim.persistence.entity.SoftwareElement;
import org.jlab.sim.persistence.entity.SqaElement;
import org.jlab.sim.persistence.enumeration.ControlLevel;
import org.jlab.sim.persistence.enumeration.ElementStatus;
import org.junit.jupiter.api.Test;

/** Which SQA elements a control level puts on a product's checklist, and how they are counted. */
class ChecklistTest {

  private static final BigInteger SOFTWARE_ID = BigInteger.ONE;

  /** As the SQAP has them: the requirements are for Minimal, Partial, Significant and Full. */
  private static final List<SqaElement> ELEMENTS =
      List.of(
          new SqaElement(
              "design-documented", "Design documented", 1, REQUIRED, REQUIRED, REQUIRED, REQUIRED),
          new SqaElement(
              "design-specification",
              "Design Specification",
              2,
              null,
              RECOMMENDED,
              REQUIRED,
              REQUIRED),
          new SqaElement(
              "version-control", "Version control", 3, RECOMMENDED, REQUIRED, REQUIRED, REQUIRED),
          new SqaElement("test-plan", "Software Test Plan", 4, null, null, RECOMMENDED, REQUIRED),
          new SqaElement("user-guide", "User Guide", 5, null, null, REQUIRED, REQUIRED));

  private static SoftwareElement recorded(String elementId, ElementStatus status) {
    return new SoftwareElement(SOFTWARE_ID, elementId, status);
  }

  /** A checklist of software whose repository provides nothing. */
  private static Checklist checklist(
      List<SqaElement> elements, ControlLevel level, List<SoftwareElement> recorded) {
    return Checklist.of(elements, level, recorded, List.of());
  }

  /** What a repository that keeps everything under version control provides. */
  private static final List<Checklist.Provision> GITLAB =
      List.of(
          new Checklist.Provision(
              "version-control", "GITLAB", "Every project is a Git repository", null));

  private static Checklist.Item item(Checklist checklist, String elementId) {
    return checklist.getItems().stream()
        .filter(item -> item.getElement().getElementId().equals(elementId))
        .findFirst()
        .orElseThrow();
  }

  private static List<String> ids(Checklist checklist) {
    return checklist.getItems().stream().map(item -> item.getElement().getElementId()).toList();
  }

  @Test
  void aLevelShowsWhatItRequiresThenWhatItRecommendsEachInTheSqapsOrder() {
    assertEquals(
        List.of("design-documented", "version-control"),
        ids(checklist(ELEMENTS, ControlLevel.Minimal, List.of())));
    assertEquals(
        List.of("design-documented", "version-control", "design-specification"),
        ids(checklist(ELEMENTS, ControlLevel.Partial, List.of())));
    assertEquals(
        List.of(
            "design-documented",
            "design-specification",
            "version-control",
            "user-guide",
            "test-plan"),
        ids(checklist(ELEMENTS, ControlLevel.Significant, List.of())));
    assertEquals(
        List.of(
            "design-documented",
            "design-specification",
            "version-control",
            "test-plan",
            "user-guide"),
        ids(checklist(ELEMENTS, ControlLevel.Full, List.of())));
  }

  @Test
  void nothingRecordedIsNotRecordedAndNotAGap() {
    Checklist checklist = checklist(ELEMENTS, ControlLevel.Significant, List.of());

    assertFalse(checklist.isRecorded());
    assertEquals(0, checklist.getGapCount());
    assertEquals("0 of 4", checklist.getSummary());
  }

  @Test
  void theSummaryCountsRequiredElementsMetOfThoseThatApply() {
    Checklist checklist =
        checklist(
            ELEMENTS,
            ControlLevel.Significant,
            List.of(
                recorded("design-documented", ElementStatus.MET),
                recorded("version-control", ElementStatus.MET),
                recorded("design-specification", ElementStatus.GAP),
                // Not applicable leaves the required elements one fewer
                recorded("user-guide", ElementStatus.NOT_APPLICABLE),
                // Recommended, so met or not it is outside the count
                recorded("test-plan", ElementStatus.MET)));

    assertTrue(checklist.isRecorded());
    assertEquals("2 of 3", checklist.getSummary());
    assertEquals(1, checklist.getGapCount());
  }

  @Test
  void aGapInARecommendedElementCountsAsAGap() {
    Checklist checklist =
        checklist(
            ELEMENTS, ControlLevel.Significant, List.of(recorded("test-plan", ElementStatus.GAP)));

    assertEquals(1, checklist.getGapCount());
    assertEquals("0 of 4", checklist.getSummary());
  }

  @Test
  void whatWasRecordedAtAnotherLevelIsKeptOffTheChecklist() {
    // Recorded while the software was Significant; now it is Minimal, which asks for neither
    Checklist checklist =
        checklist(
            ELEMENTS,
            ControlLevel.Minimal,
            List.of(
                recorded("user-guide", ElementStatus.GAP),
                recorded("design-specification", ElementStatus.MET)));

    assertEquals(List.of("design-documented", "version-control"), ids(checklist));
    assertFalse(checklist.isRecorded());
    assertEquals(0, checklist.getGapCount());
  }

  @Test
  void anElementTheRepositoryProvidesIsMetByItUnlessSomethingWasRecorded() {
    Checklist provided = Checklist.of(ELEMENTS, ControlLevel.Significant, List.of(), GITLAB);
    Checklist.Item versionControl = item(provided, "version-control");
    assertEquals(ElementStatus.MET, versionControl.getEffectiveStatus());
    assertEquals(null, versionControl.getStatus(), "nobody recorded it");
    assertEquals("GITLAB", versionControl.getProvision().getProvider());
    assertEquals(null, item(provided, "user-guide").getEffectiveStatus(), "not provided");
    assertEquals(null, item(provided, "user-guide").getProvision());

    // What was recorded for the product wins, and the provision is still there to return to
    Checklist recorded =
        Checklist.of(
            ELEMENTS,
            ControlLevel.Significant,
            List.of(recorded("version-control", ElementStatus.GAP)),
            GITLAB);
    assertEquals(ElementStatus.GAP, item(recorded, "version-control").getEffectiveStatus());
    assertEquals("GITLAB", item(recorded, "version-control").getProvision().getProvider());
    assertEquals(1, recorded.getGapCount());
    assertEquals("0 of 4", recorded.getSummary());
  }

  @Test
  void aProvidedElementCountsAsMetButDoesNotMakeTheChecklistRecorded() {
    Checklist untouched = Checklist.of(ELEMENTS, ControlLevel.Significant, List.of(), GITLAB);
    assertFalse(untouched.isRecorded(), "only a person's record does");
    assertEquals("1 of 4", untouched.getSummary());
    assertEquals(0, untouched.getGapCount());

    Checklist recorded =
        Checklist.of(
            ELEMENTS,
            ControlLevel.Significant,
            List.of(recorded("user-guide", ElementStatus.MET)),
            GITLAB);
    assertTrue(recorded.isRecorded());
    assertEquals("2 of 4", recorded.getSummary());
  }

  /**
   * What a workflow provides for all the software released with it, named as the dialog names it.
   */
  private static final List<Checklist.Provision> CERTIFIED_WORKFLOW =
      List.of(
          new Checklist.Provision(
              "user-guide", "the Certified workflow", "The template has a guide", null),
          new Checklist.Provision(
              "version-control", "the Certified workflow", "Releases are tagged", null));

  @Test
  void anElementTheWorkflowProvidesIsMetByItUnlessSomethingWasRecorded() {
    Checklist provided =
        Checklist.of(ELEMENTS, ControlLevel.Significant, List.of(), CERTIFIED_WORKFLOW);
    Checklist.Item userGuide = item(provided, "user-guide");
    assertEquals(ElementStatus.MET, userGuide.getEffectiveStatus());
    assertEquals(null, userGuide.getStatus(), "nobody recorded it");
    assertEquals("the Certified workflow", userGuide.getProvision().getProvider());
    assertEquals("2 of 4", provided.getSummary());
    assertFalse(provided.isRecorded(), "only a person's record does");

    Checklist recorded =
        Checklist.of(
            ELEMENTS,
            ControlLevel.Significant,
            List.of(recorded("user-guide", ElementStatus.NOT_APPLICABLE)),
            CERTIFIED_WORKFLOW);
    assertEquals(ElementStatus.NOT_APPLICABLE, item(recorded, "user-guide").getEffectiveStatus());
    assertEquals("1 of 3", recorded.getSummary());
  }

  @Test
  void theWorkflowsElementsAndTheRepositorysAreBothMetAndTheFirstNamesTheProvider() {
    // The workflow's, then the repository's, as the Directory lists them: both provide version
    // control here, and only the workflow a User Guide
    List<Checklist.Provision> both = new ArrayList<>(CERTIFIED_WORKFLOW);
    both.addAll(GITLAB);

    Checklist checklist = Checklist.of(ELEMENTS, ControlLevel.Significant, List.of(), both);

    assertEquals(
        "the Certified workflow", item(checklist, "version-control").getProvision().getProvider());
    assertEquals(
        "Releases are tagged", item(checklist, "version-control").getProvision().getNote());
    assertEquals(
        "the Certified workflow", item(checklist, "user-guide").getProvision().getProvider());
    assertEquals("2 of 4", checklist.getSummary());

    // The repository's alone still counts where the workflow provides nothing
    assertEquals(
        "GITLAB",
        item(Checklist.of(ELEMENTS, ControlLevel.Significant, List.of(), GITLAB), "version-control")
            .getProvision()
            .getProvider());
  }

  @Test
  void aProvisionOfAnElementTheLevelLeavesOutIsLeftOut() {
    Checklist checklist =
        Checklist.of(
            ELEMENTS,
            ControlLevel.Minimal,
            List.of(),
            List.of(new Checklist.Provision("user-guide", "GITLAB", "A guide for each", null)));

    assertEquals(List.of("design-documented", "version-control"), ids(checklist));
    assertEquals("0 of 1", checklist.getSummary());
  }
}
