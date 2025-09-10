package org.jlab.sim.presentation.controller;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.jlab.sim.business.service.RepositoryService;
import org.jlab.sim.persistence.entity.Repository;
import org.jlab.smoothness.business.service.JPAService;

/**
 * @author ryans
 */
@WebServlet(
    name = "Repositories",
    urlPatterns = {"/repositories"})
public class Repositories extends HttpServlet {

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

    List<Repository> repoList =
        repositoryService.findAll(new JPAService.OrderDirective("name", true));

    request.setAttribute("repoList", repoList);

    request.getRequestDispatcher("/WEB-INF/views/repositories.jsp").forward(request, response);
  }
}
