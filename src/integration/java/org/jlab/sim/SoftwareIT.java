package org.jlab.sim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.json.JsonObject;
import java.net.http.HttpResponse;
import java.util.ArrayList;
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
        "Ops Impact 5 Critical SQA Level 2 Significant* Attention Severe",
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
    List<Map.Entry<String, String>> form = new ArrayList<>();
    form.add(Map.entry("repositoryId", repositoryId));
    form.add(Map.entry("name", name));
    form.add(Map.entry("type", type));
    for (String topic : topics) {
      form.add(Map.entry("topicArray[]", topic));
    }
    form.add(Map.entry("description", "Added by an integration test"));
    form.add(Map.entry("maintainerUsernameCsv", "jdoe,tbrown"));
    form.add(Map.entry("homeUrl", "https://example.com/" + name));
    form.add(Map.entry("archived", archived));
    form.add(Map.entry("note", ""));

    return Sim.post("/ajax/add-software", token, form);
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
