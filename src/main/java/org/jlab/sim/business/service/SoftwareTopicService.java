package org.jlab.sim.business.service;

import jakarta.annotation.security.PermitAll;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import java.util.List;
import org.jlab.sim.persistence.entity.Software;
import org.jlab.sim.persistence.entity.SoftwareTopic;
import org.jlab.sim.persistence.entity.SoftwareTopicId;
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

  @PermitAll
  public void set(Software software, String[] topicNameArray) {

    // First, we remove / clear any existing
    List<SoftwareTopic> softwareTopics = findBySoftware(software);

    if (softwareTopics != null) {
      for (SoftwareTopic st : softwareTopics) {
        remove(st);
      }
    }

    // Next, we add the new ones
    if (topicNameArray != null) {
      for (String topicName : topicNameArray) {

        if (topicName == null || topicName.isBlank()) {
          continue;
        }

        SoftwareTopic st = new SoftwareTopic();
        st.setSoftware(software);

        Topic topic = topicService.findByName(topicName);

        if (topic == null) {
          topic = new Topic();
          topic.setName(topicName.toLowerCase());
          em.persist(topic);
          em.flush();
        }

        st.setTopic(topic);

        SoftwareTopicId SoftwareTopicId =
            new SoftwareTopicId(software.getSoftwareId(), topic.getTopicId());

        st.setSoftwareTopicId(SoftwareTopicId);

        create(st);
      }
    }
  }
}
