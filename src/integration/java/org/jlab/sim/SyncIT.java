package org.jlab.sim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.json.JsonObject;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * The sync pages, reading the stand-in sources in {@code container/sync-stand-in}, which the
 * Compose stack points the app at. Tests that need software in SIM add it to the GITHUB repository
 * with a name the stand-in lists, and remove it afterwards.
 */
class SyncIT {

  private final List<String> added = new ArrayList<>();

  @BeforeAll
  static void awaitReady() throws Exception {
    Sim.awaitReady();
  }

  @AfterEach
  void removeAdded() throws Exception {
    String token = Sim.token(Sim.ADMIN);
    String repositoryId = repositoryId("GITHUB");

    for (String name : added) {
      Element row =
          Sim.page(
                  "/directory?softwareName="
                      + Sim.encodeParam(name)
                      + "&repositoryId="
                      + repositoryId
                      + "&archived=YES",
                  null)
              .selectFirst("tr[data-name=" + name + "]");
      if (row != null) {
        assertOk(
            Sim.post(
                "/ajax/remove-software",
                token,
                List.of(Map.entry("softwareId", row.attr("data-id")))));
      }
    }
  }

  /**
   * Each sync reads its stand-in: GitHub's second page through the Link header, and GitLab without
   * the project in acc/scratch, which the default GITLAB_SYNC_EXCLUDE_CSV setting leaves out.
   */
  @ParameterizedTest
  @CsvSource({"GITHUB, 4", "GITLAB, 1", "CSUE, 2", "CERTIFIED, 3", "LLAPP, 2"})
  void syncReadsTheStandIn(String repository, int remoteCount) throws Exception {
    String text = syncPage(repository).text();

    assertFalse(text.contains("Error:"), text);
    assertTrue(
        text.contains("Found " + remoteCount + " remote records"),
        "expected " + remoteCount + " remote records: " + text);
  }

  @Test
  void softwareOnlyAtTheSourceShowsAsAdd() throws Exception {
    Document page = syncPage("GITHUB");

    Element added = addRow(page, "it-sync-new");
    assertEquals("Only in the source", added.attr("data-description"));
    assertEquals("LIB", added.attr("data-type"), "from the lib topic");
    assertEquals("false", added.attr("data-archived"));

    // From the second page
    Element archived = addRow(page, "it-sync-archived");
    assertEquals("true", archived.attr("data-archived"));
  }

  /** Issue #63: Update sent the source's empty description. */
  @Test
  void updateKeepsSimsDescriptionWhenTheSourceHasNone() throws Exception {
    add("it-sync-no-description", "Only in SIM");

    Element row = updateRow(syncPage("GITHUB"), "it-sync-no-description");

    assertEquals("Only in SIM", row.attr("data-description"));
    assertEquals("acg,app,java", row.attr("data-topic-csv"), "the source's topics differ");
    assertFalse(
        row.select(".local").eachText().contains("Only in SIM"),
        "the description isn't shown as changing: " + row.text());
  }

  @Test
  void updateTakesTheSourcesDescription() throws Exception {
    add("it-sync-description", "Typed in SIM");

    Element row = updateRow(syncPage("GITHUB"), "it-sync-description");

    assertEquals("From the source", row.attr("data-description"));
    assertTrue(row.select(".remote").eachText().contains("From the source"), row.text());
    assertTrue(row.select(".local").eachText().contains("Typed in SIM"), row.text());
  }

  /** Adds software to GITHUB with the app topic and the home URL the stand-in gives it. */
  private void add(String name, String description) throws Exception {
    List<Map.Entry<String, String>> form = new ArrayList<>();
    form.add(Map.entry("repositoryId", repositoryId("GITHUB")));
    form.add(Map.entry("name", name));
    form.add(Map.entry("type", "APP"));
    form.add(Map.entry("topicArray[]", "app"));
    form.add(Map.entry("description", description));
    form.add(Map.entry("maintainerUsernameCsv", "jdoe"));
    form.add(Map.entry("homeUrl", "https://github.com/JeffersonLab/" + name));
    form.add(Map.entry("archived", "N"));
    form.add(Map.entry("note", ""));

    added.add(name);
    assertOk(Sim.post("/ajax/add-software", Sim.token(Sim.ADMIN), form));
  }

  private static Document syncPage(String repository) throws Exception {
    return Sim.page(
        "/repositories/sync?repositoryId=" + repositoryId(repository), Sim.token(Sim.ADMIN));
  }

  private static Element addRow(Document page, String name) {
    Element row = page.selectFirst("tr[data-name=" + name + "]:has(button.add)");
    assertNotNull(row, "Add row for " + name);
    return row;
  }

  private static Element updateRow(Document page, String name) {
    Element row = page.selectFirst("tr[data-name=" + name + "]:has(button.update)");
    assertNotNull(row, "Update row for " + name);
    return row;
  }

  private static String repositoryId(String name) throws Exception {
    Element option =
        Sim.page("/directory", null)
            .selectFirst("select[name=repositoryId] option:contains(" + name + ")");
    assertNotNull(option, name + " repository");
    return option.attr("value");
  }

  private static void assertOk(HttpResponse<String> response) {
    assertEquals(200, response.statusCode(), response.body());
    JsonObject json = Sim.json(response);
    assertEquals("ok", json.getString("stat"), response.body());
  }
}
