package org.jlab.sim.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jlab.sim.persistence.enumeration.Attention;

@Converter(autoApply = true)
public class AttentionConverter implements AttributeConverter<Attention, Integer> {

  @Override
  public Integer convertToDatabaseColumn(Attention obj) {
    if (obj == null) {
      return null;
    }
    return obj.getValue();
  }

  @Override
  public Attention convertToEntityAttribute(Integer value) {
    if (value == null) {
      return null;
    }
    return Attention.fromValue(value);
  }
}
