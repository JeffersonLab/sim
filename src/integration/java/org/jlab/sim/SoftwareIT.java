package org.jlab.sim;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    assertEquals("https://example.com/" + name, row.attr("data-url"));
    assertEquals(repositoryId, row.attr("data-repo-id"));
    assertEquals("N", row.attr("data-archived"));
    assertEquals("3", row.attr("data-impact"), "new software scores Medium");

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
                Map.entry("esotericism", "4"))));

    row = row(name);
    assertEquals("5", row.attr("data-impact"));
    assertEquals("4", row.attr("data-rate"));
    assertEquals("1", row.attr("data-difficulty"));
    assertEquals("2", row.attr("data-complexity"));
    assertEquals("5", row.attr("data-gaps"));
    assertEquals("4", row.attr("data-esotericism"));

    assertOk(
        Sim.post("/ajax/remove-software", token, List.of(Map.entry("softwareId", softwareId))));

    assertNull(row(name), "removed software is not listed");
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
