package org.jlab.sim.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jlab.sim.persistence.enumeration.DocumentationGaps;

@Converter(autoApply = true)
public class DocumentationGapsConverter implements AttributeConverter<DocumentationGaps, Integer> {

  @Override
  public Integer convertToDatabaseColumn(DocumentationGaps obj) {
    if (obj == null) {
      return null;
    }
    return obj.getValue();
  }

  @Override
  public DocumentationGaps convertToEntityAttribute(Integer value) {
    if (value == null) {
      return null;
    }
    return DocumentationGaps.fromValue(value);
  }
}
