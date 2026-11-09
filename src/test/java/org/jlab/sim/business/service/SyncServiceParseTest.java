package org.jlab.sim.business.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.jlab.sim.persistence.entity.Repository;
import org.jlab.sim.persistence.entity.Software;
import org.jlab.sim.persistence.enumeration.SoftwareType;
import org.jlab.sim.persistence.view.SoftwareDiff;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;

/**
 * How a sync reads each repository's response. The responses are made up, in the format each
 * repository returns.
 */
class SyncServiceParseTest {

  private static final Repository REPOSITORY = new Repository();

  @Test
  void gitHubSearchItemsBecomeSoftware() throws Exception {
    String json =
        """
        {"total_count": 2, "incomplete_results": false, "items": [
          {"name": "epics2web", "html_url": "https://github.com/JeffersonLab/epics2web",
           "description": "Web gateway", "topics": ["ACG", "App", "epics"], "archived": false,
           "stargazers_count": 3},
          {"name": "oldlib", "html_url": "https://github.com/JeffersonLab/oldlib",
           "description": null, "topics": ["acg", "lib"], "archived": true}
        ]}
        """;

    List<Software> softwareList = SyncService.parseGitHubSearch(json, REPOSITORY);

    assertEquals(2, softwareList.size());

    Software app = softwareList.get(0);
    assertEquals("epics2web", app.getName());
    assertEquals("https://github.com/JeffersonLab/epics2web", app.getHomeUrl());
    assertEquals("Web gateway", app.getDescription());
    assertEquals(SoftwareType.APP, app.getType());
    assertEquals(List.of("acg", "app", "epics"), app.getStringTopicList(), "lower case");
    assertFalse(app.isArchived());
    assertTrue(app.isArchivedSynced(), "GitHub says whether a repository is archived");
    assertNull(app.getMaintainerUsernameCsv());
    assertSame(REPOSITORY, app.getRepository());

    Software lib = softwareList.get(1);
    assertEquals(SoftwareType.LIB, lib.getType());
    assertNull(lib.getDescription());
    assertTrue(lib.isArchived());
  }

  @Test
  void gitLabProjectsBecomeSoftware() throws Exception {
    String json =
        """
        [
          {"id": 12, "name": "fsdlib", "path_with_namespace": "acc/ops/fsdlib",
           "web_url": "https://code.jlab.org/acc/ops/fsdlib", "description": "FSD library",
           "topics": ["Lib"], "archived": false},
          {"id": 13, "name": "notes", "web_url": "https://code.jlab.org/acc/notes",
           "description": null, "topics": [], "archived": true}
        ]
        """;

    List<Software> softwareList = SyncService.parseGitLabProjects(json, REPOSITORY, List.of());

    assertEquals(2, softwareList.size());

    Software lib = softwareList.get(0);
    assertEquals("fsdlib", lib.getName());
    assertEquals("https://code.jlab.org/acc/ops/fsdlib", lib.getHomeUrl());
    assertEquals("FSD library", lib.getDescription());
    assertEquals(SoftwareType.LIB, lib.getType());
    assertEquals(List.of("lib"), lib.getStringTopicList());
    assertTrue(lib.isArchivedSynced());

    Software notes = softwareList.get(1);
    assertEquals(SoftwareType.APP, notes.getType(), "no type topic means app");
    assertEquals(List.of(), notes.getStringTopicList());
    assertTrue(notes.isArchived());
  }

  @Test
  void multiLineDescriptionMatchesWhatSimSaved() throws Exception {
    // GitLab returns a description entered in its web form with CRLF line endings; the sync page
    // passes it through an HTML attribute, which the browser reads with LF line endings (sim#31)
    String json =
        """
        [
          {"name": "fsdlib", "web_url": "https://code.jlab.org/acc/ops/fsdlib",
           "description": "FSD library\\r\\nfor apps\\rand scripts\\n", "topics": [],
           "archived": false}
        ]
        """;

    List<Software> remoteList = SyncService.parseGitLabProjects(json, REPOSITORY, List.of());

    assertEquals("FSD library\nfor apps\nand scripts\n", remoteList.get(0).getDescription());

    Software saved =
        new Software(
            REPOSITORY,
            "fsdlib",
            SoftwareType.APP,
            "FSD library\nfor apps\nand scripts\n",
            null,
            "https://code.jlab.org/acc/ops/fsdlib",
            false,
            null);

    SoftwareDiff diff = new SyncService().diff(List.of(saved), Map.of("fsdlib", remoteList.get(0)));

    assertEquals(1, diff.getMatchList().size(), "matched, not updated again");
    assertFalse(diff.hasChanges());
  }

  @Test
  void excludedGitLabGroupsAndProjectsAreLeftOut() throws Exception {
    String json =
        """
        [
          {"name": "fsdlib", "path_with_namespace": "acc/ops/fsdlib",
           "web_url": "https://code.jlab.org/acc/ops/fsdlib", "description": null,
           "topics": [], "archived": false},
          {"name": "try-this", "path_with_namespace": "acc/scratch/try-this",
           "web_url": "https://code.jlab.org/acc/scratch/try-this", "description": null,
           "topics": [], "archived": false},
          {"name": "deep", "path_with_namespace": "acc/Scratch/jdoe/deep",
           "web_url": "https://code.jlab.org/acc/Scratch/jdoe/deep", "description": null,
           "topics": [], "archived": false},
          {"name": "scratchpad", "path_with_namespace": "acc/scratchpad",
           "web_url": "https://code.jlab.org/acc/scratchpad", "description": null,
           "topics": [], "archived": false},
          {"name": "demo", "path_with_namespace": "acc/ops/demo",
           "web_url": "https://code.jlab.org/acc/ops/demo", "description": null,
           "topics": [], "archived": false}
        ]
        """;

    List<String> excluded =
        SyncService.excludedGitLabPaths(List.of("acc/scratch", " /ACC/ops/demo/ ", ""));

    assertEquals(List.of("acc/scratch", "acc/ops/demo"), excluded, "trimmed and lower case");

    List<Software> softwareList = SyncService.parseGitLabProjects(json, REPOSITORY, excluded);

    assertEquals(
        List.of("fsdlib", "scratchpad"),
        softwareList.stream().map(Software::getName).toList(),
        "a group's projects and subgroups are left out, as is a listed project, but not a"
            + " sibling whose name starts the same");
  }

  @Test
  void noExcludeSettingExcludesNothing() {
    assertEquals(List.of(), SyncService.excludedGitLabPaths(null));
    assertFalse(SyncService.isExcludedGitLabPath("acc/scratch/x", List.of()));
    assertFalse(
        SyncService.isExcludedGitLabPath(null, List.of("acc/scratch")),
        "a project without a path is kept");
  }

  @Test
  void emptyResultsAreNoSoftware() throws Exception {
    assertEquals(
        List.of(),
        SyncService.parseGitHubSearch("{\"total_count\": 0, \"items\": []}", REPOSITORY));
    assertEquals(List.of(), SyncService.parseGitLabProjects("[]", REPOSITORY, List.of()));
  }

  @Test
  void unexpectedJsonFails() {
    // Such as an error object instead of results, or a project without a field sim reads
    String missingArchived =
        "[{\"name\": \"a\", \"web_url\": \"u\", \"description\": null, \"topics\": []}]";

    for (String json :
        new String[] {"{\"message\": \"Bad credentials\"}", "[]", "not json", missingArchived}) {
      UserFriendlyException e =
          assertThrows(
              UserFriendlyException.class,
              () -> {
                if (json.startsWith("[{")) {
                  SyncService.parseGitLabProjects(json, REPOSITORY, List.of());
                } else {
                  SyncService.parseGitHubSearch(json, REPOSITORY);
                }
              },
              json);
      assertEquals("Could not parse JSON", e.getUserMessage(), json);
    }

    assertThrows(
        UserFriendlyException.class,
        () ->
            SyncService.parseGitLabProjects(
                "{\"message\": \"401 Unauthorized\"}", REPOSITORY, List.of()));
  }

  @Test
  void typeTopicsCountInOrderAppLibScriptPlugin() {
    assertEquals(SoftwareType.APP, SyncService.typeFromTopics(List.of("plugin", "lib", "app")));
    assertEquals(SoftwareType.LIB, SyncService.typeFromTopics(List.of("plugin", "script", "lib")));
    assertEquals(SoftwareType.SCRIPT, SyncService.typeFromTopics(List.of("plugin", "script")));
    assertEquals(SoftwareType.PLUGIN, SyncService.typeFromTopics(List.of("epics", "plugin")));
    assertEquals(SoftwareType.APP, SyncService.typeFromTopics(List.of("epics")));
    assertEquals(SoftwareType.APP, SyncService.typeFromTopics(List.of()));
  }

  @Test
  void linkHeaderGivesPagesByRel() {
    // GitHub's format
    Map<String, String> links =
        SyncService.parseLinkHeader(
            "<https://api.github.com/search/repositories?q=topic%3Aacg&page=2>; rel=\"next\", "
                + "<https://api.github.com/search/repositories?q=topic%3Aacg&page=4>; rel=\"last\"");

    assertEquals(
        "https://api.github.com/search/repositories?q=topic%3Aacg&page=2", links.get("next"));
    assertEquals(
        "https://api.github.com/search/repositories?q=topic%3Aacg&page=4", links.get("last"));

    // GitLab's, on its last page
    links =
        SyncService.parseLinkHeader(
            "<https://code.jlab.org/api/v4/groups/acc/projects?page=1>; rel=\"first\","
                + "<https://code.jlab.org/api/v4/groups/acc/projects?page=3>; rel=\"prev\"");

    assertNull(links.get("next"));
    assertEquals("https://code.jlab.org/api/v4/groups/acc/projects?page=3", links.get("prev"));

    assertTrue(SyncService.parseLinkHeader("").isEmpty());
  }

  @Test
  void csueRowsWithFiveCellsAreApps() {
    String html =
        """
        <html><body><table>
          <tr><th>App</th><th>Host</th><th>Dir</th><th>Owner</th><th>Description</th></tr>
          <tr><td><a href="app.php?name=alarms">alarms</a></td><td>h1</td><td>/cs/apps/alarms</td>
              <td>dir owner:jdoe</td><td>Alarm handler</td></tr>
          <tr><td>nolink</td><td>h1</td><td>/cs/apps/x</td><td>dir owner:tbrown</td><td>X</td></tr>
          <tr><td colspan="5">A note row</td></tr>
        </table></body></html>
        """;

    List<Software> softwareList = SyncService.parseCSUE(Jsoup.parse(html), REPOSITORY);

    assertEquals(2, softwareList.size(), "the header and note rows are skipped");

    Software app = softwareList.get(0);
    assertEquals("alarms", app.getName());
    assertEquals(
        "https://opweb.acc.jlab.org/CSUEApps/csueTools/app.php?name=alarms", app.getHomeUrl());
    assertEquals("jdoe", app.getMaintainerUsernameCsv());
    assertEquals("Alarm handler", app.getDescription());
    assertEquals(SoftwareType.APP, app.getType());
    assertFalse(app.isArchivedSynced(), "CSUE doesn't say whether an app is archived");

    Software noLink = softwareList.get(1);
    assertEquals("", noLink.getName(), "a row without a link has no name");
    assertEquals("", noLink.getHomeUrl());
  }

  @Test
  void certifiedRowsWithTwoCellsAreProducts() {
    String html =
        """
        <table>
        <tr><td><a href='product.php?product_id=12'>myRestore</a></td><td>Restore IOCs</td></tr>
        <tr><td><a href='product.php?product_id=40'>fsd</a></td><td>Fast shutdown</td></tr>
        <tr><td>Three</td><td>cell</td><td>row</td></tr>
        </table>
        """;

    List<Software> softwareList =
        SyncService.parseCertified(Jsoup.parse(html), REPOSITORY, SoftwareType.LIB);

    assertEquals(2, softwareList.size());

    Software product = softwareList.get(0);
    assertEquals("myRestore", product.getName());
    assertEquals(
        "https://devweb.acc.jlab.org/controls_web/cjs/CSD/product.php?product_id=12",
        product.getHomeUrl());
    assertEquals("Restore IOCs", product.getDescription());
    assertEquals(SoftwareType.LIB, product.getType(), "the page's type");
    assertNull(product.getMaintainerUsernameCsv());
  }

  @Test
  void pagesWithoutTablesAreNoSoftware() {
    assertEquals(
        List.of(), SyncService.parseCSUE(Jsoup.parse("<p>Down for maintenance</p>"), REPOSITORY));
    assertEquals(
        List.of(),
        SyncService.parseCertified(
            Jsoup.parse("<p>Down for maintenance</p>"), REPOSITORY, SoftwareType.APP));
  }

  @Test
  void llappListingLinesAreApps() throws Exception {
    String text =
        """
        <pre>total 24
        drwxr-xr-x  4 jdoe   cs  4096 Mar  3  2021 bpmviewer
        drwxrwxr-x 12 tbrown cs  4096 Oct 21 09:14 orbit-tool
        lrwxrwxrwx  1 jsmith cs    22 Jan  5  2019 oldname -> orbit-tool
        """;

    List<Software> softwareList = SyncService.parseLLAPP(text, REPOSITORY);

    assertEquals(
        List.of("bpmviewer", "orbit-tool", "oldname"),
        softwareList.stream().map(Software::getName).toList(),
        "the ninth field; a link's target is ignored");
    assertEquals(
        List.of("jdoe", "tbrown", "jsmith"),
        softwareList.stream().map(Software::getMaintainerUsernameCsv).toList(),
        "the owner");

    Software app = softwareList.get(0);
    assertEquals(SoftwareType.APP, app.getType());
    assertNull(app.getDescription());
    assertNull(app.getHomeUrl());
    assertFalse(app.isArchivedSynced());
  }

  @Test
  void llappShortLineFails() {
    UserFriendlyException e =
        assertThrows(
            UserFriendlyException.class,
            () -> SyncService.parseLLAPP("<pre>total 4\n</pre>\n", REPOSITORY));

    assertEquals("unexpected line: </pre>", e.getUserMessage());
  }

  @Test
  void emptyLlappListingIsNoSoftware() throws Exception {
    assertEquals(List.of(), SyncService.parseLLAPP("", REPOSITORY));
    assertEquals(List.of(), SyncService.parseLLAPP("<pre>total 0\n", REPOSITORY));
  }
}
