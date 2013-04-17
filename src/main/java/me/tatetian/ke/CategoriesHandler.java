package me.tatetian.ke;

import java.util.Iterator;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;

public class CategoriesHandler extends Handler {
  public CategoriesHandler(OntModel model) {
    super(model);
  }
  
  public void handleLine(String line) {
    line = line.trim();
    
    int pos = line.indexOf('\t');
    if(pos <= 0) throw new RuntimeException("Can't find tab in line!");
    
    String    superCategoryName = line.substring(0, pos).trim();
    String[]  subCategoryNames  = line.substring(pos + 1).split(";");
    OntClass  superClass = model.createClass(BaikeOntology.NS_C + superCategoryName);
    for(String subName : subCategoryNames) {
      //System.out.print(subName + "|");
      OntClass subClass = model.createClass(BaikeOntology.NS_C + subName);
      superClass.addSubClass(subClass);
    }
    //System.out.println("");
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    Iterator<OntClass> ontClasses = model.listClasses();
    while (ontClasses.hasNext()) {
      OntClass ontClass = ontClasses.next();
      sb.append(ontClass.getURI());
      sb.append("\n");
    }
    return sb.toString();
  }
}
