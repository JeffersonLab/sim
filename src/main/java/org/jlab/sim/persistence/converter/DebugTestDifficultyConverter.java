package org.jlab.sim.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jlab.sim.persistence.enumeration.DebugTestDifficulty;

@Converter(autoApply = true)
public class DebugTestDifficultyConverter
    implements AttributeConverter<DebugTestDifficulty, Integer> {

  @Override
  public Integer convertToDatabaseColumn(DebugTestDifficulty obj) {
    if (obj == null) {
      return null;
    }
    return obj.getValue();
  }

  @Override
  public DebugTestDifficulty convertToEntityAttribute(Integer value) {
    if (value == null) {
      return null;
    }
    return DebugTestDifficulty.fromValue(value);
  }
}
