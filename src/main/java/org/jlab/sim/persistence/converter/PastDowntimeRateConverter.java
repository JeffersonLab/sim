package org.jlab.sim.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jlab.sim.persistence.enumeration.PastDowntimeRate;

@Converter(autoApply = true)
public class PastDowntimeRateConverter implements AttributeConverter<PastDowntimeRate, Integer> {

  @Override
  public Integer convertToDatabaseColumn(PastDowntimeRate obj) {
    if (obj == null) {
      return null;
    }
    return obj.getValue();
  }

  @Override
  public PastDowntimeRate convertToEntityAttribute(Integer value) {
    if (value == null) {
      return null;
    }
    return PastDowntimeRate.fromValue(value);
  }
}
