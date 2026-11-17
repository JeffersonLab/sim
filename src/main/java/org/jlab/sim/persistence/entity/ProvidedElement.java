package org.jlab.sim.persistence.entity;

/**
 * An SQA element that a repository or a workflow provides for all its software, so that it need not
 * be recorded on each product.
 */
public interface ProvidedElement {

  /** The SQAP's id for the element. */
  String getElementId();

  /** On what basis the element is provided. */
  String getNote();

  /** Where to read more, or null. */
  String getLinkUrl();
}
