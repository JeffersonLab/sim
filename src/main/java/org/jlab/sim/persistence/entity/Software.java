package org.jlab.sim.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;
import org.jlab.sim.persistence.enumeration.*;
import org.jlab.smoothness.persistence.util.YnStringToBoolean;

@Entity
@Table(name = "SOFTWARE", schema = "SIM_OWNER")
public class Software implements Serializable {
  private static final long serialVersionUID = 1L;

  static final String SOFTWAREID = "SOFTWARE_ID";

  @Id
  @SequenceGenerator(name = "SoftwareId", sequenceName = SOFTWAREID, allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SoftwareId")
  @Basic(optional = false)
  @NotNull
  @Column(name = SOFTWAREID, nullable = false, precision = 22, scale = 0)
  private BigInteger softwareId;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 128)
  @Column(name = "NAME", nullable = false, length = 128)
  private String name;

  @Basic(optional = false)
  @NotNull
  @Column(name = "TYPE", nullable = false, length = 64)
  @Enumerated(EnumType.STRING)
  private SoftwareType type;

  @Size(max = 256)
  @Column(name = "DESCRIPTION", nullable = true, length = 256)
  private String description;

  @Size(max = 256)
  @Column(name = "NOTE", nullable = true, length = 256)
  private String note;

  @NotNull
  @ManyToOne(optional = false)
  @JoinColumn(name = "REPOSITORY_ID", referencedColumnName = "REPOSITORY_ID", nullable = false)
  private Repository repository;

  @Size(max = 256)
  @Column(name = "HOME_URL", nullable = true, length = 256)
  private String homeUrl;

  @Size(max = 256)
  @Column(name = "MAINTAINER_USERNAME_CSV", nullable = true, length = 256)
  private String maintainerUsernameCsv;

  @Basic
  @Column(name = "ARCHIVED_YN", nullable = false, length = 1)
  @Convert(converter = YnStringToBoolean.class)
  private boolean archived;

  @NotNull
  @Column(name = "OPS_IMPACT", nullable = false)
  private OpsImpact impact;

  /** Derived by the database from impact and complexity; see 02_ddl.sql */
  @Column(name = "CONTROL_LEVEL", updatable = false, insertable = false)
  private ControlLevel controlLevel;

  @Column(name = "CONTROL_LEVEL_OVERRIDE", nullable = true)
  private ControlLevel controlLevelOverride;

  @Size(max = 512)
  @Column(name = "CONTROL_LEVEL_NOTE", nullable = true, length = 512)
  private String controlLevelNote;

  /** Derived by the database as the worst of the five risk factors; see 02_ddl.sql */
  @Column(name = "FRAGILITY", updatable = false, insertable = false)
  private Fragility fragility;

  /** Derived by the database from impact and fragility; see 02_ddl.sql */
  @Column(name = "ATTENTION", updatable = false, insertable = false)
  private Attention attention;

  @Size(max = 64)
  @Column(name = "ASSESSED_BY", nullable = true, length = 64)
  private String assessedBy;

  @Column(name = "ASSESSED_DATE", nullable = true)
  @Temporal(TemporalType.TIMESTAMP)
  private Date assessedDate;

  @NotNull
  @Column(name = "PAST_DOWNTIME", nullable = false)
  private PastDowntimeRate rate;

  @NotNull
  @Column(name = "DEBUGGABILITY_N_TESTING", nullable = false)
  private DebugTestDifficulty difficulty;

  @NotNull
  @Column(name = "CODE_COMPLEXITY", nullable = false)
  private CodeComplexity complexity;

  @NotNull
  @Column(name = "DOCUMENTATION_GAPS", nullable = false)
  private DocumentationGaps gaps;

  @NotNull
  @Column(name = "ESOTERICISM", nullable = false)
  private Esotericism esotericism;

  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(
      name = SOFTWAREID,
      referencedColumnName = SOFTWAREID,
      insertable = false,
      updatable = false)
  private List<SoftwareTopic> softwareTopicList;

  @Transient private List<String> stringTopicList;
  @Transient private boolean archivedSynced = false;

  public Software() {}

  public Software(
      Repository repository,
      String name,
      SoftwareType type,
      String description,
      String maintainerUsernameCsv,
      String homeUrl,
      boolean archived,
      String note) {
    this.repository = repository;
    this.name = name;
    this.type = type;
    this.description = description;
    this.maintainerUsernameCsv = maintainerUsernameCsv;
    this.homeUrl = homeUrl;
    this.archived = archived;
    this.note = note;

    this.complexity = CodeComplexity.Medium;
    this.esotericism = Esotericism.Medium;
    this.difficulty = DebugTestDifficulty.Medium;
    this.gaps = DocumentationGaps.Medium;
    this.rate = PastDowntimeRate.Some;
    this.impact = OpsImpact.Medium;
  }

  public BigInteger getSoftwareId() {
    return softwareId;
  }

  public void setSoftwareId(BigInteger softwareId) {
    this.softwareId = softwareId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public SoftwareType getType() {
    return type;
  }

  public void setType(SoftwareType type) {
    this.type = type;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public Repository getRepository() {
    return repository;
  }

  public void setRepository(Repository repository) {
    this.repository = repository;
  }

  public String getHomeUrl() {
    return homeUrl;
  }

  public void setHomeUrl(String homeUrl) {
    this.homeUrl = homeUrl;
  }

  public String getMaintainerUsernameCsv() {
    return maintainerUsernameCsv;
  }

  public boolean isArchived() {
    return archived;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
  }

  public void setMaintainerUsernameCsv(String maintainerUsernameCsv) {
    this.maintainerUsernameCsv = maintainerUsernameCsv;
  }

  @PostLoad
  public void sortSoftwareTopicList() {
    if (softwareTopicList != null) {
      Collections.sort(softwareTopicList);
    }
  }

  public List<SoftwareTopic> getSoftwareTopicList() {
    return softwareTopicList;
  }

  public void setSoftwareTopicList(List<SoftwareTopic> softwareTopicList) {
    this.softwareTopicList = softwareTopicList;
  }

  public String getTopicCsv() {
    String csv = "";
    if (softwareTopicList != null) {
      for (int i = 0; i < softwareTopicList.size(); i++) {
        SoftwareTopic topic = softwareTopicList.get(i);
        csv = csv + topic.getTopic().getName();
        if (i < softwareTopicList.size() - 1) {
          csv = csv + ",";
        }
      }
    }
    return csv;
  }

  public boolean isArchivedSynced() {
    return archivedSynced;
  }

  public void setArchivedSynced(boolean archivedSynced) {
    this.archivedSynced = archivedSynced;
  }

  public OpsImpact getImpact() {
    return impact;
  }

  public void setImpact(OpsImpact impact) {
    this.impact = impact;
  }

  /** The control level the database derived from impact and complexity, before any override. */
  public ControlLevel getControlLevel() {
    return controlLevel;
  }

  public ControlLevel getControlLevelOverride() {
    return controlLevelOverride;
  }

  public void setControlLevelOverride(ControlLevel controlLevelOverride) {
    this.controlLevelOverride = controlLevelOverride;
  }

  public String getControlLevelNote() {
    return controlLevelNote;
  }

  public void setControlLevelNote(String controlLevelNote) {
    this.controlLevelNote = controlLevelNote;
  }

  /** The control level that applies: the override if there is one, else the derived level. */
  public ControlLevel getEffectiveControlLevel() {
    return controlLevelOverride != null ? controlLevelOverride : controlLevel;
  }

  public Fragility getFragility() {
    return fragility;
  }

  public Attention getAttention() {
    return attention;
  }

  public String getAssessedBy() {
    return assessedBy;
  }

  public void setAssessedBy(String assessedBy) {
    this.assessedBy = assessedBy;
  }

  public Date getAssessedDate() {
    return assessedDate;
  }

  public void setAssessedDate(Date assessedDate) {
    this.assessedDate = assessedDate;
  }

  /** Whether somebody has assessed the risk, as opposed to the inputs still being the defaults. */
  public boolean isAssessed() {
    return assessedDate != null;
  }

  public PastDowntimeRate getRate() {
    return rate;
  }

  public void setRate(PastDowntimeRate rate) {
    this.rate = rate;
  }

  public DebugTestDifficulty getDifficulty() {
    return difficulty;
  }

  public void setDifficulty(DebugTestDifficulty difficulty) {
    this.difficulty = difficulty;
  }

  public CodeComplexity getComplexity() {
    return complexity;
  }

  public void setComplexity(CodeComplexity complexity) {
    this.complexity = complexity;
  }

  public DocumentationGaps getGaps() {
    return gaps;
  }

  public void setGaps(DocumentationGaps gaps) {
    this.gaps = gaps;
  }

  public Esotericism getEsotericism() {
    return esotericism;
  }

  public void setEsotericism(Esotericism esotericism) {
    this.esotericism = esotericism;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Software)) return false;
    Software software = (Software) o;
    return Objects.equals(softwareId, software.softwareId);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(softwareId);
  }

  public boolean syncEquals(Software other) {
    boolean equals = Objects.equals(name, other.name);

    // Only compare if other is non-empty.
    if (other.description != null && !other.description.isBlank()) {
      equals = equals && Objects.equals(description, other.description);
    }

    // Only compare if other is non-empty.
    String otherTopicCsv = other.getStringTopicCsv();
    if (otherTopicCsv != null && !otherTopicCsv.isBlank()) {
      equals = equals && Objects.equals(getTopicCsv(), otherTopicCsv);
    }

    // Only compare if other is non-empty.
    if (other.homeUrl != null && !other.homeUrl.isBlank()) {
      equals = equals && Objects.equals(homeUrl, other.homeUrl);
    }

    // Only compare if other is synced.
    if (other.archivedSynced) {
      equals = equals && Objects.equals(archived, other.archived);
    }

    /*if (other.maintainerUsernameCsv != null && !other.maintainerUsernameCsv.isBlank()) {
      equals = equals && Objects.equals(maintainerUsernameCsv, other.maintainerUsernameCsv);
    }*/

    return equals;
  }

  public List<String> getStringTopicList() {
    return stringTopicList;
  }

  public void setStringTopicList(List<String> stringTopicList) {
    this.stringTopicList = stringTopicList;
  }

  public String getStringTopicCsv() {
    String csv = "";
    if (stringTopicList != null) {
      Collections.sort(stringTopicList);
      for (int i = 0; i < stringTopicList.size(); i++) {
        String topic = stringTopicList.get(i);
        csv = csv + topic;
        if (i < stringTopicList.size() - 1) {
          csv = csv + ",";
        }
      }
    }
    return csv;
  }
}
