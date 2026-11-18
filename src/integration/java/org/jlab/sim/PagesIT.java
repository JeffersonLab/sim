package org.jlab.sim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.http.HttpResponse;
import java.util.List;
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
        "/directory?level=2&attention=3&fragile=Y&assessed=N",
        "/directory?workflowId=NONE",
        "/repositories",
        "/workflows",
        "/help"
      })
  void anyoneReadsPage(String path) throws Exception {
    Document page = Sim.page(path, null);

    assertTrue(page.title().startsWith("SIM - "), "title: " + page.title());
  }

  @ParameterizedTest
  @CsvSource({
    "/directory?type=bogus, 400",
    "/directory?type=lib, 400",
    "/directory?repositoryId=abc, 400",
    "/directory?archived=bogus, 400",
    "/directory?offset=-1, 400",
    "/directory?offset=abc, 400",
    "/directory?max=abc, 400",
    "/directory?level=7, 400",
    "/directory?level=abc, 400",
    "/directory?attention=0, 400",
    "/directory?fragile=maybe, 400",
    "/directory?assessed=bogus, 400",
    "/directory?repositoryId=999999, 400",
    "/directory?workflowId=abc, 400",
    "/directory?workflowId=999999, 400",
    "/repositories/sync, 400",
    "/repositories/sync?repositoryId=, 400",
    "/repositories/sync?repositoryId=abc, 400",
    "/repositories/sync?repositoryId=999999, 404"
  })
  void pageRejectsBadParameter(String path, int status) throws Exception {
    HttpResponse<String> response = Sim.get(path, null);

    assertEquals(status, response.statusCode(), path);
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

  /** The SQA elements a repository provides for all its software, with the basis of each. */
  @Test
  void repositoriesListsTheSqaElementsEachProvides() throws Exception {
    Document page = Sim.page("/repositories", null);

    String gitlab =
        page.select("tr:has(td:first-child:contains(GITLAB)) td.provided-elements").text();
    assertTrue(gitlab.contains("Version control"), gitlab);
    assertTrue(gitlab.contains("Every project is a Git repository on code.jlab.org."), gitlab);
    assertTrue(gitlab.contains("Issue tracking"), gitlab);

    String csue = page.select("tr:has(td:first-child:contains(CSUE)) td.provided-elements").text();
    assertTrue(csue.contains("Version control"), csue);
    assertFalse(csue.contains("Issue tracking"), csue);

    for (String name : new String[] {"CERTIFIED", "HOME_DIR"}) {
      assertEquals(
          "",
          page.select("tr:has(td:first-child:contains(" + name + ")) td.provided-elements").text(),
          name + " provides none");
    }
  }

  /**
   * The Workflows tab: each workflow with the SQA elements it provides for all the software
   * released with it, and the basis of each. CSUE and the EPICS build system install into a
   * directory for each version, but under developers' usernames, so they provide the version number
   * and not read-only installs. None provides pinned versions, which are left to each product's
   * record.
   */
  @Test
  void workflowsListsTheSqaElementsEachProvides() throws Exception {
    Document page = Sim.page("/workflows", null);

    assertEquals(
        "Certified,GitHub web application,CSUE,EPICS build system,Legacy Certified,None",
        String.join(",", page.select("tbody td:first-child .workflow-name").eachText()),
        "the workflows, in their order, then none");

    String certified = provided(page, "Certified");
    assertTrue(certified.contains("Installed version shown"), certified);
    assertTrue(
        certified.contains("Each release is installed under /cs/certified in a folder for"),
        certified);
    assertTrue(certified.contains("Read-only install"), certified);

    for (String name : new String[] {"GitHub web application", "Legacy Certified"}) {
      String both = provided(page, name);
      assertTrue(both.contains("Installed version shown"), both);
      assertTrue(both.contains("A service account owns the installed software"), both);
    }

    for (String name : new String[] {"CSUE", "EPICS build system"}) {
      String versionOnly = provided(page, name);
      assertTrue(versionOnly.contains("Installed version shown"), name);
      assertTrue(
          versionOnly.contains("Each release is installed in a directory named for its version"),
          versionOnly);
      assertFalse(versionOnly.contains("Read-only install"), name);
    }

    assertFalse(page.text().contains("Dependency versions pinned"), "no workflow provides it");
    assertEquals("", provided(page, "None"));
    assertTrue(page.select("#primary-nav a").eachText().contains("Workflows"), "the tab");
  }

  /**
   * Lists of SQA elements show the SQAP's short label, with its full row as the title for hover:
   * the Directory's gap filter and checklist, and the Repositories tab.
   */
  @Test
  void elementsShowTheirShortLabelWithTheFullRowOnHover() throws Exception {
    String full = "Choice recorded: purpose, source, license, maintenance status";
    Document directory = Sim.page("/directory", null);

    Element option = directory.selectFirst("#gap-select option[value=acquired-choice-recorded]");
    assertEquals("Dependency choices recorded", option.text());
    assertEquals(full, option.attr("title"));

    Element label =
        directory.selectFirst(
            "#elements-table tr[data-element-id=acquired-choice-recorded] .element-short");
    assertEquals("Dependency choices recorded", label.text());
    assertEquals(full, label.attr("title"));

    Element provided =
        Sim.page("/repositories", null)
            .selectFirst("tr:has(td:first-child:contains(GITLAB)) .provided-element > span");
    assertEquals("Version control", provided.text());
    assertEquals("Version control", provided.attr("title"));
  }

  /**
   * Each SQA element links to its section in the revision of the SQAP it was loaded from, as does
   * the dialog's link to section 4.4: for a draft, the plan's source at the draft's commit, since
   * the website's latest is the last released revision, whose tables may lack the draft's elements.
   */
  @Test
  void elementsLinkToTheSqapRevisionTheyCameFrom() throws Exception {
    Document directory = Sim.page("/directory", null);
    String footer = directory.selectFirst("#elements-footer").text();
    String section =
        directory
            .selectFirst("#elements-table tr[data-element-id=two-maintainers] .element-meta a")
            .attr("href");
    String stages = directory.selectFirst("#elements-dialog .dialog-links a").attr("href");

    assertTrue(footer.contains("draft "), "the test data is a draft revision: " + footer);
    String revision = footer.replaceAll(".*draft (\\S+) after.*", "$1");
    String page =
        "https://code.jlab.org/acc/iac/docs/sqap/-/blob/"
            + revision
            + "/docs/04-software-development-life-cycle.md";
    assertEquals(page + "#446-maintenance", section);
    assertEquals(page + "#44-life-cycle-stages", stages);
  }

  /** The elements a workflow's row lists, by the workflow's name exactly. */
  private static String provided(Document page, String workflow) {
    for (Element row : page.select("tbody tr")) {
      if (row.selectFirst(".workflow-name").text().equals(workflow)) {
        return row.select("td.provided-elements").text();
      }
    }
    throw new AssertionError(workflow + " workflow");
  }

  @ParameterizedTest
  @CsvSource({Sim.ADMIN + ", 200", Sim.EDITOR + ", 403", Sim.USER + ", 403"})
  void onlyAdminsReadSetup(String username, int status) throws Exception {
    assertEquals(status, Sim.get("/setup/settings", Sim.token(username)).statusCode());
  }

  @Test
  void settingsListTheGitLabSyncExclusions() throws Exception {
    String text = Sim.page("/setup/settings", Sim.token(Sim.ADMIN)).text();

    assertTrue(text.contains("GITLAB_SYNC_EXCLUDE_CSV"), "the setting is listed");
    assertTrue(text.contains("acc/scratch"), "it leaves out the scratch group by default");
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
    Document page = Sim.page("/directory", Sim.token(username));

    assertEquals(editable, directoryEditable(page), "table");
    assertEquals(editable, assessmentEditable(page), "assessment dialog");
    assertEquals(editable ? "Save,Cancel" : "Close", assessmentButtons(page), "dialog buttons");
    assertEquals(editable, editOpenersOnEveryRow(page), "name, description and maintainers cells");
    assertEquals(editable ? "Add" : "", rowControls(page), "toolbar");
    assertEquals(
        editable ? "Click a row to edit it" : "",
        page.select("#editable-row-table-control-panel .row-edit-hint").text(),
        "hint beside Add");
    assertEquals(
        editable,
        page.selectFirst("#table-row-dialog #remove-software-button") != null,
        "Remove in the edit dialog");
  }

  @Test
  void anonymousAndLooseDirectoryAreReadOnly() throws Exception {
    Document anonymous = Sim.page("/directory", null);
    Document loose = Sim.page("/directory?loose=Y", Sim.token(Sim.ADMIN));

    assertFalse(directoryEditable(anonymous));
    assertFalse(assessmentEditable(anonymous));
    assertFalse(directoryEditable(loose));
    assertFalse(assessmentEditable(loose));
    assertEquals("Close", assessmentButtons(anonymous));
    assertEquals("Close", assessmentButtons(loose));
    assertFalse(editOpenersOnEveryRow(anonymous));
    assertFalse(editOpenersOnEveryRow(loose));
    assertEquals("", rowControls(anonymous));
    assertEquals("", rowControls(loose));
    assertTrue(anonymous.select(".row-edit-hint").isEmpty(), "no hint for anonymous");
    assertTrue(loose.select(".row-edit-hint").isEmpty(), "no hint in the loose view");
  }

  /**
   * The filter form submits a blank select as an empty parameter, such as {@code level=}; the page
   * must keep it blank rather than select level 0, which Next and Previous would then submit.
   */
  @ParameterizedTest
  @CsvSource({
    "'level=&attention=', '', ''",
    "'level=0&attention=', 0, ''",
    "'level=2&attention=3', 2, 3",
  })
  void directoryFilterKeepsSelections(String query, String level, String attention)
      throws Exception {
    Document page = Sim.page("/directory?" + query, null);

    assertEquals(level, selected(page, "#level-select"), query);
    assertEquals(attention, selected(page, "#attention-select"), query);
  }

  /** The workflow filter keeps a workflow, None, or nothing, as submitted. */
  @Test
  void directoryFilterKeepsTheWorkflow() throws Exception {
    Document page = Sim.page("/directory?workflowId=NONE", null);
    assertEquals("NONE", selected(page, "#workflow-select"));

    String id =
        page.selectFirst("#workflow-select option:matchesOwn(^\\s*CSUE\\s*$)").attr("value");
    assertEquals(id, selected(Sim.page("/directory?workflowId=" + id, null), "#workflow-select"));

    assertEquals("", selected(Sim.page("/directory?workflowId=", null), "#workflow-select"));
  }

  /** Editors pick the workflow in the Add and Edit dialog; the first choice is none. */
  @Test
  void editDialogOffersTheWorkflows() throws Exception {
    Document page = Sim.page("/directory", Sim.token(Sim.EDITOR));

    assertEquals(
        List.of(
            "None",
            "Certified",
            "GitHub web application",
            "CSUE",
            "EPICS build system",
            "Legacy Certified"),
        page.select("#table-row-dialog #row-workflow option").eachText());
  }

  private static String selected(Document page, String select) {
    return page.select(select + " option[selected]").eachAttr("value").stream()
        .reduce((a, b) -> a + "," + b)
        .orElse("");
  }

  private static boolean directoryEditable(Document page) {
    Element table = page.selectFirst("table.data-table");

    assertTrue(table != null, "directory table");

    return table.hasClass("editable-row-table");
  }

  /** directory.js enables the Risk &amp; SQA Assessment dialog's inputs from this. */
  private static boolean assessmentEditable(Document page) {
    Element dialog = page.selectFirst("#risk-dialog");

    assertTrue(dialog != null, "assessment dialog");

    return "Y".equals(dialog.attr("data-editable"));
  }

  /**
   * Whether each row's Name, Description and Maintainers cells, and only those, are marked to open
   * the edit dialog with a tooltip (#70); directory.js handles the click. Fails if only some are.
   */
  private static boolean editOpenersOnEveryRow(Document page) {
    int rows = page.select("table.data-table tbody tr").size();
    int openers = page.select("table.data-table tbody td.edit-opener").size();
    int expected = 3 * rows;

    assertTrue(rows > 0, "directory rows");
    assertTrue(openers == 0 || openers == expected, openers + " openers for " + rows + " rows");
    assertEquals(
        openers,
        page.select(
                "tbody td.name.edit-opener[title], tbody td.description.edit-opener[title],"
                    + " tbody td.maintainers.edit-opener[title]")
            .size(),
        "only the name, description and maintainers cells, with a tooltip");
    assertFalse(
        page.selectFirst("table.data-table").hasClass("uniselect-table"), "rows aren't selected");

    return openers == expected;
  }

  /**
   * The visible buttons above the directory table: Add only, since a row click opens Edit and
   * Remove is in its dialog. Unselect is always in smoothness's markup; directory.css hides it.
   */
  private static String rowControls(Document page) {
    return String.join(
        ",",
        page.select("#editable-row-table-control-panel button:not(#unselect-all-button)")
            .eachText());
  }

  /** The assessment dialog's buttons, so OK can't be read as save (#69). */
  private static String assessmentButtons(Document page) {
    return String.join(",", page.select("#risk-dialog .dialog-button-panel button").eachText());
  }
}
