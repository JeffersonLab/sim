package org.jlab.sim.presentation.controller;

import java.io.IOException;
import java.math.BigInteger;
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
import org.jlab.sim.persistence.entity.Repository;
import org.jlab.sim.persistence.entity.Software;
import org.jlab.sim.persistence.enumeration.SoftwareType;
import org.jlab.sim.presentation.util.Parameter;
import org.jlab.smoothness.business.service.JPAService;
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
    BigInteger repositoryId = ParamConverter.convertBigInteger(request, "repositoryId");

    int offset = ParamUtil.convertAndValidateNonNegativeInt(request, "offset", 0);
    int maxPerPage = 100;

    List<Software> softwareList =
        softwareService.findAll(new JPAService.OrderDirective("name", true));

    List<SoftwareType> typeList = Arrays.asList(SoftwareType.values());

    List<Repository> repoList =
        repositoryService.findAll(new JPAService.OrderDirective("name", true));

    request.setAttribute("repoList", repoList);
    request.setAttribute("typeList", typeList);
    request.setAttribute("softwareList", softwareList);

    request.getRequestDispatcher("/WEB-INF/views/directory.jsp").forward(request, response);
  }
}
