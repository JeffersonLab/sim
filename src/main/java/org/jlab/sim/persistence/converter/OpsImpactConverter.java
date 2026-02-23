package org.jlab.sim.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jlab.sim.persistence.enumeration.OpsImpact;

@Converter(autoApply = true)
public class OpsImpactConverter implements AttributeConverter<OpsImpact, Integer> {

  @Override
  public Integer convertToDatabaseColumn(OpsImpact obj) {
    if (obj == null) {
      return null;
    }
    return obj.getValue();
  }

  @Override
  public OpsImpact convertToEntityAttribute(Integer value) {
    if (value == null) {
      return null;
    }
    return OpsImpact.fromValue(value);
  }
}
