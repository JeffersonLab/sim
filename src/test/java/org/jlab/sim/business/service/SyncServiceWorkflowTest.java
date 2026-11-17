package org.jlab.sim.business.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import org.jlab.sim.persistence.entity.Repository;
import org.jlab.sim.persistence.entity.Software;
import org.jlab.sim.persistence.entity.Workflow;
import org.jlab.sim.persistence.enumeration.SoftwareType;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;

/**
 * The workflow each sync proposes for the software it adds: the one its repository tells, and for a
 * GitLab project the certified workflow if it is in the certified group. The responses are made up,
 * in the format each repository returns.
 */
class SyncServiceWorkflowTest {

  private static final Repository REPOSITORY = new Repository();

  private static List<String> proposed(List<Software> softwareList) {
    return softwareList.stream().map(Software::getProposedWorkflowName).toList();
  }

  @Test
  void csueProposesCsue() {
    String html =
        """
        <table>
          <tr><td><a href="app.php?name=alarms">alarms</a></td><td>h1</td><td>/cs/apps/alarms</td>
              <td>dir owner:jdoe</td><td>Alarm handler</td></tr>
        </table>
        """;

    assertEquals(List.of("CSUE"), proposed(SyncService.parseCSUE(Jsoup.parse(html), REPOSITORY)));
  }

  @Test
  void lowLevelAppsProposeTheEpicsBuildSystem() throws Exception {
    String text =
        """
        <pre>total 8
        drwxr-xr-x  4 jdoe   cs  4096 Mar  3  2021 bpmviewer
        """;

    assertEquals(List.of("EPICS build system"), proposed(SyncService.parseLLAPP(text, REPOSITORY)));
  }

  @Test
  void theCertifiedListProposesLegacyCertified() {
    String html =
        "<table><tr><td><a href='product.php?product_id=12'>myRestore</a></td>"
            + "<td>Restore IOCs</td></tr></table>";

    assertEquals(
        List.of("Legacy Certified"),
        proposed(SyncService.parseCertified(Jsoup.parse(html), REPOSITORY, SoftwareType.APP)));
  }

  @Test
  void gitLabProposesTheCertifiedWorkflowInTheCertifiedGroupOnly() throws Exception {
    String json =
        """
        [
          {"name": "scratch", "path_with_namespace": "acc/scratch/scratch",
           "web_url": "https://code.jlab.org/acc/scratch/scratch", "description": null,
           "topics": [], "archived": false},
          {"name": "jmyapi", "path_with_namespace": "acc/ops/acs/certified/jmyapi",
           "web_url": "https://code.jlab.org/acc/ops/acs/certified/jmyapi", "description": null,
           "topics": [], "archived": false},
          {"name": "fsdlib", "path_with_namespace": "acc/ops/fsdlib",
           "web_url": "https://code.jlab.org/acc/ops/fsdlib", "description": null,
           "topics": [], "archived": false},
          {"name": "nopath", "web_url": "https://code.jlab.org/acc/nopath", "description": null,
           "topics": [], "archived": false}
        ]
        """;

    // The first project is left out, and the proposals still go with their projects
    List<Software> softwareList =
        SyncService.parseGitLabProjects(json, REPOSITORY, List.of("acc/scratch"));

    assertEquals(
        List.of("jmyapi", "fsdlib", "nopath"),
        softwareList.stream().map(Software::getName).toList());
    assertEquals(Arrays.asList("Certified", null, null), proposed(softwareList));
  }

  @Test
  void theCertifiedGroupIsAPathPrefixIgnoringCase() {
    assertEquals("Certified", SyncService.gitLabWorkflowName("acc/ops/acs/certified/jmyapi"));
    assertEquals("Certified", SyncService.gitLabWorkflowName("ACC/Ops/acs/Certified/libs/rflib"));
    assertNull(SyncService.gitLabWorkflowName("acc/ops/acs/certified"), "the group itself");
    assertNull(SyncService.gitLabWorkflowName("acc/ops/acs/certified-drafts/jmyapi"));
    assertNull(SyncService.gitLabWorkflowName("acc/ops/acs/jmyapi"));
    assertNull(SyncService.gitLabWorkflowName(null));
  }

  @Test
  void gitHubProposesNone() throws Exception {
    String json =
        """
        {"total_count": 1, "incomplete_results": false, "items": [
          {"name": "epics2web", "html_url": "https://github.com/JeffersonLab/epics2web",
           "description": "Web gateway", "topics": ["acg", "app"], "archived": false}
        ]}
        """;

    assertEquals(
        Arrays.asList((String) null), proposed(SyncService.parseGitHubSearch(json, REPOSITORY)));
  }

  @Test
  void aProposalBecomesTheWorkflowOfThatName() {
    Workflow certified = new Workflow(BigInteger.ONE, "Certified");
    Workflow csue = new Workflow(BigInteger.TWO, "CSUE");
    Software inCertifiedGroup = proposing("Certified");
    Software elsewhere = proposing(null);
    Software renamed = proposing("EPICS build system");

    SyncService.proposeWorkflows(
        List.of(inCertifiedGroup, elsewhere, renamed), List.of(certified, csue));

    assertSame(certified, inCertifiedGroup.getWorkflow());
    assertNull(elsewhere.getWorkflow(), "nothing proposed");
    assertNull(renamed.getWorkflow(), "a name no workflow has proposes none");
  }

  private static Software proposing(String workflowName) {
    Software software = new Software();
    software.setProposedWorkflowName(workflowName);
    return software;
  }
}
