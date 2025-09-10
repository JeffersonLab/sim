package org.jlab.sim.business.service;

import jakarta.annotation.security.PermitAll;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.util.List;
import org.jlab.sim.persistence.entity.Software;
import org.jlab.sim.persistence.entity.SoftwareTopic;
import org.jlab.sim.persistence.entity.Topic;
import org.jlab.smoothness.business.service.JPAService;

@Stateless
public class SoftwareTopicService extends JPAService<SoftwareTopic> {
  @EJB private TopicService topicService;

  public SoftwareTopicService() {
    super(SoftwareTopic.class);
  }

  @PermitAll
  public List<SoftwareTopic> findBySoftware(Software software) {
    TypedQuery<SoftwareTopic> query =
        em.createQuery(
            "select s from SoftwareTopic s where s.software.softwareId = :softwareId",
            SoftwareTopic.class);

    query.setParameter("softwareId", software.getSoftwareId());

    return query.getResultList();
  }

  private void removeAllTopics(Software software) {
    Query q = em.createNativeQuery("delete from software_topic where software_id = ?");

    q.setParameter(1, software.getSoftwareId());

    q.executeUpdate();
  }

  private void addSoftwareTopic(Software software, Topic topic) {
    Query q =
        em.createNativeQuery("insert into software_topic (software_id, topic_id) values (?, ?)");

    q.setParameter(1, software.getSoftwareId());
    q.setParameter(2, topic.getTopicId());

    q.executeUpdate();
  }

  private void addTopics(Software software, String[] topicNameArray) {
    if (topicNameArray != null) {
      for (String topicName : topicNameArray) {

        if (topicName == null || topicName.isBlank()) {
          continue;
        }

        Topic topic = topicService.findByName(topicName);

        if (topic == null) {
          topic = new Topic();
          topic.setName(topicName.toLowerCase());
          em.persist(topic);
          em.flush();
        }

        addSoftwareTopic(software, topic);
      }
    }
  }

  @PermitAll
  public void set(Software software, String[] topicNameArray) {

    // First, we remove / clear any existing
    removeAllTopics(software);

    // Next, we add the new ones
    addTopics(software, topicNameArray);
  }
}
