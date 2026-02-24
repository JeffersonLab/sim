package org.jlab.sim.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jlab.sim.persistence.enumeration.DowntimeRisk;

@Converter(autoApply = true)
public class DowntimeRiskConverter implements AttributeConverter<DowntimeRisk, Integer> {

  @Override
  public Integer convertToDatabaseColumn(DowntimeRisk obj) {
    if (obj == null) {
      return null;
    }
    return obj.getValue();
  }

  @Override
  public DowntimeRisk convertToEntityAttribute(Integer value) {
    if (value == null) {
      return null;
    }
    return DowntimeRisk.fromValue(value);
  }
}
