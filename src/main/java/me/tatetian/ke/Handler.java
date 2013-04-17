package me.tatetian.ke;

import com.hp.hpl.jena.ontology.*;

public abstract class Handler {
  protected OntModel model;
  
  public Handler(OntModel model) {
    this.model = model;
  }
  
  public abstract void handleLine(String line);
}
