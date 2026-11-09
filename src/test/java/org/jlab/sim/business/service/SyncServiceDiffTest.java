package org.jlab.sim.business.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jlab.sim.persistence.entity.Software;
import org.jlab.sim.persistence.entity.SoftwareTopic;
import org.jlab.sim.persistence.entity.Topic;
import org.jlab.sim.persistence.enumeration.SoftwareType;
import org.jlab.sim.persistence.view.SoftwareDiff;
import org.junit.jupiter.api.Test;

/**
 * How a sync sorts the software it fetched from a repository against the software already recorded:
 * to add, to remove, to update, or matched. The remote side only counts the fields a repository
 * provides, so an empty remote field never makes a record differ.
 */
class SyncServiceDiffTest {

  private final SyncService service = new SyncService();

  @Test
  void sameFieldsMatch() {
    SoftwareDiff diff =
        diff(
            List.of(local("epics2web", "Web gateway", "https://a/epics2web", false, "app", "web")),
            remote("epics2web", "Web gateway", "https://a/epics2web", false, true, "app", "web"));

    assertCounts(diff, 1, 0, 0, 0);
    assertFalse(diff.hasChanges());
  }

  @Test
  void changedDescriptionUpdates() {
    SoftwareDiff diff =
        diff(
            List.of(local("epics2web", "Web gateway", "https://a/epics2web", false)),
            remote("epics2web", "EPICS web gateway", "https://a/epics2web", false, true));

    assertCounts(diff, 0, 0, 0, 1);
    assertEquals("epics2web", diff.getUpdateList().get(0).getName());
    assertTrue(diff.hasChanges());
  }

  @Test
  void emptyRemoteDescriptionAndHomeUrlDontCount() {
    // Such as LLAPP, which provides neither
    SoftwareDiff diff =
        diff(
            List.of(local("myapp", "Recorded description", "https://a/myapp", false)),
            remote("myapp", null, " ", false, false));

    assertCounts(diff, 1, 0, 0, 0);
  }

  @Test
  void changedHomeUrlUpdates() {
    SoftwareDiff diff =
        diff(
            List.of(local("myapp", "d", "https://old/myapp", false)),
            remote("myapp", "d", "https://new/myapp", false, false));

    assertCounts(diff, 0, 0, 0, 1);
  }

  @Test
  void topicsMatchInAnyOrder() {
    SoftwareDiff diff =
        diff(
            List.of(local("myapp", "d", "u", false, "web", "app", "epics")),
            remote("myapp", "d", "u", false, true, "epics", "web", "app"));

    assertCounts(diff, 1, 0, 0, 0);
  }

  @Test
  void changedTopicsUpdate() {
    SoftwareDiff diff =
        diff(
            List.of(local("myapp", "d", "u", false, "app")),
            remote("myapp", "d", "u", false, true, "app", "web"));

    assertCounts(diff, 0, 0, 0, 1);
  }

  @Test
  void noRemoteTopicsDontCount() {
    SoftwareDiff diff =
        diff(
            List.of(local("myapp", "d", "u", false, "app")),
            remote("myapp", "d", "u", false, true));

    assertCounts(diff, 1, 0, 0, 0);
  }

  @Test
  void archivedCountsOnlyWhenTheRepositorySyncsIt() {
    // GitHub and GitLab say whether a project is archived; the other repositories don't
    List<Software> localList = List.of(local("myapp", "d", "u", false));

    assertCounts(diff(localList, remote("myapp", "d", "u", true, false)), 1, 0, 0, 0);
    assertCounts(diff(localList, remote("myapp", "d", "u", true, true)), 0, 0, 0, 1);
  }

  @Test
  void maintainersDontCount() {
    Software local = local("myapp", "d", "u", false);
    local.setMaintainerUsernameCsv("jdoe");
    Software remote = remote("myapp", "d", "u", false, false);
    remote.setMaintainerUsernameCsv("tbrown");

    assertCounts(diff(List.of(local), remote), 1, 0, 0, 0);
  }

  @Test
  void localOnlyIsRemoved() {
    SoftwareDiff diff =
        diff(
            List.of(local("kept", "d", "u", false), local("gone", "d", "u", false)),
            remote("kept", "d", "u", false, false));

    assertCounts(diff, 1, 0, 1, 0);
    assertEquals("gone", diff.getRemoveList().get(0).getName());
  }

  @Test
  void localWithoutNameIsRemoved() {
    SoftwareDiff diff = diff(List.of(local(null, "d", "u", false)));

    assertCounts(diff, 0, 0, 1, 0);
  }

  @Test
  void remoteOnlyIsAddedInRemoteOrder() {
    SoftwareDiff diff =
        diff(
            List.of(local("kept", "d", "u", false)),
            remote("zeta", "d", "u", false, false),
            remote("kept", "d", "u", false, false),
            remote("alpha", "d", "u", false, false));

    assertCounts(diff, 1, 2, 0, 0);
    assertEquals(
        List.of("zeta", "alpha"), diff.getAddList().stream().map(Software::getName).toList());
  }

  @Test
  void emptyRepositoryRemovesEverything() {
    SoftwareDiff diff = diff(List.of(local("a", "d", "u", false), local("b", "d", "u", false)));

    assertCounts(diff, 0, 0, 2, 0);
  }

  private SoftwareDiff diff(List<Software> localList, Software... remotes) {
    Map<String, Software> remoteMap = new LinkedHashMap<>();
    for (Software remote : remotes) {
      remoteMap.put(remote.getName(), remote);
    }
    return service.diff(localList, remoteMap);
  }

  private static void assertCounts(SoftwareDiff diff, int match, int add, int remove, int update) {
    assertEquals(match, diff.getMatchCount(), "match");
    assertEquals(add, diff.getAddCount(), "add");
    assertEquals(remove, diff.getRemoveCount(), "remove");
    assertEquals(update, diff.getUpdateCount(), "update");
  }

  /** Software as recorded and loaded from the database, with its topics as rows. */
  private static Software local(
      String name, String description, String homeUrl, boolean archived, String... topics) {
    Software software =
        new Software(null, name, SoftwareType.APP, description, null, homeUrl, archived, null);

    List<SoftwareTopic> softwareTopicList = new ArrayList<>();
    for (String topicName : topics) {
      Topic topic = new Topic();
      topic.setName(topicName);
      SoftwareTopic softwareTopic = new SoftwareTopic();
      softwareTopic.setSoftware(software);
      softwareTopic.setTopic(topic);
      softwareTopicList.add(softwareTopic);
    }
    software.setSoftwareTopicList(softwareTopicList);
    // As JPA does when it loads the software (@PostLoad)
    software.sortSoftwareTopicList();

    return software;
  }

  /** Software as a repository's fetch builds it, with its topics as strings. */
  private static Software remote(
      String name,
      String description,
      String homeUrl,
      boolean archived,
      boolean archivedSynced,
      String... topics) {
    Software software =
        new Software(null, name, SoftwareType.APP, description, null, homeUrl, archived, null);
    software.setArchivedSynced(archivedSynced);
    software.setStringTopicList(new ArrayList<>(Arrays.asList(topics)));
    return software;
  }
}
