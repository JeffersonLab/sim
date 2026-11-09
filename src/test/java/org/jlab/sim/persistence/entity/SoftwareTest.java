package org.jlab.sim.persistence.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

/** How the stored maintainer list splits into the usernames the pages show one per line. */
class SoftwareTest {

  private static List<String> usernames(String csv) {
    Software software = new Software();
    software.setMaintainerUsernameCsv(csv);
    return software.getMaintainerUsernameList();
  }

  @Test
  void commaSeparatedSplits() {
    assertEquals(List.of("bfreeman", "adamc"), usernames("bfreeman,adamc"));
  }

  @Test
  void spacesAndBlankEntriesAreDropped() {
    assertEquals(List.of("bfreeman", "adamc"), usernames(" bfreeman, adamc ,,"));
    assertEquals(List.of("bfreeman", "adamc"), usernames("bfreeman adamc"));
    assertEquals(List.of("bfreeman"), usernames(",bfreeman"));
  }

  @Test
  void noneIsEmpty() {
    assertEquals(List.of(), usernames(null));
    assertEquals(List.of(), usernames("  "));
  }
}
