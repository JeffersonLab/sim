package org.jlab.sim.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jlab.sim.persistence.enumeration.CodeComplexity;

@Converter(autoApply = true)
public class CodeComplexityConverter implements AttributeConverter<CodeComplexity, Integer> {

  @Override
  public Integer convertToDatabaseColumn(CodeComplexity obj) {
    if (obj == null) {
      return null;
    }
    return obj.getValue();
  }

  @Override
  public CodeComplexity convertToEntityAttribute(Integer value) {
    if (value == null) {
      return null;
    }
    return CodeComplexity.fromValue(value);
  }
}
