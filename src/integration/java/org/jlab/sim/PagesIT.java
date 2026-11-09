package org.jlab.sim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.http.HttpResponse;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/** The pages anyone may read, the setup pages only admins may, and who may edit the directory. */
class PagesIT {

  @BeforeAll
  static void awaitReady() throws Exception {
    Sim.awaitReady();
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "/directory",
        "/directory?loose=Y",
        "/directory?type=LIB",
        "/directory?archived=YES",
        "/directory?max=5&offset=5",
        "/repositories",
        "/help"
      })
  void anyoneReadsPage(String path) throws Exception {
    Document page = Sim.page(path, null);

    assertTrue(page.title().startsWith("SIM - "), "title: " + page.title());
  }

  @Test
  void rootRedirectsToDirectory() throws Exception {
    HttpResponse<String> response = Sim.get("/", null);

    assertEquals(302, response.statusCode());
    assertEquals(
        Sim.SIM_URL + "/directory", response.headers().firstValue("Location").orElseThrow());
  }

  @Test
  void repositoriesListsEachRepository() throws Exception {
    String text = Sim.page("/repositories", null).text();

    for (String name : new String[] {"GITHUB", "GITLAB", "CERTIFIED", "CSUE", "LLAPP"}) {
      assertTrue(text.contains(name), name);
    }
  }

  @ParameterizedTest
  @CsvSource({Sim.ADMIN + ", 200", Sim.EDITOR + ", 403", Sim.USER + ", 403"})
  void onlyAdminsReadSetup(String username, int status) throws Exception {
    assertEquals(status, Sim.get("/setup/settings", Sim.token(username)).statusCode());
  }

  @Test
  void anonymousSetupGoesToLogin() throws Exception {
    HttpResponse<String> response = Sim.get("/setup/settings", null);

    assertEquals(302, response.statusCode());
    assertTrue(
        response.headers().firstValue("Location").orElseThrow().startsWith(Sim.KEYCLOAK_URL + "/"),
        "redirects to Keycloak's login");
  }

  @ParameterizedTest
  @CsvSource({Sim.ADMIN + ", true", Sim.EDITOR + ", true", Sim.USER + ", false"})
  void onlyEditorsGetEditableDirectory(String username, boolean editable) throws Exception {
    assertEquals(editable, directoryEditable(Sim.page("/directory", Sim.token(username))));
  }

  @Test
  void anonymousAndLooseDirectoryAreReadOnly() throws Exception {
    assertFalse(directoryEditable(Sim.page("/directory", null)));
    assertFalse(directoryEditable(Sim.page("/directory?loose=Y", Sim.token(Sim.ADMIN))));
  }

  private static boolean directoryEditable(Document page) {
    Element table = page.selectFirst("table.data-table");

    assertTrue(table != null, "directory table");

    return table.hasClass("editable-row-table");
  }
}
