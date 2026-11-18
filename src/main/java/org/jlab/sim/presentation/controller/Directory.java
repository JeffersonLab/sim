package org.jlab.sim.presentation.controller;

import jakarta.ejb.EJB;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.jlab.sim.business.service.RepositoryService;
import org.jlab.sim.business.service.SoftwareService;
import org.jlab.sim.business.service.SqaElementService;
import org.jlab.sim.business.service.TopicService;
import org.jlab.sim.business.service.WorkflowService;
import org.jlab.sim.persistence.entity.ProvidedElement;
import org.jlab.sim.persistence.entity.Repository;
import org.jlab.sim.persistence.entity.RepositoryElement;
import org.jlab.sim.persistence.entity.Software;
import org.jlab.sim.persistence.entity.SoftwareElement;
import org.jlab.sim.persistence.entity.SqaElement;
import org.jlab.sim.persistence.entity.Topic;
import org.jlab.sim.persistence.entity.Workflow;
import org.jlab.sim.persistence.entity.WorkflowElement;
import org.jlab.sim.persistence.enumeration.*;
import org.jlab.sim.persistence.view.Checklist;
import org.jlab.sim.persistence.view.ComputedElements;
import org.jlab.sim.presentation.util.Parameter;
import org.jlab.smoothness.business.service.JPAService;
import org.jlab.smoothness.presentation.util.Paginator;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.smoothness.presentation.util.ParamUtil;

/**
 * @author ryans
 */
@WebServlet(
    name = "Directory",
    urlPatterns = {"/directory"})
// false positive on @EJB lines (Injected EJB proxy IS Serializable)
@SuppressWarnings({"PMD.NonSerializableClass"})
public class Directory extends HttpServlet {

  @Serial private static final long serialVersionUID = 1L;
  private static final String LOOSE_PARAMETER = "loose";
  private static final String LOOSE_TRUE_VALUE = "Y";

  /** The workflow filter's value for software that has no workflow, where an id names one */
  private static final String NO_WORKFLOW = "NONE";

  // Section 4.4 when no element says which revision of the SQAP it is from
  private static final String SQAP_STAGES_URL =
      "https://pages.jlab.org/acc/iac/docs/sqap/latest/04-software-development-life-cycle/#44-life-cycle-stages";

  @EJB SoftwareService softwareService;
  @EJB RepositoryService repositoryService;
  @EJB TopicService topicService;
  @EJB SqaElementService elementService;
  @EJB WorkflowService workflowService;

  /**
   * Handles the HTTP <code>GET</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String softwareName = request.getParameter("softwareName");
    String username = request.getParameter("username");
    String[] topicNameArray = request.getParameterValues("topic");
    SoftwareType type;
    BigInteger repositoryId;
    String workflowParam = request.getParameter("workflowId");
    boolean noWorkflow = NO_WORKFLOW.equals(workflowParam);
    BigInteger workflowId = null;
    Include includeArchived;
    ControlLevel level;
    Attention attention;
    boolean fragile;
    Boolean assessed;
    String gapParam = request.getParameter("gap");
    String gapElementId = gapParam == null || gapParam.isBlank() ? null : gapParam;
    int offset;
    Integer maxPerPage;

    try {
      type = Parameter.convertSoftwareType(request, "type");
      repositoryId = ParamConverter.convertBigInteger(request, "repositoryId");
      if (!noWorkflow) {
        workflowId = ParamConverter.convertBigInteger(request, "workflowId");
      }
      includeArchived = Parameter.convertInclude(request, "archived");
      level = Parameter.convertControlLevel(request, "level");
      attention = Parameter.convertAttention(request, "attention");
      fragile = Boolean.TRUE.equals(Parameter.convertYesNo(request, "fragile"));
      assessed = Parameter.convertYesNo(request, "assessed");
      offset = ParamUtil.convertAndValidateNonNegativeInt(request, "offset", 0);
      maxPerPage = ParamConverter.convertInteger(request, "max");
    } catch (IllegalArgumentException e) {
      // Such as a type that isn't one, a number that doesn't parse, or a negative offset
      response.sendError(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    if (maxPerPage == null || maxPerPage > 100 || maxPerPage < 1) {
      maxPerPage = 10;
    }

    List<SqaElement> elementList = elementService.getAll();
    SqaElement gapElement = null;

    if (gapElementId != null && !SqaElementService.ANY_ELEMENT.equals(gapElementId)) {
      for (SqaElement element : elementList) {
        if (element.getElementId().equals(gapElementId)) {
          gapElement = element;
        }
      }

      // Rather than list nothing, as if no software had the gap
      if (gapElement == null) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        return;
      }
    }

    List<Repository> repoList =
        repositoryService.findAll(new JPAService.OrderDirective("name", true));

    List<SoftwareType> typeList = Arrays.asList(SoftwareType.values());

    List<Include> includeList = Arrays.asList(Include.values());

    Repository repository = null;

    if (repositoryId != null) {
      repository = repositoryService.find(repositoryId);

      // Rather than list all software as if there were no filter
      if (repository == null) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        return;
      }
    }

    List<Workflow> workflowList = workflowService.getAll();

    Workflow workflow = null;

    if (workflowId != null) {
      workflow = workflowService.find(workflowId);

      // Rather than list all software as if there were no filter
      if (workflow == null) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        return;
      }
    }

    List<Software> softwareList =
        softwareService.filterList(
            softwareName,
            username,
            repository,
            workflow,
            noWorkflow,
            type,
            topicNameArray,
            includeArchived,
            level,
            attention,
            fragile,
            assessed,
            gapElementId,
            SoftwareOrder.IMPACT,
            offset,
            maxPerPage);

    long totalRecords =
        softwareService.countList(
            softwareName,
            username,
            repository,
            workflow,
            noWorkflow,
            type,
            topicNameArray,
            includeArchived,
            level,
            attention,
            fragile,
            assessed,
            gapElementId);

    Paginator paginator = new Paginator(totalRecords, offset, maxPerPage);

    String selectionMessage =
        createSelectionMessage(
            paginator,
            softwareName,
            username,
            repository,
            workflow,
            noWorkflow,
            type,
            topicNameArray,
            includeArchived,
            level,
            attention,
            fragile,
            assessed,
            gapElementId,
            gapElement);

    // Each assessed product's checklist: unassessed software has no control level to choose its
    // elements by
    Map<BigInteger, List<SoftwareElement>> recorded = elementService.findRecorded(softwareList);
    Map<BigInteger, List<RepositoryElement>> provided = elementService.findProvided();
    Map<BigInteger, List<WorkflowElement>> providedByWorkflow =
        elementService.findProvidedByWorkflow();
    Map<BigInteger, Checklist> checklists = new HashMap<>();
    Map<BigInteger, String> checklistJson = new HashMap<>();
    for (Software software : softwareList) {
      if (software.isAssessed()) {
        Checklist checklist =
            Checklist.of(
                elementList,
                software.getEffectiveControlLevel(),
                recorded.getOrDefault(software.getSoftwareId(), List.of()),
                provisions(software, providedByWorkflow, provided),
                ComputedElements.of(software));
        checklists.put(software.getSoftwareId(), checklist);
        checklistJson.put(software.getSoftwareId(), toJson(checklist));
      }
    }

    List<Topic> topicList = topicService.getAll();

    request.setAttribute("topicList", topicList);
    request.setAttribute("selectionMessage", selectionMessage);
    request.setAttribute("repoList", repoList);
    request.setAttribute("workflowList", workflowList);
    request.setAttribute("typeList", typeList);
    request.setAttribute("includeList", includeList);
    request.setAttribute("softwareList", softwareList);
    request.setAttribute("paginator", paginator);
    request.setAttribute("levelList", Arrays.asList(ControlLevel.values()));
    request.setAttribute("attentionList", Arrays.asList(Attention.values()));
    request.setAttribute("fragilityList", Arrays.asList(Fragility.values()));
    request.setAttribute("impactList", Arrays.asList(OpsImpact.values()));
    request.setAttribute("rateList", Arrays.asList(PastDowntimeRate.values()));
    request.setAttribute("difficultyList", Arrays.asList(DebugTestDifficulty.values()));
    request.setAttribute("complexityList", Arrays.asList(CodeComplexity.values()));
    request.setAttribute("gapList", Arrays.asList(DocumentationGaps.values()));
    request.setAttribute("esotericismList", Arrays.asList(Esotericism.values()));
    request.setAttribute("elementList", elementList);
    request.setAttribute("elementStatusList", Arrays.asList(ElementStatus.values()));
    request.setAttribute("checklists", checklists);
    request.setAttribute("checklistJson", checklistJson);
    request.setAttribute(
        "sqapVersion", elementList.isEmpty() ? null : elementList.get(0).getSqapVersion());
    request.setAttribute("sqapStagesUrl", stagesUrl(elementList));

    if (isLooseModeRequested(request)) {
      request.getRequestDispatcher("/WEB-INF/views/directory-loose.jsp").forward(request, response);
    } else {
      request.getRequestDispatcher("/WEB-INF/views/directory.jsp").forward(request, response);
    }
  }

  /**
   * The SQAP's section 4.4, Life Cycle Stages, in the revision the elements were loaded from: on
   * the page of an element of one of its subsections, which links to the website's folder for a
   * released revision and to the source at its commit for a draft.
   */
  static String stagesUrl(List<SqaElement> elements) {
    for (SqaElement element : elements) {
      if (element.getSectionNumber() != null && element.getSectionNumber().startsWith("4.4.")) {
        String url = element.getSectionUrl();
        int anchor = url.indexOf('#');
        return (anchor < 0 ? url : url.substring(0, anchor)) + "#44-life-cycle-stages";
      }
    }
    return SQAP_STAGES_URL;
  }

  /**
   * What is provided for a product without being recorded on it: what its workflow provides for all
   * the software released with it, then what its repository provides for all its software, each
   * named for its provider. The workflow's come first, so that it names the provider of an element
   * both provide: how software is released says more of it than where it lives.
   */
  private static List<Checklist.Provision> provisions(
      Software software,
      Map<BigInteger, List<WorkflowElement>> providedByWorkflow,
      Map<BigInteger, List<RepositoryElement>> providedByRepository) {
    List<Checklist.Provision> provisions = new ArrayList<>();
    Workflow workflow = software.getWorkflow();
    if (workflow != null) {
      addProvisions(
          provisions,
          workflow.getPhrase(),
          providedByWorkflow.getOrDefault(workflow.getWorkflowId(), List.of()));
    }
    Repository repository = software.getRepository();
    addProvisions(
        provisions,
        repository.getName(),
        providedByRepository.getOrDefault(repository.getRepositoryId(), List.of()));
    return provisions;
  }

  private static void addProvisions(
      List<Checklist.Provision> provisions,
      String provider,
      List<? extends ProvidedElement> elements) {
    for (ProvidedElement element : elements) {
      provisions.add(
          new Checklist.Provision(
              element.getElementId(), provider, element.getNote(), element.getLinkUrl()));
    }
  }

  /**
   * A checklist for the page's dialog (directory.js): its elements in order, each with its
   * requirement and what was recorded of it.
   */
  private static String toJson(Checklist checklist) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.US);
    JsonArrayBuilder items = Json.createArrayBuilder();
    for (Checklist.Item item : checklist.getItems()) {
      JsonObjectBuilder json =
          Json.createObjectBuilder()
              .add("id", item.getElement().getElementId())
              .add("requirement", item.getRequirement().name());
      if (item.getStatus() != null) {
        json.add("status", item.getStatus().name())
            .add("link", item.getLinkUrl() == null ? "" : item.getLinkUrl())
            .add("note", item.getNote() == null ? "" : item.getNote())
            .add("by", item.getRecordedBy())
            .add("date", dateFormat.format(item.getRecordedDate()));
      }
      Checklist.Finding finding = item.getFinding();
      if (finding != null) {
        json.add("computed", finding.getStatus().name()).add("computedNote", finding.getNote());
      }
      Checklist.Provision provision = item.getProvision();
      if (provision != null) {
        json.add("provider", provision.getProvider())
            .add("providerNote", provision.getNote())
            .add("providerLink", provision.getLinkUrl() == null ? "" : provision.getLinkUrl());
      }
      items.add(json);
    }
    return items.build().toString();
  }

  private boolean isLooseModeRequested(HttpServletRequest request) {
    return LOOSE_TRUE_VALUE.equals(request.getParameter(LOOSE_PARAMETER));
  }

  private String createSelectionMessage(
      Paginator paginator,
      String softwareName,
      String username,
      Repository repository,
      Workflow workflow,
      boolean noWorkflow,
      SoftwareType type,
      String[] topicNameArray,
      Include includeArchived,
      ControlLevel level,
      Attention attention,
      boolean fragile,
      Boolean assessed,
      String gapElementId,
      SqaElement gapElement) {
    DecimalFormat formatter = new DecimalFormat("###,###");

    StringBuilder selectionMessage = new StringBuilder("All Software ");

    List<String> filters = new ArrayList<>();

    if (softwareName != null && !softwareName.isBlank()) {
      filters.add("Name \"" + softwareName + "\"");
    }

    if (username != null && !username.isBlank()) {
      filters.add("Username \"" + username + "\"");
    }

    if (repository != null) {
      filters.add("Repository \"" + repository.getName() + "\"");
    }

    if (noWorkflow) {
      filters.add("No Workflow");
    } else if (workflow != null) {
      filters.add("Workflow \"" + workflow.getName() + "\"");
    }

    if (type != null) {
      filters.add("Type \"" + type + "\"");
    }

    if (topicNameArray != null && topicNameArray.length > 0) {
      filters.add("Topic \"" + String.join(",", topicNameArray) + "\"");
    }

    if (includeArchived != null) {
      filters.add("Include Archived \"" + includeArchived + "\"");
    }

    if (level != null) {
      filters.add("SQA Level \"" + level.getValue() + " " + level + "\"");
    }

    if (attention != null) {
      filters.add("Attention \"" + attention + "\"");
    }

    if (fragile) {
      filters.add("Any Risk Factor \u2265 4");
    }

    if (assessed != null) {
      filters.add(assessed ? "Assessed" : "Not Assessed");
    }

    if (gapElement != null) {
      filters.add("Gap in \"" + gapElement.getShortName() + "\"");
    } else if (gapElementId != null) {
      filters.add("Gap in any SQA Element");
    }

    if (!filters.isEmpty()) {
      selectionMessage = new StringBuilder(filters.get(0));

      for (int i = 1; i < filters.size(); i++) {
        String filter = filters.get(i);
        selectionMessage.append(" and ").append(filter);
      }
    }

    if (paginator.getTotalRecords() < paginator.getMaxPerPage() && paginator.getOffset() == 0) {
      selectionMessage
          .append(" {")
          .append(formatter.format(paginator.getTotalRecords()))
          .append("}");
    } else {
      selectionMessage
          .append(" {")
          .append(formatter.format(paginator.getStartNumber()))
          .append(" - ")
          .append(formatter.format(paginator.getEndNumber()))
          .append(" of ")
          .append(formatter.format(paginator.getTotalRecords()))
          .append("}");
    }

    return selectionMessage.toString();
  }
}
