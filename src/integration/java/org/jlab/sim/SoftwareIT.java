package org.jlab.sim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import java.io.StringReader;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Adding, editing, scoring and removing software, as the directory page's dialogs do, and who may.
 * Each test adds software with its own name to the HOME_DIR repository, which isn't synced, and
 * removes it afterwards.
 */
class SoftwareIT {

  /** Topics the tests share, so that reruns don't add more; sim keeps topics nobody uses */
  private static final String TOPIC_A = "integration-test-a";

  private static final String TOPIC_B = "integration-test-b";

  private static String repositoryId;

  private final List<String> added = new ArrayList<>();

  @BeforeAll
  static void awaitReady() throws Exception {
    Sim.awaitReady();

    Element option =
        Sim.page("/directory", null)
            .selectFirst("select[name=repositoryId] option:contains(HOME_DIR)");
    assertNotNull(option, "HOME_DIR repository");
    repositoryId = option.attr("value");
  }

  @AfterEach
  void removeAdded() throws Exception {
    String token = Sim.token(Sim.ADMIN);

    for (String name : added) {
      // Including archived software, which the directory leaves out by default
      Element row = row(name, "&archived=YES");
      if (row != null) {
        assertOk(
            Sim.post(
                "/ajax/remove-software",
                token,
                List.of(Map.entry("softwareId", row.attr("data-id")))));
      }
    }
  }

  @Test
  void editorAddsEditsScoresAndRemovesSoftware() throws Exception {
    String token = Sim.token(Sim.EDITOR);
    String name = newName();

    assertOk(add(token, name, "LIB", "N", TOPIC_B, TOPIC_A));

    Element row = row(name);
    assertNotNull(row, "added software is listed");
    assertEquals("LIB", row.attr("data-type"));
    assertEquals(TOPIC_A + "," + TOPIC_B, row.attr("data-topic-csv"), "topics, sorted");
    assertEquals("Added by an integration test", row.attr("data-description"));
    assertEquals("jdoe,tbrown", row.attr("data-maintainer-csv"));
    assertEquals(
        List.of("jdoe", "tbrown"),
        row.select("td.maintainers > div").eachText(),
        "maintainers, one per line");
    assertEquals("https://example.com/" + name, row.attr("data-url"));
    assertEquals(repositoryId, row.attr("data-repo-id"));
    assertEquals("N", row.attr("data-archived"));
    assertEquals("3", row.attr("data-impact"), "new software scores Medium");
    assertEquals("", row.attr("data-assessed-date"), "new software is not assessed");
    assertEquals("Not assessed", row.selectFirst("td.risk").text(), "said once, not per value");
    assertNull(impactCell(row), "no values before an assessment");
    assertEquals("LIB · HOME_DIR", row.selectFirst("td.name .name-meta").text());

    String softwareId = row.attr("data-id");

    assertOk(
        Sim.post(
            "/ajax/edit-software",
            token,
            List.of(
                Map.entry("softwareId", softwareId),
                Map.entry("repositoryId", repositoryId),
                Map.entry("type", "SCRIPT"),
                Map.entry("topicArray[]", TOPIC_A),
                Map.entry("description", "Edited by an integration test"),
                Map.entry("maintainerUsernameCsv", "jsmith"),
                Map.entry("homeUrl", "https://example.com/edited"),
                Map.entry("archived", "N"),
                Map.entry("note", "A note"))));

    row = row(name);
    assertEquals("SCRIPT", row.attr("data-type"));
    assertEquals(TOPIC_A, row.attr("data-topic-csv"));
    assertEquals("Edited by an integration test", row.attr("data-description"));
    assertEquals("jsmith", row.attr("data-maintainer-csv"));
    assertEquals("https://example.com/edited", row.attr("data-url"));
    assertEquals("A note", row.attr("data-note"));

    assertOk(
        Sim.post(
            "/ajax/edit-software-risk",
            token,
            List.of(
                Map.entry("softwareId", softwareId),
                Map.entry("impact", "5"),
                Map.entry("rate", "4"),
                Map.entry("difficulty", "1"),
                Map.entry("complexity", "2"),
                Map.entry("gaps", "5"),
                Map.entry("esotericism", "4"),
                Map.entry("levelOverride", "2"),
                Map.entry("levelNote", "Approved exception: no operational use"))));

    row = row(name);
    assertEquals("5", row.attr("data-impact"));
    assertEquals("4", row.attr("data-rate"));
    assertEquals("1", row.attr("data-difficulty"));
    assertEquals("2", row.attr("data-complexity"));
    assertEquals("5", row.attr("data-gaps"));
    assertEquals("4", row.attr("data-esotericism"));
    assertEquals("3", row.attr("data-level"), "Impact 5 is always Full");
    assertEquals("2", row.attr("data-level-override"));
    assertEquals("Approved exception: no operational use", row.attr("data-level-note"));
    assertEquals("5", row.attr("data-fragility"), "the worst factor, Documentation Gaps");
    assertEquals("4", row.attr("data-attention"), "Impact 5 and Fragility 5 is Severe");
    assertEquals(Sim.EDITOR, row.attr("data-assessed-by"));
    assertFalse(row.attr("data-assessed-date").isEmpty(), "assessed date");
    assertEquals("5 Critical", impactCell(row).text());
    assertEquals("2 Significant*", levelCell(row).text(), "the override, marked");
    assertEquals("Severe", attentionCell(row).text());
    assertEquals(
        "Ops Impact 5 Critical SQA Level 2 Significant* Attention Severe SQA Elements Not recorded",
        row.selectFirst("td.risk").text(),
        "each value labelled");

    // Dropping the override drops its rationale
    assertOk(
        Sim.post(
            "/ajax/edit-software-risk",
            token,
            List.of(
                Map.entry("softwareId", softwareId),
                Map.entry("impact", "5"),
                Map.entry("rate", "4"),
                Map.entry("difficulty", "1"),
                Map.entry("complexity", "2"),
                Map.entry("gaps", "5"),
                Map.entry("esotericism", "4"),
                Map.entry("levelOverride", ""),
                Map.entry("levelNote", "Approved exception: no operational use"))));

    row = row(name);
    assertEquals("", row.attr("data-level-override"));
    assertEquals("", row.attr("data-level-note"));
    assertEquals("3 Full", levelCell(row).text());

    assertOk(
        Sim.post("/ajax/remove-software", token, List.of(Map.entry("softwareId", softwareId))));

    assertNull(row(name), "removed software is not listed");
  }

  /**
   * The database derives the SQA control level from Ops Impact and Code Complexity, fragility as
   * the worst risk factor, and attention from Ops Impact and fragility, by the lookup tables in
   * 02_ddl.sql. The other four factors all get the same value here.
   */
  @ParameterizedTest(name = "impact {0}, complexity {1}, other factors {2}")
  @CsvSource({
    // impact, complexity, others, level, fragility, attention
    "1, 1, 1, 0, 1, 1",
    "1, 3, 1, 1, 3, 1",
    "2, 2, 5, 0, 5, 3", // low complexity but one esoteric, undocumented, fragile factor
    "2, 4, 1, 2, 4, 2",
    "3, 3, 3, 2, 3, 2", // the defaults
    "3, 4, 1, 3, 4, 3",
    "4, 1, 1, 2, 1, 2",
    "4, 2, 5, 2, 5, 4",
    "5, 1, 1, 3, 1, 2", // Impact 5 is always Full
  })
  void controlLevelFragilityAndAttentionFollowTheLookupTables(
      int impact, int complexity, int others, int level, int fragility, int attention)
      throws Exception {
    String token = Sim.token(Sim.EDITOR);
    String name = newName();
    assertOk(add(token, name, "APP", "N"));
    String softwareId = row(name).attr("data-id");

    assertOk(
        score(
            token,
            softwareId,
            impact,
            complexity,
            others,
            List.of(Map.entry("levelOverride", ""), Map.entry("levelNote", ""))));

    Element row = row(name);
    assertEquals(String.valueOf(level), row.attr("data-level"), "control level");
    assertEquals(String.valueOf(fragility), row.attr("data-fragility"), "fragility");
    assertEquals(String.valueOf(attention), row.attr("data-attention"), "attention");
    assertEquals("", row.attr("data-level-override"));
  }

  @Test
  void overrideNeedsARationaleAndALevel() throws Exception {
    String token = Sim.token(Sim.EDITOR);
    String name = newName();
    assertOk(add(token, name, "APP", "N"));
    String softwareId = row(name).attr("data-id");

    assertFail(
        score(
            token,
            softwareId,
            3,
            3,
            3,
            List.of(Map.entry("levelOverride", "1"), Map.entry("levelNote", " "))),
        "needs a rationale");
    assertFail(
        score(
            token,
            softwareId,
            3,
            3,
            3,
            List.of(Map.entry("levelOverride", "7"), Map.entry("levelNote", "Why"))),
        "Unknown value: 7");
    assertFail(
        score(
            token,
            softwareId,
            3,
            3,
            3,
            List.of(Map.entry("levelOverride", "x"), Map.entry("levelNote", "Why"))),
        "Unable to edit Software Risk");

    assertEquals("", row(name).attr("data-assessed-date"), "nothing was saved");
  }

  @Test
  void directoryFiltersByLevelAttentionFragilityAndAssessment() throws Exception {
    String token = Sim.token(Sim.EDITOR);
    String assessed = newName();
    String unassessed = newName();
    assertOk(add(token, assessed, "APP", "N"));
    assertOk(add(token, unassessed, "APP", "N"));
    String softwareId = row(assessed).attr("data-id");

    // Impact 5 and robust factors: level 3 Full, fragility 1, attention 2 Moderate
    assertOk(
        score(
            token,
            softwareId,
            5,
            1,
            1,
            List.of(Map.entry("levelOverride", ""), Map.entry("levelNote", ""))));

    assertNotNull(row(assessed, "&level=3"));
    assertNull(row(assessed, "&level=2"));
    assertNull(row(unassessed, "&level=2"), "unassessed software shows no level, even the default");
    assertNotNull(row(assessed, "&attention=2"));
    assertNull(row(assessed, "&attention=4"));
    assertNull(row(unassessed, "&attention=2"), "unassessed software shows no attention");
    assertNull(row(assessed, "&fragile=Y"), "no factor is 4 or worse");
    assertNotNull(row(assessed, "&assessed=Y"));
    assertNull(row(assessed, "&assessed=N"));
    assertNotNull(row(unassessed, "&assessed=N"));
    assertNull(row(unassessed, "&assessed=Y"));

    // An override is the level that applies, and one bad factor makes the software fragile
    assertOk(
        score(
            token,
            softwareId,
            5,
            1,
            4,
            List.of(Map.entry("levelOverride", "1"), Map.entry("levelNote", "An exception"))));

    assertNotNull(row(assessed, "&level=1"));
    assertNull(row(assessed, "&level=3"));
    assertNotNull(row(assessed, "&fragile=Y"));

    String message =
        Sim.page("/directory?level=1&fragile=Y&assessed=N", null).select(".message-box").text();
    assertTrue(message.contains("SQA Level \"1 Partial\""), message);
    assertTrue(message.contains("Any Risk Factor \u2265 4"), message);
    assertTrue(message.contains("Not Assessed"), message);
  }

  /**
   * The directory lists the most important software first: assessed software by Ops Impact, then by
   * Attention, then by name, and after it the software nobody has assessed.
   */
  @Test
  void directoryListsHighestOpsImpactFirst() throws Exception {
    String token = Sim.token(Sim.EDITOR);
    String prefix = newName();
    String unassessed = addNamed(token, prefix + "-a");
    String mediumRobust = addNamed(token, prefix + "-b");
    String critical = addNamed(token, prefix + "-c");
    String mediumFragile = addNamed(token, prefix + "-d");
    String criticalToo = addNamed(token, prefix + "-e");

    // Impact, complexity and the other factors: attention is Low for 3 with 1, High for 3 with 5,
    // and Moderate for 5 with 1
    scoreNamed(token, mediumRobust, 3, 1);
    scoreNamed(token, critical, 5, 1);
    scoreNamed(token, mediumFragile, 3, 5);
    scoreNamed(token, criticalToo, 5, 1);

    String filter = "/directory?softwareName=" + Sim.encodeParam(prefix + "-*");

    assertEquals(
        List.of(critical, criticalToo, mediumFragile, mediumRobust, unassessed),
        Sim.page(filter, null).select("tbody tr[data-name]").eachAttr("data-name"),
        "Ops Impact, then Attention, then name, then unassessed");

    assertEquals(
        List.of(mediumFragile, mediumRobust),
        Sim.page(filter + "&max=2&offset=2", null)
            .select("tbody tr[data-name]")
            .eachAttr("data-name"),
        "the second page of two carries on in the same order");
  }

  @Test
  void directoryShowsThreeTopicsAndTheRestBehindMore() throws Exception {
    String token = Sim.token(Sim.EDITOR);
    // Topics the tests share, so that reruns don't add more; listed sorted by name
    String[] topics = new String[7];
    for (int i = 0; i < topics.length; i++) {
      topics[i] = "integration-test-" + (char) ('a' + i);
    }
    String seven = newName();
    String five = newName();
    String four = newName();

    assertOk(add(token, seven, "APP", "N", topics));
    assertOk(add(token, five, "APP", "N", Arrays.copyOf(topics, 5)));
    assertOk(add(token, four, "APP", "N", Arrays.copyOf(topics, 4)));

    Element row = row(seven);
    assertEquals(List.of(topics), row.select("li.topic").eachText(), "every topic is on the page");
    assertEquals(
        List.of(topics).subList(3, 7),
        row.select("li.topic.extra-topic").eachText(),
        "topics after the third are hidden");
    Element more = row.selectFirst("button.topic-more");
    assertNotNull(more, "more button");
    assertEquals("+4 more", more.text());
    assertEquals("+4 more", more.attr("data-more-text"));

    row = row(five);
    assertEquals(2, row.select("li.topic.extra-topic").size(), "two of five are hidden");
    assertEquals("+2 more", row.selectFirst("button.topic-more").text());

    row = row(four);
    assertEquals(4, row.select("li.topic").size());
    assertTrue(
        row.select("li.topic.extra-topic").isEmpty(),
        "four topics are all shown: hiding one would save no room");
    assertNull(row.selectFirst("button.topic-more"), "no more button for four topics");
  }

  @Test
  void archivedSoftwareIsListedOnlyWhenIncluded() throws Exception {
    String name = newName();

    assertOk(add(Sim.token(Sim.EDITOR), name, "APP", "Y"));

    assertNull(row(name), "the directory leaves archived software out by default");
    assertEquals("Y", row(name, "&archived=YES").attr("data-archived"));
    assertNotNull(row(name, "&archived=EXCLUSIVELY"));
  }

  @Test
  void duplicateNameFails() throws Exception {
    String token = Sim.token(Sim.EDITOR);
    String name = newName();

    assertOk(add(token, name, "APP", "N"));

    assertFail(add(token, name, "APP", "N"), "make sure name doesn't already exist: " + name);
  }

  @Test
  void missingNameOrTypeFails() throws Exception {
    String token = Sim.token(Sim.EDITOR);

    assertFail(add(token, "", "APP", "N"), "Name cannot be empty");
    assertFail(add(token, newName(), "", "N"), "Type cannot be empty");
  }

  @Test
  void userAndAnonymousCannotEditSoftware() throws Exception {
    String name = newName();
    assertOk(add(Sim.token(Sim.EDITOR), name, "APP", "N"));
    String softwareId = row(name).attr("data-id");

    for (String token : new String[] {Sim.token(Sim.USER), null}) {
      assertFail(add(token, newName(), "APP", "N"), "Not authenticated / authorized");
      assertFail(
          Sim.post("/ajax/remove-software", token, List.of(Map.entry("softwareId", softwareId))),
          "Not authenticated / authorized");
    }

    assertNotNull(row(name), "the software is still listed");
  }

  /**
   * An editor records which SQA elements a product has, and the directory counts the required ones
   * that are met. The elements are the SQAP's, by their ids, which never change.
   */
  @Test
  void editorRecordsSqaElementsAndTheDirectoryCountsThem() throws Exception {
    String token = Sim.token(Sim.EDITOR);
    String name = newName();
    assertOk(add(token, name, "APP", "N"));
    String softwareId = row(name).attr("data-id");

    assertNull(elementsCell(row(name)), "unassessed software has no level to choose elements by");

    // Impact 3 and Complexity 3: level 2 Significant
    assertOk(
        score(
            token,
            softwareId,
            3,
            3,
            3,
            List.of(Map.entry("levelOverride", ""), Map.entry("levelNote", ""))));

    assertEquals("Not recorded", elementsCell(row(name)).text(), "nothing recorded is not a gap");
    Map<String, JsonObject> checklist = checklist(row(name));
    long required =
        checklist.values().stream()
            .filter(item -> "REQUIRED".equals(item.getString("requirement")))
            .count();
    assertEquals("REQUIRED", checklist.get("user-guide").getString("requirement"));
    assertEquals("RECOMMENDED", checklist.get("test-plan").getString("requirement"));
    assertFalse(checklist.get("user-guide").containsKey("status"));

    assertOk(
        record(
            token,
            softwareId,
            new String[] {"version-control", "MET", "https://example.com/repo", ""},
            new String[] {"user-guide", "GAP", "https://example.com/issues/1", " Being written "},
            new String[] {"requirements-specification", "NOT_APPLICABLE", "", "A wrapper script"},
            new String[] {"test-plan", "MET", "", ""}));

    // Not applicable leaves one fewer required element, and a recommended one is outside the count
    assertEquals(
        (1 + computedMet(row(name))) + " of " + (required - 1) + " (1 gap)",
        elementsCell(row(name)).text(),
        "required met");
    checklist = checklist(row(name));
    JsonObject versionControl = checklist.get("version-control");
    assertEquals("MET", versionControl.getString("status"));
    assertEquals("https://example.com/repo", versionControl.getString("link"));
    assertEquals(Sim.EDITOR, versionControl.getString("by"));
    assertEquals("Being written", checklist.get("user-guide").getString("note"));
    assertEquals("A wrapper script", checklist.get("requirements-specification").getString("note"));

    // A status that didn't change keeps who recorded it; one that did is the new recorder's, and
    // a blank status clears what was recorded. Elements left out stay as they are.
    assertOk(
        record(
            Sim.token(Sim.ADMIN),
            softwareId,
            new String[] {"version-control", "MET", "https://example.com/repo", ""},
            new String[] {"user-guide", "MET", "https://example.com/guide", ""},
            new String[] {"requirements-specification", "", "", ""}));

    assertEquals((2 + computedMet(row(name))) + " of " + required, elementsCell(row(name)).text());
    checklist = checklist(row(name));
    assertEquals(Sim.EDITOR, checklist.get("version-control").getString("by"));
    assertEquals(Sim.ADMIN, checklist.get("user-guide").getString("by"));
    assertFalse(checklist.get("requirements-specification").containsKey("status"));
    assertEquals("MET", checklist.get("test-plan").getString("status"));

    // The checklist follows the level: 0 Minimal asks for neither a User Guide nor a Test Plan,
    // and what was recorded of them comes back with the level
    assertOk(
        score(
            token,
            softwareId,
            3,
            3,
            3,
            List.of(Map.entry("levelOverride", "0"), Map.entry("levelNote", "An exception"))));
    checklist = checklist(row(name));
    assertFalse(checklist.containsKey("user-guide"));
    assertEquals("RECOMMENDED", checklist.get("version-control").getString("requirement"));
    assertOk(
        score(
            token,
            softwareId,
            3,
            3,
            3,
            List.of(Map.entry("levelOverride", ""), Map.entry("levelNote", ""))));
    assertEquals("MET", checklist(row(name)).get("user-guide").getString("status"));
  }

  @Test
  void notApplicableNeedsANoteAndALinkAWebAddress() throws Exception {
    String token = Sim.token(Sim.EDITOR);
    String name = newName();
    assertOk(add(token, name, "APP", "N"));
    String softwareId = row(name).attr("data-id");
    // Impact 4 and Complexity 1: level 2 Significant, which requires a User Guide
    scoreNamed(token, name, 4, 3);

    assertFail(
        record(token, softwareId, new String[] {"user-guide", "NOT_APPLICABLE", "", " "}),
        "\"User guide\" needs a note saying why it is not applicable");
    assertFail(
        record(token, softwareId, new String[] {"user-guide", "MET", "javascript:alert(1)", ""}),
        "needs a link that starts with http:// or https://");
    assertFail(
        record(token, softwareId, new String[] {"user-guide", "", "https://example.com", ""}),
        "needs a status to keep its link or note");
    assertFail(
        record(token, softwareId, new String[] {"user-guide", "DONE", "", ""}),
        "Unable to record SQA Elements");
    assertFail(
        record(token, softwareId, new String[] {"no-such-element", "MET", "", ""}),
        "Unknown SQA element: no-such-element");
    // One bad element fails them all
    assertFail(
        record(
            token,
            softwareId,
            new String[] {"version-control", "MET", "", ""},
            new String[] {"user-guide", "NOT_APPLICABLE", "", ""}),
        "not applicable");
    assertFail(
        Sim.post(
            "/ajax/edit-software-elements",
            token,
            List.of(
                Map.entry("softwareId", softwareId),
                Map.entry("elementId[]", "user-guide"),
                Map.entry("status[]", "MET"))),
        "Each element needs a status, a link and a note");

    assertEquals("Not recorded", elementsCell(row(name)).text(), "nothing was saved");
  }

  @Test
  void userAndAnonymousCannotRecordSqaElements() throws Exception {
    String name = newName();
    assertOk(add(Sim.token(Sim.EDITOR), name, "APP", "N"));
    String softwareId = row(name).attr("data-id");
    scoreNamed(Sim.token(Sim.EDITOR), name, 4, 3);

    for (String token : new String[] {Sim.token(Sim.USER), null}) {
      assertFail(
          record(token, softwareId, new String[] {"user-guide", "MET", "", ""}),
          "Not authenticated / authorized");
    }

    assertEquals("Not recorded", elementsCell(row(name)).text(), "nothing was saved");
  }

  @Test
  void directoryFiltersByAGapInAnSqaElement() throws Exception {
    String token = Sim.token(Sim.EDITOR);
    String withGap = newName();
    String without = newName();
    assertOk(add(token, withGap, "APP", "N"));
    assertOk(add(token, without, "APP", "N"));
    String softwareId = row(withGap).attr("data-id");
    // Impact 4 and Complexity 1: level 2 Significant, which requires a User Guide
    scoreNamed(token, withGap, 4, 3);
    scoreNamed(token, without, 4, 3);

    assertOk(record(token, softwareId, new String[] {"user-guide", "GAP", "", ""}));
    assertOk(
        record(
            token,
            row(without).attr("data-id"),
            new String[] {"user-guide", "MET", "https://example.com/guide", ""}));

    assertNotNull(row(withGap, "&gap=user-guide"));
    assertNotNull(row(withGap, "&gap=ANY"));
    assertNull(row(withGap, "&gap=version-control"));
    assertNull(row(without, "&gap=user-guide"));
    assertNull(row(without, "&gap=ANY"));

    String message = Sim.page("/directory?gap=user-guide", null).select(".message-box").text();
    assertTrue(message.contains("Gap in \"User guide\""), message);
    message = Sim.page("/directory?gap=ANY", null).select(".message-box").text();
    assertTrue(message.contains("Gap in any SQA Element"), message);
    assertEquals(400, Sim.get("/directory?gap=no-such-element", null).statusCode());

    // A gap recorded at another level is not on the checklist: 0 Minimal asks for no User Guide
    assertOk(
        score(
            token,
            softwareId,
            4,
            1,
            3,
            List.of(Map.entry("levelOverride", "0"), Map.entry("levelNote", "An exception"))));
    assertNull(row(withGap, "&gap=user-guide"));
    assertNull(row(withGap, "&gap=ANY"));
  }

  /**
   * SIM computes two elements from its own records, and nobody records them: the ratings, recorded
   * by the assessment that gives the checklist, and two maintainers, from the Maintainers column. A
   * product with one maintainer has a gap, which the gap filter finds where its level asks for two.
   */
  @Test
  void simComputesTheRatingsAndMaintainersElements() throws Exception {
    String token = Sim.token(Sim.EDITOR);
    String two = newName();
    String one = newName();
    assertOk(add(token, two, "APP", "N"));
    assertOk(
        addIn(
            repositoryId,
            token,
            one,
            "APP",
            "N",
            List.of(Map.entry("maintainerUsernameCsv", "jdoe"))));
    // Impact 4 and Complexity 1: level 2 Significant, which asks for two maintainers
    scoreNamed(token, two, 4, 3);
    scoreNamed(token, one, 4, 3);

    Map<String, JsonObject> checklist = checklist(row(two));
    assertEquals("MET", checklist.get("ratings-recorded").getString("computed"));
    String assessed = checklist.get("ratings-recorded").getString("computedNote");
    assertTrue(assessed.startsWith("Assessed by " + Sim.EDITOR + " on "), assessed);
    assertEquals("MET", checklist.get("two-maintainers").getString("computed"));
    assertEquals(
        "Maintainers: jdoe, tbrown", checklist.get("two-maintainers").getString("computedNote"));

    checklist = checklist(row(one));
    assertEquals("GAP", checklist.get("two-maintainers").getString("computed"));
    assertEquals("Only jdoe listed", checklist.get("two-maintainers").getString("computedNote"));
    assertEquals("Not recorded", elementsCell(row(one)).text(), "computed is not recorded");

    assertNotNull(row(one, "&gap=two-maintainers"));
    assertNotNull(row(one, "&gap=ANY"));
    assertNull(row(two, "&gap=two-maintainers"));
    assertNull(row(two, "&gap=ANY"));
    assertNull(row(two, "&gap=ratings-recorded"));

    String oneId = row(one).attr("data-id");
    assertFail(
        record(token, oneId, new String[] {"two-maintainers", "MET", "", ""}),
        "\"Two maintainers\" is set by SIM from its own records");
    assertFail(
        record(token, oneId, new String[] {"ratings-recorded", "GAP", "", ""}),
        "\"Ratings recorded\" is set by SIM from its own records");

    // 0 Minimal doesn't ask for two maintainers, so one is no gap there
    assertOk(
        score(
            token,
            oneId,
            4,
            1,
            3,
            List.of(Map.entry("levelOverride", "0"), Map.entry("levelNote", "An exception"))));
    assertNull(row(one, "&gap=two-maintainers"));
    assertFalse(checklist(row(one)).containsKey("two-maintainers"));
  }

  /**
   * An SQA element a repository provides for all its software is met on each product's checklist
   * without a record, and a status recorded for the product replaces it. GITHUB provides version
   * control and issue tracking; HOME_DIR, where the other tests add software, provides nothing.
   */
  @Test
  void anElementTheRepositoryProvidesIsMetUnlessRecordedOtherwise() throws Exception {
    String token = Sim.token(Sim.EDITOR);
    String github =
        Sim.page("/directory", null)
            .selectFirst("select[name=repositoryId] option:contains(GITHUB)")
            .attr("value");
    String name = "it-" + UUID.randomUUID().toString().substring(0, 8);
    String filter = "/directory?softwareName=" + name + "&repositoryId=" + github;
    assertOk(addIn(github, token, name, "APP", "N"));
    String softwareId = Sim.page(filter, null).selectFirst("tr[data-name]").attr("data-id");

    try {
      // Impact 4 and Complexity 1: level 2 Significant, which requires all three elements
      assertOk(
          score(
              token,
              softwareId,
              4,
              1,
              3,
              List.of(Map.entry("levelOverride", ""), Map.entry("levelNote", ""))));

      Element row = Sim.page(filter, null).selectFirst("tr[data-name]");
      Map<String, JsonObject> checklist = checklist(row);
      long required =
          checklist.values().stream()
              .filter(item -> "REQUIRED".equals(item.getString("requirement")))
              .count();
      JsonObject versionControl = checklist.get("version-control");
      assertEquals("GITHUB", versionControl.getString("provider"));
      assertEquals(
          "Every project is a Git repository on GitHub.", versionControl.getString("providerNote"));
      assertFalse(versionControl.containsKey("status"), "nobody recorded it");
      assertEquals("GITHUB", checklist.get("issue-tracking").getString("provider"));
      assertFalse(checklist.get("user-guide").containsKey("provider"));
      assertEquals(
          "Not recorded",
          elementsCell(row).text(),
          "what the repository provides is not a person's record");

      // Once something is recorded, the count includes what the repository provides
      assertOk(record(token, softwareId, new String[] {"user-guide", "MET", "", ""}));
      assertEquals(
          (3 + computedMet(Sim.page(filter, null))) + " of " + required,
          elementsCell(Sim.page(filter, null)).text());

      // A status recorded for the product replaces the repository's, and clearing it returns it
      assertOk(record(token, softwareId, new String[] {"version-control", "GAP", "", ""}));
      row = Sim.page(filter, null).selectFirst("tr[data-name]");
      assertEquals(
          (2 + computedMet(row)) + " of " + required + " (1 gap)", elementsCell(row).text());
      assertEquals("GAP", checklist(row).get("version-control").getString("status"));
      assertEquals("GITHUB", checklist(row).get("version-control").getString("provider"));
      assertNotNull(Sim.page(filter + "&gap=version-control", null).selectFirst("tr[data-name]"));

      assertOk(record(token, softwareId, new String[] {"version-control", "", "", ""}));
      assertEquals(
          (3 + computedMet(Sim.page(filter, null))) + " of " + required,
          elementsCell(Sim.page(filter, null)).text());
      assertNull(Sim.page(filter + "&gap=ANY", null).selectFirst("tr[data-name]"));
    } finally {
      assertOk(
          Sim.post(
              "/ajax/remove-software",
              Sim.token(Sim.ADMIN),
              List.of(Map.entry("softwareId", softwareId))));
    }

    // Software in HOME_DIR gets nothing from its repository
    String homeDir = newName();
    assertOk(add(token, homeDir, "APP", "N"));
    scoreNamed(token, homeDir, 4, 3);
    assertFalse(checklist(row(homeDir)).get("version-control").containsKey("provider"));
  }

  /**
   * An editor says how software is released, in the Edit dialog, and the Directory shows the
   * workflow and filters by it. A save that names no workflow, as a sync's Update does, leaves it.
   */
  @Test
  void editorSetsTheWorkflowAndTheDirectoryFiltersByIt() throws Exception {
    String token = Sim.token(Sim.EDITOR);
    String certified = workflowId("Certified");
    String name = newName();
    assertOk(add(token, name, "APP", "N"));

    Element row = row(name);
    String softwareId = row.attr("data-id");
    assertEquals("", row.attr("data-workflow-id"), "new software has no workflow");
    assertNull(row.selectFirst("td.name .name-meta.workflow"), "and shows none");
    assertNotNull(row(name, "&workflowId=NONE"));
    assertNull(row(name, "&workflowId=" + certified));

    assertOk(edit(token, softwareId, certified));

    row = row(name);
    assertEquals(certified, row.attr("data-workflow-id"));
    assertEquals("Certified workflow", row.selectFirst("td.name .name-meta.workflow").text());
    assertEquals("APP · HOME_DIR", row.selectFirst("td.name .name-meta").text());
    assertNotNull(row(name, "&workflowId=" + certified));
    assertNull(row(name, "&workflowId=" + workflowId("Legacy Certified")));
    assertNull(row(name, "&workflowId=NONE"));

    String message =
        Sim.page("/directory?workflowId=" + certified, null).select(".message-box").text();
    assertTrue(message.contains("Workflow \"Certified\""), message);
    message = Sim.page("/directory?workflowId=NONE", null).select(".message-box").text();
    assertTrue(message.contains("No Workflow"), message);

    // No workflow named: left as it is. A blank one: none.
    assertOk(edit(token, softwareId, null));
    assertEquals(certified, row(name).attr("data-workflow-id"));
    assertFail(edit(token, softwareId, "999999"), "workflow not found with id: 999999");
    assertFail(edit(token, softwareId, "abc"), "Unable to edit Software");
    for (String other : new String[] {Sim.token(Sim.USER), null}) {
      assertFail(edit(other, softwareId, ""), "Not authenticated / authorized");
    }
    assertEquals(certified, row(name).attr("data-workflow-id"), "nothing was saved");
    assertOk(edit(token, softwareId, ""));
    assertEquals("", row(name).attr("data-workflow-id"));

    // The Add dialog sets it too
    String withWorkflow = newName();
    assertOk(
        addIn(
            repositoryId,
            token,
            withWorkflow,
            "APP",
            "N",
            List.of(Map.entry("workflowId", certified))));
    assertEquals(certified, row(withWorkflow).attr("data-workflow-id"));
    assertFail(
        addIn(
            repositoryId, token, newName(), "APP", "N", List.of(Map.entry("workflowId", "999999"))),
        "workflow not found with id: 999999");
  }

  /**
   * An SQA element a workflow provides for all the software released with it is met on each
   * product's checklist without a record, as one its repository provides is, and a status recorded
   * for the product replaces it. The Certified workflow provides the version number and read-only
   * installs; CSUE the version number only; pinned versions are left to each product's record.
   */
  @Test
  void anElementTheWorkflowProvidesIsMetUnlessRecordedOtherwise() throws Exception {
    String token = Sim.token(Sim.EDITOR);
    String name = newName();
    assertOk(
        addIn(
            repositoryId,
            token,
            name,
            "APP",
            "N",
            List.of(Map.entry("workflowId", workflowId("Certified")))));
    String softwareId = row(name).attr("data-id");
    // Impact 4 and Complexity 1: level 2 Significant, which requires both elements
    scoreNamed(token, name, 4, 3);

    Element row = row(name);
    Map<String, JsonObject> checklist = checklist(row);
    long required =
        checklist.values().stream()
            .filter(item -> "REQUIRED".equals(item.getString("requirement")))
            .count();
    JsonObject version = checklist.get("installed-version");
    assertEquals("REQUIRED", version.getString("requirement"));
    assertEquals("the Certified workflow", version.getString("provider"));
    assertEquals(
        "Each release is installed under /cs/certified in a folder for its version.",
        version.getString("providerNote"));
    assertTrue(
        version.getString("providerLink").startsWith("https://pages.jlab.org/acc/iac/docs/sqap/"),
        version.getString("providerLink"));
    assertFalse(version.containsKey("status"), "nobody recorded it");
    assertEquals(
        "the Certified workflow", checklist.get("installed-read-only").getString("provider"));
    assertFalse(checklist.get("versions-pinned").containsKey("provider"), "left to the product");
    assertFalse(checklist.get("version-control").containsKey("provider"), "HOME_DIR provides none");
    assertEquals(
        "Not recorded",
        elementsCell(row).text(),
        "what the workflow provides is not a person's record");

    // Once something is recorded, the count includes what the workflow provides
    assertOk(record(token, softwareId, new String[] {"user-guide", "MET", "", ""}));
    assertEquals((3 + computedMet(row(name))) + " of " + required, elementsCell(row(name)).text());

    // A status recorded for the product replaces the workflow's, and clearing it returns it
    assertOk(record(token, softwareId, new String[] {"installed-read-only", "GAP", "", ""}));
    row = row(name);
    assertEquals((2 + computedMet(row)) + " of " + required + " (1 gap)", elementsCell(row).text());
    assertEquals("GAP", checklist(row).get("installed-read-only").getString("status"));
    assertEquals(
        "the Certified workflow", checklist(row).get("installed-read-only").getString("provider"));
    assertNotNull(row(name, "&gap=installed-read-only"));

    assertOk(record(token, softwareId, new String[] {"installed-read-only", "", "", ""}));
    assertEquals((3 + computedMet(row(name))) + " of " + required, elementsCell(row(name)).text());
    assertNull(row(name, "&gap=ANY"));

    // CSUE installs under developers' usernames: a product there stays not recorded for read-only
    assertOk(edit(token, softwareId, workflowId("CSUE")));
    row = row(name);
    assertEquals(
        "the CSUE workflow", checklist(row).get("installed-version").getString("provider"));
    assertFalse(checklist(row).get("installed-read-only").containsKey("provider"));
    assertFalse(checklist(row).get("installed-read-only").containsKey("status"), "not a gap");
    assertEquals((2 + computedMet(row)) + " of " + required, elementsCell(row).text());
    assertNull(row(name, "&gap=ANY"));

    // No workflow, nothing provided
    assertOk(edit(token, softwareId, ""));
    assertFalse(checklist(row(name)).get("installed-version").containsKey("provider"));
    assertEquals((1 + computedMet(row(name))) + " of " + required, elementsCell(row(name)).text());
  }

  /** A product gets what its workflow provides and what its repository provides, each named. */
  @Test
  void theWorkflowsElementsAddToTheRepositorys() throws Exception {
    String token = Sim.token(Sim.EDITOR);
    String github =
        Sim.page("/directory", null)
            .selectFirst("select[name=repositoryId] option:contains(GITHUB)")
            .attr("value");
    String name = "it-" + UUID.randomUUID().toString().substring(0, 8);
    String filter = "/directory?softwareName=" + name + "&repositoryId=" + github;
    assertOk(
        addIn(
            github,
            token,
            name,
            "APP",
            "N",
            List.of(Map.entry("workflowId", workflowId("GitHub web application")))));
    String softwareId = Sim.page(filter, null).selectFirst("tr[data-name]").attr("data-id");

    try {
      assertOk(
          score(
              token,
              softwareId,
              4,
              1,
              3,
              List.of(Map.entry("levelOverride", ""), Map.entry("levelNote", ""))));

      Element row = Sim.page(filter, null).selectFirst("tr[data-name]");
      Map<String, JsonObject> checklist = checklist(row);
      assertEquals("GITHUB", checklist.get("version-control").getString("provider"));
      assertEquals("GITHUB", checklist.get("issue-tracking").getString("provider"));
      assertEquals(
          "the GitHub web application workflow",
          checklist.get("installed-version").getString("provider"));
      assertEquals(
          "the GitHub web application workflow",
          checklist.get("installed-read-only").getString("provider"));
      assertEquals("", checklist.get("installed-version").getString("providerLink"));

      long required =
          checklist.values().stream()
              .filter(item -> "REQUIRED".equals(item.getString("requirement")))
              .count();
      assertOk(record(token, softwareId, new String[] {"user-guide", "MET", "", ""}));
      assertEquals(
          (5 + computedMet(Sim.page(filter, null))) + " of " + required,
          elementsCell(Sim.page(filter, null)).text());
    } finally {
      assertOk(
          Sim.post(
              "/ajax/remove-software",
              Sim.token(Sim.ADMIN),
              List.of(Map.entry("softwareId", softwareId))));
    }
  }

  /** A name nobody else uses, which the test's cleanup removes. */
  private String newName() {
    String name = "it-" + UUID.randomUUID().toString().substring(0, 8);
    added.add(name);
    return name;
  }

  /** Adds an app of this name, which the test's cleanup removes, and returns the name. */
  private String addNamed(String token, String name) throws Exception {
    added.add(name);
    assertOk(add(token, name, "APP", "N"));
    return name;
  }

  /** Scores the software of this name: Code Complexity 1, and the other four factors the same. */
  private static void scoreNamed(String token, String name, int impact, int others)
      throws Exception {
    assertOk(
        score(
            token,
            row(name).attr("data-id"),
            impact,
            1,
            others,
            List.of(Map.entry("levelOverride", ""), Map.entry("levelNote", ""))));
  }

  private HttpResponse<String> add(
      String token, String name, String type, String archived, String... topics) throws Exception {
    return addIn(repositoryId, token, name, type, archived, topics);
  }

  /** Adds software to a repository other than HOME_DIR: the caller removes it. */
  private static HttpResponse<String> addIn(
      String inRepositoryId,
      String token,
      String name,
      String type,
      String archived,
      String... topics)
      throws Exception {
    return addIn(inRepositoryId, token, name, type, archived, List.of(), topics);
  }

  /** Adds software with more parameters than the others send, such as its workflow. */
  private static HttpResponse<String> addIn(
      String inRepositoryId,
      String token,
      String name,
      String type,
      String archived,
      List<Map.Entry<String, String>> more,
      String... topics)
      throws Exception {
    List<Map.Entry<String, String>> form = new ArrayList<>(more);
    form.add(Map.entry("repositoryId", inRepositoryId));
    form.add(Map.entry("name", name));
    form.add(Map.entry("type", type));
    for (String topic : topics) {
      form.add(Map.entry("topicArray[]", topic));
    }
    form.add(Map.entry("description", "Added by an integration test"));
    if (more.stream().noneMatch(entry -> entry.getKey().equals("maintainerUsernameCsv"))) {
      form.add(Map.entry("maintainerUsernameCsv", "jdoe,tbrown"));
    }
    form.add(Map.entry("homeUrl", "https://example.com/" + name));
    form.add(Map.entry("archived", archived));
    form.add(Map.entry("note", ""));

    return Sim.post("/ajax/add-software", token, form);
  }

  /**
   * Saves software in HOME_DIR as the Edit dialog does, with this workflow: an id, blank for none,
   * or null to send no workflow at all, as a sync's Update does.
   */
  private static HttpResponse<String> edit(String token, String softwareId, String workflowId)
      throws Exception {
    List<Map.Entry<String, String>> form = new ArrayList<>();
    form.add(Map.entry("softwareId", softwareId));
    form.add(Map.entry("repositoryId", repositoryId));
    if (workflowId != null) {
      form.add(Map.entry("workflowId", workflowId));
    }
    form.add(Map.entry("type", "APP"));
    form.add(Map.entry("description", "Added by an integration test"));
    form.add(Map.entry("maintainerUsernameCsv", "jdoe,tbrown"));
    form.add(Map.entry("homeUrl", "https://example.com/edited"));
    form.add(Map.entry("archived", "N"));
    form.add(Map.entry("note", ""));

    return Sim.post("/ajax/edit-software", token, form);
  }

  /** A workflow's id, from the Directory's filter, by its name exactly. */
  private static String workflowId(String name) throws Exception {
    for (Element option : Sim.page("/directory", null).select("select[name=workflowId] option")) {
      if (option.text().equals(name)) {
        return option.attr("value");
      }
    }
    throw new AssertionError(name + " workflow");
  }

  /** Scores software: the impact and complexity given, and the other four factors all the same. */
  private static HttpResponse<String> score(
      String token,
      String softwareId,
      int impact,
      int complexity,
      int others,
      List<Map.Entry<String, String>> overrideParams)
      throws Exception {
    List<Map.Entry<String, String>> form = new ArrayList<>();
    form.add(Map.entry("softwareId", softwareId));
    form.add(Map.entry("impact", String.valueOf(impact)));
    form.add(Map.entry("complexity", String.valueOf(complexity)));
    form.add(Map.entry("rate", String.valueOf(others)));
    form.add(Map.entry("difficulty", String.valueOf(others)));
    form.add(Map.entry("gaps", String.valueOf(others)));
    form.add(Map.entry("esotericism", String.valueOf(others)));
    form.addAll(overrideParams);

    return Sim.post("/ajax/edit-software-risk", token, form);
  }

  /** Records SQA elements for software: each is an id, a status, a link and a note. */
  private static HttpResponse<String> record(String token, String softwareId, String[]... elements)
      throws Exception {
    List<Map.Entry<String, String>> form = new ArrayList<>();
    form.add(Map.entry("softwareId", softwareId));
    for (String[] element : elements) {
      form.add(Map.entry("elementId[]", element[0]));
      form.add(Map.entry("status[]", element[1]));
      form.add(Map.entry("link[]", element[2]));
      form.add(Map.entry("note[]", element[3]));
    }

    return Sim.post("/ajax/edit-software-elements", token, form);
  }

  /** The row's checklist, as the dialog reads it: its elements by id. */
  private static Map<String, JsonObject> checklist(Element row) {
    Map<String, JsonObject> checklist = new LinkedHashMap<>();
    try (JsonReader reader = Json.createReader(new StringReader(row.attr("data-checklist")))) {
      for (JsonValue item : reader.readArray()) {
        checklist.put(item.asJsonObject().getString("id"), item.asJsonObject());
      }
    }
    return checklist;
  }

  /** The count of required SQA elements met; null for software that has no checklist. */
  /**
   * The required elements SIM computes as met for this row, or the first row of this page: its
   * ratings, and two maintainers where the level asks for them, which the tests' software has.
   */
  private static long computedMet(Element rowOrPage) {
    Element row = rowOrPage.is("tr") ? rowOrPage : rowOrPage.selectFirst("tr[data-checklist]");
    return checklist(row).values().stream()
        .filter(item -> "REQUIRED".equals(item.getString("requirement")))
        .filter(item -> "MET".equals(item.getString("computed", "")))
        .count();
  }

  private static Element elementsCell(Element row) {
    return row.selectFirst("td.risk .sqa-elements");
  }

  private static Element impactCell(Element row) {
    return row.selectFirst("td.risk .ops-impact");
  }

  private static Element levelCell(Element row) {
    return row.selectFirst("td.risk .sqa-level");
  }

  private static Element attentionCell(Element row) {
    return row.selectFirst("td.risk .attention");
  }

  /** The software's row in the directory, or null if the directory doesn't list it. */
  private static Element row(String name) throws Exception {
    return row(name, "");
  }

  private static Element row(String name, String extraParams) throws Exception {
    return Sim.page(
            "/directory?softwareName="
                + Sim.encodeParam(name)
                + "&repositoryId="
                + repositoryId
                + extraParams,
            null)
        .selectFirst("tr[data-name=" + name + "]");
  }

  private static void assertOk(HttpResponse<String> response) {
    assertEquals(200, response.statusCode(), response.body());
    JsonObject json = Sim.json(response);
    assertEquals("ok", json.getString("stat"), response.body());
  }

  private static void assertFail(HttpResponse<String> response, String errorPart) {
    assertEquals(200, response.statusCode(), response.body());
    JsonObject json = Sim.json(response);
    assertEquals("fail", json.getString("stat"), response.body());
    assertTrue(json.getString("error").contains(errorPart), json.getString("error"));
  }
}
