package org.jlab.sim.persistence.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.jlab.sim.persistence.entity.Software;
import org.jlab.sim.persistence.enumeration.ElementStatus;

/**
 * The SQA elements SIM computes from its own records of a product, so that nobody has to record
 * them in the checklist: that the product's Ops Impact and Code Complexity are recorded, which its
 * assessment is, and that it has at least two maintainers, which its Maintainers column lists.
 */
public final class ComputedElements {

  /** The SQAP's id for "Ops Impact and Code Complexity recorded in the Software Directory". */
  public static final String RATINGS_RECORDED = "ratings-recorded";

  /** The SQAP's id for "At least two maintainers listed in the Software Directory". */
  public static final String TWO_MAINTAINERS = "two-maintainers";

  /** How many maintainers the SQAP asks for. */
  private static final int MAINTAINERS = 2;

  /** The elements SIM computes; nobody records them. */
  public static final Set<String> IDS = Set.of(RATINGS_RECORDED, TWO_MAINTAINERS);

  private ComputedElements() {}

  /**
   * What SIM computes of an assessed product's elements. Only assessed software has a checklist, so
   * its ratings are always recorded.
   */
  public static List<Checklist.Finding> of(Software software) {
    List<Checklist.Finding> findings = new ArrayList<>();
    if (software.isAssessed()) {
      String date =
          new SimpleDateFormat("dd-MMM-yyyy", Locale.US).format(software.getAssessedDate());
      findings.add(
          new Checklist.Finding(
              RATINGS_RECORDED,
              ElementStatus.MET,
              "Assessed by " + software.getAssessedBy() + " on " + date));
    }
    findings.add(twoMaintainers(software.getMaintainerUsernameList()));
    return findings;
  }

  /** Met with two or more maintainers listed, a gap with fewer. */
  static Checklist.Finding twoMaintainers(List<String> maintainers) {
    if (maintainers.size() >= MAINTAINERS) {
      return new Checklist.Finding(
          TWO_MAINTAINERS, ElementStatus.MET, "Maintainers: " + String.join(", ", maintainers));
    }
    String note =
        maintainers.isEmpty() ? "No maintainers listed" : "Only " + maintainers.get(0) + " listed";
    return new Checklist.Finding(TWO_MAINTAINERS, ElementStatus.GAP, note);
  }
}
