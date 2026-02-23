package org.jlab.sim.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jlab.sim.persistence.enumeration.Esotericism;

@Converter(autoApply = true)
public class EsotericismConverter implements AttributeConverter<Esotericism, Integer> {

  @Override
  public Integer convertToDatabaseColumn(Esotericism obj) {
    if (obj == null) {
      return null;
    }
    return obj.getValue();
  }

  @Override
  public Esotericism convertToEntityAttribute(Integer value) {
    if (value == null) {
      return null;
    }
    return Esotericism.fromValue(value);
  }
}
