package org.jlab.sim.presentation.controller.ajax;

import jakarta.ejb.EJB;
import jakarta.ejb.EJBAccessException;
import jakarta.json.Json;
import jakarta.json.stream.JsonGenerator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.sim.business.service.SoftwareService;
import org.jlab.sim.persistence.enumeration.*;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.business.util.ExceptionUtil;
import org.jlab.smoothness.presentation.util.ParamConverter;

@WebServlet(
    name = "EditSoftwareRisk",
    urlPatterns = {"/ajax/edit-software-risk"})
public class EditSoftwareRisk extends HttpServlet {

  private static final Logger logger = Logger.getLogger(EditSoftwareRisk.class.getName());

  @EJB SoftwareService softwareService;

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String stat = "ok";
    String error = null;
    String name = null;

    try {
      BigInteger softwareId = ParamConverter.convertBigInteger(request, "softwareId");

      Integer impactValue = ParamConverter.convertInteger(request, "impact");
      Integer rateValue = ParamConverter.convertInteger(request, "rate");
      Integer difficultyValue = ParamConverter.convertInteger(request, "difficulty");
      Integer complexityValue = ParamConverter.convertInteger(request, "complexity");
      Integer gapsValue = ParamConverter.convertInteger(request, "gaps");
      Integer esotericismValue = ParamConverter.convertInteger(request, "esotericism");

      OpsImpact impact = OpsImpact.fromValue(impactValue);
      PastDowntimeRate rate = PastDowntimeRate.fromValue(rateValue);
      DebugTestDifficulty difficulty = DebugTestDifficulty.fromValue(difficultyValue);
      CodeComplexity complexity = CodeComplexity.fromValue(complexityValue);
      DocumentationGaps gaps = DocumentationGaps.fromValue(gapsValue);
      Esotericism esotericism = Esotericism.fromValue(esotericismValue);

      // Since name is key in other repos, must delete and add new for name changes.
      softwareService.editSoftwareRisk(
          softwareId, impact, rate, difficulty, complexity, gaps, esotericism);
    } catch (UserFriendlyException e) {
      stat = "fail";
      error = "Unable to edit Software Risk: " + e.getUserMessage();
    } catch (EJBAccessException e) {
      stat = "fail";
      error = "Unable to edit Software: Not authenticated / authorized (do you need to re-login?)";
    } catch (RuntimeException e) {
      stat = "fail";
      error = "Unable to edit Software Risk";
      logger.log(Level.SEVERE, "Unable to edit Software Risk", e);
      Throwable rootCause = ExceptionUtil.getRootCause(e);
      if ("OracleDatabaseException".equals(rootCause.getClass().getSimpleName())) {
        error = "Oracle Database Exception - make sure name doesn't already exist: " + name;
      }
    }

    response.setContentType("application/json");

    OutputStream out = response.getOutputStream();

    try (JsonGenerator gen = Json.createGenerator(out)) {
      gen.writeStartObject().write("stat", stat);
      if (error != null) {
        gen.write("error", error);
      }
      gen.writeEnd();
    }
  }
}
