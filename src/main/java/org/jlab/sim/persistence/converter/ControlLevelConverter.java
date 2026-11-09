package org.jlab.sim.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jlab.sim.persistence.enumeration.ControlLevel;

@Converter(autoApply = true)
public class ControlLevelConverter implements AttributeConverter<ControlLevel, Integer> {

  @Override
  public Integer convertToDatabaseColumn(ControlLevel obj) {
    if (obj == null) {
      return null;
    }
    return obj.getValue();
  }

  @Override
  public ControlLevel convertToEntityAttribute(Integer value) {
    if (value == null) {
      return null;
    }
    return ControlLevel.fromValue(value);
  }
}
