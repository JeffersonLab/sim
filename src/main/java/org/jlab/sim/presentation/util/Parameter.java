package org.jlab.sim.presentation.util;

import javax.servlet.http.HttpServletRequest;
import org.jlab.sim.persistence.enumeration.Include;
import org.jlab.sim.persistence.enumeration.SoftwareType;

public class Parameter {
  public static SoftwareType convertSoftwareType(HttpServletRequest request, String parameterName) {
    String value = request.getParameter(parameterName);
    SoftwareType result = null;

    if (value != null && !value.isBlank()) {
      result = SoftwareType.valueOf(value);
    }

    return result;
  }

  public static Include convertInclude(HttpServletRequest request, String parameterName) {
    String value = request.getParameter(parameterName);
    Include result = null;

    if (value != null && !value.isBlank()) {
      result = Include.valueOf(value);
    }

    return result;
  }
}
