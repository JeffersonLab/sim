package org.jlab.sim.presentation.util;

import jakarta.servlet.http.HttpServletRequest;
import org.jlab.sim.persistence.enumeration.Attention;
import org.jlab.sim.persistence.enumeration.ControlLevel;
import org.jlab.sim.persistence.enumeration.Include;
import org.jlab.sim.persistence.enumeration.SoftwareType;

public class Parameter {
  private static final String YES = "Y";
  private static final String NO = "N";

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

  /**
   * A control level by its number, 0 to 3, or null if the parameter is blank.
   *
   * @throws IllegalArgumentException if the value isn't a level's number
   */
  public static ControlLevel convertControlLevel(HttpServletRequest request, String parameterName) {
    String value = request.getParameter(parameterName);
    ControlLevel result = null;

    if (value != null && !value.isBlank()) {
      result = ControlLevel.fromValue(Integer.parseInt(value.trim()));
    }

    return result;
  }

  /**
   * An attention band by its number, 1 to 4, or null if the parameter is blank.
   *
   * @throws IllegalArgumentException if the value isn't a band's number
   */
  public static Attention convertAttention(HttpServletRequest request, String parameterName) {
    String value = request.getParameter(parameterName);
    Attention result = null;

    if (value != null && !value.isBlank()) {
      result = Attention.fromValue(Integer.parseInt(value.trim()));
    }

    return result;
  }

  /**
   * A Y or N parameter as true or false, or null if it is blank.
   *
   * @throws IllegalArgumentException if the value is neither
   */
  public static Boolean convertYesNo(HttpServletRequest request, String parameterName) {
    String value = request.getParameter(parameterName);
    Boolean result = null;

    if (value != null && !value.isBlank()) {
      if (YES.equals(value)) {
        result = true;
      } else if (NO.equals(value)) {
        result = false;
      } else {
        throw new IllegalArgumentException(parameterName + " must be Y or N: " + value);
      }
    }

    return result;
  }
}
