package org.jlab.sim.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jlab.sim.persistence.enumeration.DowntimeProbability;

@Converter(autoApply = true)
public class DowntimeProbabilityConverter
    implements AttributeConverter<DowntimeProbability, Integer> {

  @Override
  public Integer convertToDatabaseColumn(DowntimeProbability obj) {
    if (obj == null) {
      return null;
    }
    return obj.getValue();
  }

  @Override
  public DowntimeProbability convertToEntityAttribute(Integer value) {
    if (value == null) {
      return null;
    }
    return DowntimeProbability.fromValue(value);
  }
}
