package org.jlab.sim.persistence.view;

import static org.jlab.sim.persistence.enumeration.ElementRequirement.RECOMMENDED;
import static org.jlab.sim.persistence.enumeration.ElementRequirement.REQUIRED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
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

  private static List<String> ids(Checklist checklist) {
    return checklist.getItems().stream().map(item -> item.getElement().getElementId()).toList();
  }

  @Test
  void aLevelShowsWhatItRequiresThenWhatItRecommendsEachInTheSqapsOrder() {
    assertEquals(
        List.of("design-documented", "version-control"),
        ids(Checklist.of(ELEMENTS, ControlLevel.Minimal, List.of())));
    assertEquals(
        List.of("design-documented", "version-control", "design-specification"),
        ids(Checklist.of(ELEMENTS, ControlLevel.Partial, List.of())));
    assertEquals(
        List.of(
            "design-documented",
            "design-specification",
            "version-control",
            "user-guide",
            "test-plan"),
        ids(Checklist.of(ELEMENTS, ControlLevel.Significant, List.of())));
    assertEquals(
        List.of(
            "design-documented",
            "design-specification",
            "version-control",
            "test-plan",
            "user-guide"),
        ids(Checklist.of(ELEMENTS, ControlLevel.Full, List.of())));
  }

  @Test
  void nothingRecordedIsNotRecordedAndNotAGap() {
    Checklist checklist = Checklist.of(ELEMENTS, ControlLevel.Significant, List.of());

    assertFalse(checklist.isRecorded());
    assertEquals(0, checklist.getGapCount());
    assertEquals("0 of 4", checklist.getSummary());
  }

  @Test
  void theSummaryCountsRequiredElementsMetOfThoseThatApply() {
    Checklist checklist =
        Checklist.of(
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
        Checklist.of(
            ELEMENTS, ControlLevel.Significant, List.of(recorded("test-plan", ElementStatus.GAP)));

    assertEquals(1, checklist.getGapCount());
    assertEquals("0 of 4", checklist.getSummary());
  }

  @Test
  void whatWasRecordedAtAnotherLevelIsKeptOffTheChecklist() {
    // Recorded while the software was Significant; now it is Minimal, which asks for neither
    Checklist checklist =
        Checklist.of(
            ELEMENTS,
            ControlLevel.Minimal,
            List.of(
                recorded("user-guide", ElementStatus.GAP),
                recorded("design-specification", ElementStatus.MET)));

    assertEquals(List.of("design-documented", "version-control"), ids(checklist));
    assertFalse(checklist.isRecorded());
    assertEquals(0, checklist.getGapCount());
  }
}
