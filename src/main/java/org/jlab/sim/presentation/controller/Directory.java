package org.jlab.sim.presentation.controller;

import java.io.IOException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.sim.business.service.RepositoryService;
import org.jlab.sim.business.service.SoftwareService;
import org.jlab.sim.business.service.TopicService;
import org.jlab.sim.persistence.entity.Repository;
import org.jlab.sim.persistence.entity.Software;
import org.jlab.sim.persistence.entity.Topic;
import org.jlab.sim.persistence.enumeration.Include;
import org.jlab.sim.persistence.enumeration.SoftwareType;
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
public class Directory extends HttpServlet {

  @EJB SoftwareService softwareService;
  @EJB RepositoryService repositoryService;
  @EJB TopicService topicService;

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
    SoftwareType type = Parameter.convertSoftwareType(request, "type");
    String[] topicNameArray = request.getParameterValues("topic");
    BigInteger repositoryId = ParamConverter.convertBigInteger(request, "repositoryId");
    Include includeArchived = Parameter.convertInclude(request, "archived");

    int offset = ParamUtil.convertAndValidateNonNegativeInt(request, "offset", 0);
    Integer maxPerPage = ParamConverter.convertInteger(request, "max");

    if (maxPerPage == null || maxPerPage > 100 || maxPerPage < 1) {
      maxPerPage = 10;
    }

    List<Repository> repoList =
        repositoryService.findAll(new JPAService.OrderDirective("name", true));

    List<SoftwareType> typeList = Arrays.asList(SoftwareType.values());

    List<Include> includeList = Arrays.asList(Include.values());

    Repository repository = null;

    if (repositoryId != null) {
      repository = repositoryService.find(repositoryId);
    }

    List<Software> softwareList =
        softwareService.filterList(
            softwareName,
            username,
            repository,
            type,
            topicNameArray,
            includeArchived,
            offset,
            maxPerPage);

    long totalRecords =
        softwareService.countList(
            softwareName, username, repository, type, topicNameArray, includeArchived);

    Paginator paginator = new Paginator(totalRecords, offset, maxPerPage);

    String selectionMessage =
        createSelectionMessage(
            paginator, softwareName, username, repository, type, topicNameArray, includeArchived);

    List<Topic> topicList = topicService.getAll();

    request.setAttribute("topicList", topicList);
    request.setAttribute("selectionMessage", selectionMessage);
    request.setAttribute("repoList", repoList);
    request.setAttribute("typeList", typeList);
    request.setAttribute("includeList", includeList);
    request.setAttribute("softwareList", softwareList);
    request.setAttribute("paginator", paginator);

    if ("Y".equals(request.getParameter("loose"))) {
      request.getRequestDispatcher("/WEB-INF/views/directory-loose.jsp").forward(request, response);
    } else {
      request.getRequestDispatcher("/WEB-INF/views/directory.jsp").forward(request, response);
    }
  }

  private String createSelectionMessage(
      Paginator paginator,
      String softwareName,
      String username,
      Repository repository,
      SoftwareType type,
      String[] topicNameArray,
      Include includeArchived) {
    DecimalFormat formatter = new DecimalFormat("###,###");

    String selectionMessage = "All Software ";

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

    if (type != null) {
      filters.add("Type \"" + type + "\"");
    }

    if (topicNameArray != null && topicNameArray.length > 0) {
      filters.add("Topic \"" + String.join(",", topicNameArray) + "\"");
    }

    if (includeArchived != null) {
      filters.add("Include Archived \"" + includeArchived + "\"");
    }

    if (!filters.isEmpty()) {
      selectionMessage = filters.get(0);

      for (int i = 1; i < filters.size(); i++) {
        String filter = filters.get(i);
        selectionMessage += " and " + filter;
      }
    }

    if (paginator.getTotalRecords() < paginator.getMaxPerPage() && paginator.getOffset() == 0) {
      selectionMessage =
          selectionMessage + " {" + formatter.format(paginator.getTotalRecords()) + "}";
    } else {
      selectionMessage =
          selectionMessage
              + " {"
              + formatter.format(paginator.getStartNumber())
              + " - "
              + formatter.format(paginator.getEndNumber())
              + " of "
              + formatter.format(paginator.getTotalRecords())
              + "}";
    }

    return selectionMessage;
  }
}
