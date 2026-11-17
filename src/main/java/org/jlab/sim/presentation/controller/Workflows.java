package org.jlab.sim.presentation.controller;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jlab.sim.business.service.SqaElementService;
import org.jlab.sim.business.service.WorkflowService;
import org.jlab.sim.persistence.entity.SqaElement;
import org.jlab.sim.persistence.entity.Workflow;
import org.jlab.sim.persistence.entity.WorkflowElement;

/** The workflows software is released with, and the SQA elements each provides. */
@WebServlet(
    name = "Workflows",
    urlPatterns = {"/workflows"})
// false positive on @EJB lines (Injected EJB proxy IS Serializable)
@SuppressWarnings({"PMD.NonSerializableClass"})
public class Workflows extends HttpServlet {

  @Serial private static final long serialVersionUID = 1L;

  @EJB WorkflowService workflowService;
  @EJB SqaElementService elementService;

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

    List<Workflow> workflowList = workflowService.getAll();

    // The SQA elements each workflow provides for all the software released with it, in the
    // SQAP's order
    Map<String, SqaElement> elementsById = new HashMap<>();
    for (SqaElement element : elementService.getAll()) {
      elementsById.put(element.getElementId(), element);
    }
    Map<BigInteger, List<WorkflowElement>> provided = elementService.findProvidedByWorkflow();
    for (List<WorkflowElement> elements : provided.values()) {
      elements.sort(
          Comparator.comparingInt(
              (WorkflowElement element) -> elementsById.get(element.getElementId()).getWeight()));
    }

    request.setAttribute("workflowList", workflowList);
    request.setAttribute("provided", provided);
    request.setAttribute("elementsById", elementsById);

    request.getRequestDispatcher("/WEB-INF/views/workflows.jsp").forward(request, response);
  }
}
