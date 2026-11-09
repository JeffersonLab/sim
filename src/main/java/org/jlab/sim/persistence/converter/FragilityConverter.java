package org.jlab.sim.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jlab.sim.persistence.enumeration.Fragility;

@Converter(autoApply = true)
public class FragilityConverter implements AttributeConverter<Fragility, Integer> {

  @Override
  public Integer convertToDatabaseColumn(Fragility obj) {
    if (obj == null) {
      return null;
    }
    return obj.getValue();
  }

  @Override
  public Fragility convertToEntityAttribute(Integer value) {
    if (value == null) {
      return null;
    }
    return Fragility.fromValue(value);
  }
}
