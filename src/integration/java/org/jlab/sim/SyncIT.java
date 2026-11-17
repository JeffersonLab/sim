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
 * Compose stack points the app at. Tests that need software in SIM add it with a name the stand-in
 * lists, to the GITHUB repository unless they say otherwise, and remove it afterwards.
 */
class SyncIT {

  /** The software the test added, each as its repository and its name. */
  private final List<Map.Entry<String, String>> added = new ArrayList<>();

  @BeforeAll
  static void awaitReady() throws Exception {
    Sim.awaitReady();
  }

  @AfterEach
  void removeAdded() throws Exception {
    String token = Sim.token(Sim.ADMIN);

    for (Map.Entry<String, String> software : added) {
      Element row = directoryRow(software.getKey(), software.getValue());
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
  @CsvSource({"GITHUB, 4", "GITLAB, 2", "CSUE, 2", "CERTIFIED, 3", "LLAPP, 2"})
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

  /**
   * Each sync proposes a workflow for the software it adds, where its repository tells how its
   * software is released: CSUE, Low Level Apps and the legacy Certified list each one way, and a
   * GitLab project in acc/ops/acs/certified by the certified workflow. Other GitLab projects and
   * GitHub's get none (#80).
   */
  @ParameterizedTest
  @CsvSource({
    "CSUE, it-sync-csue, CSUE",
    "LLAPP, it-sync-llapp, EPICS build system",
    "CERTIFIED, it-sync-certified-app, Legacy Certified",
    "CERTIFIED, it-sync-certified-lib, Legacy Certified",
    "GITLAB, it-sync-certified, Certified",
    "GITLAB, it-sync-gitlab, ''",
    "GITHUB, it-sync-new, ''"
  })
  void syncProposesAWorkflowWhereTheRepositoryTells(String repository, String name, String workflow)
      throws Exception {
    Element row = addRow(syncPage(repository), name);

    assertEquals(workflow, row.selectFirst("td.workflow").text(), "the proposal, shown");
    assertEquals(
        workflow.isEmpty() ? "" : workflowId(workflow),
        row.attr("data-workflow-id"),
        "the proposal, as Add sends it");
  }

  /**
   * Add gives the software the workflow its sync proposes. Update sends no workflow, so a sync
   * never changes one somebody set, whatever it would propose.
   */
  @Test
  void addTakesTheProposedWorkflowAndUpdateLeavesASetOneAlone() throws Exception {
    String token = Sim.token(Sim.EDITOR);
    String name = "it-sync-certified";

    // As the page's Add button does (sync.js)
    Element addRow = addRow(syncPage("GITLAB"), name);
    List<Map.Entry<String, String>> form = new ArrayList<>();
    form.add(Map.entry("repositoryId", repositoryId("GITLAB")));
    form.add(Map.entry("workflowId", addRow.attr("data-workflow-id")));
    form.add(Map.entry("name", name));
    form.add(Map.entry("type", addRow.attr("data-type")));
    form.add(Map.entry("topicArray[]", addRow.attr("data-topic-csv")));
    form.add(Map.entry("description", addRow.attr("data-description")));
    form.add(Map.entry("maintainerUsernameCsv", addRow.attr("data-maintainer")));
    form.add(Map.entry("homeUrl", addRow.attr("data-url")));
    form.add(Map.entry("archived", "N"));
    added.add(Map.entry("GITLAB", name));
    assertOk(Sim.post("/ajax/add-software", token, form));

    Element row = directoryRow("GITLAB", name);
    assertEquals(workflowId("Certified"), row.attr("data-workflow-id"));
    assertEquals("Certified workflow", row.selectFirst("td.name .name-meta.workflow").text());
    assertEquals(
        "Certified",
        syncPage("GITLAB")
            .selectFirst("tr:has(td:first-child:matchesOwn(^" + name + "$))")
            .selectFirst("td.workflow")
            .text(),
        "a matched row shows SIM's workflow");

    // Somebody sets another workflow, and a description the source will put back
    String softwareId = row.attr("data-id");
    assertOk(
        Sim.post(
            "/ajax/edit-software",
            token,
            List.of(
                Map.entry("softwareId", softwareId),
                Map.entry("repositoryId", repositoryId("GITLAB")),
                Map.entry("workflowId", workflowId("CSUE")),
                Map.entry("type", row.attr("data-type")),
                Map.entry("topicArray[]", row.attr("data-topic-csv")),
                Map.entry("description", "Typed in SIM"),
                Map.entry("maintainerUsernameCsv", ""),
                Map.entry("homeUrl", row.attr("data-url")),
                Map.entry("archived", "N"))));

    // As the page's Update button does: no workflowId
    Element updateRow = updateRow(syncPage("GITLAB"), name);
    assertEquals("CSUE", updateRow.selectFirst("td.workflow").text(), "SIM's, not the proposal");
    assertFalse(updateRow.hasAttr("data-workflow-id"), "Update has no workflow to send");
    assertOk(
        Sim.post(
            "/ajax/edit-software",
            token,
            List.of(
                Map.entry("softwareId", softwareId),
                Map.entry("repositoryId", repositoryId("GITLAB")),
                Map.entry("type", updateRow.attr("data-type")),
                Map.entry("topicArray[]", updateRow.attr("data-topic-csv")),
                Map.entry("description", updateRow.attr("data-description")),
                Map.entry("maintainerUsernameCsv", updateRow.attr("data-maintainer")),
                Map.entry("homeUrl", updateRow.attr("data-url")),
                Map.entry("archived", "N"))));

    row = directoryRow("GITLAB", name);
    assertEquals(
        "In acc/ops/acs/certified, so its sync proposes the certified workflow",
        row.attr("data-description"),
        "the update was applied");
    assertEquals(workflowId("CSUE"), row.attr("data-workflow-id"), "and left the workflow alone");
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

    added.add(Map.entry("GITHUB", name));
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

  /** The software's row in the Directory, archived or not, or null if it isn't there. */
  private static Element directoryRow(String repository, String name) throws Exception {
    return Sim.page(
            "/directory?softwareName="
                + Sim.encodeParam(name)
                + "&repositoryId="
                + repositoryId(repository)
                + "&archived=YES",
            null)
        .selectFirst("tr[data-name=" + name + "]");
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
