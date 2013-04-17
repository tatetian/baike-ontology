package me.tatetian.ke;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class BaikeOntology {
  
  public static final String NS_BASE = "http://tatetian.me/baike/"; // base namespace
  public static final String NS_C = NS_BASE + "categories/"; // ns for categories
  public static final String NS_P = NS_BASE + "properties/"; // ns for properties
  public static final String NS_A = NS_BASE + "articles/";   // ns for articles
  
  private String categoriesFile = null, articlesFile = null;
  private OntModel model = null;
  
  public BaikeOntology(String categoriesFile, String articlesFile) {
    this.categoriesFile = categoriesFile;
    this.articlesFile   = articlesFile;
    this.model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
  }
  
  public void process() throws IOException {
    BaikeUtil.loadCategoriesFile(model, categoriesFile);
    BaikeUtil.loadArticlesFile(model, articlesFile);
  }
  
  public void print() {
    // Fast RDFS reasoner
    Reasoner reasoner = ReasonerRegistry.getRDFSSimpleReasoner();
    reasoner = reasoner.bindSchema(model);
    // Obtain standard OWL-RDFS spec
    OntModelSpec ontModelSpec = OntModelSpec.OWL_MEM_RDFS_INF;
    ontModelSpec.setReasoner(reasoner);
    // Create ontology model with reasoner support
    OntModel newModel = ModelFactory.createOntologyModel(ontModelSpec, model);
    // Get root classes
    String rootClassURI = BaikeUtil.getCategoryURI("Root");
    OntClass _rootClass  = newModel.getOntClass(rootClassURI);
    assert(_rootClass != null);
    ExtendedIterator<OntClass> iterRootClass = _rootClass.listSubClasses(true);
    // Iterate each root class
    int rootClassIndex = 1;
    while(iterRootClass.hasNext()) {
      OntClass rootClass = iterRootClass.next();
      System.out.println("-------Class " + rootClass.getURI() + ": <" + rootClassURI + ">-------");
      // Count subclasses
      int countSubclasses = 0;
      ExtendedIterator<OntClass> iterSubclass = rootClass.listSubClasses(false);
      while(iterSubclass.hasNext()) {
        iterSubclass.next();
        countSubclasses ++;
      } 
      System.out.println("# of subclasses = " + countSubclasses);
      // Count instances
      int countInstances = 0;
      int resourceProperties = 0, literalProperties = 0;
      ExtendedIterator<? extends OntResource> iterIndividual = rootClass.listInstances();
      while(iterIndividual.hasNext()) {
        Individual individual = iterIndividual.next().asIndividual();
        countInstances ++;

        StmtIterator iterStatement = individual.listProperties();
        while(iterStatement.hasNext()) {
          Statement stmt = iterStatement.next();
          RDFNode object = stmt.getObject(); 
          if(object.isLiteral()) literalProperties ++;
          else if(object.isResource()) resourceProperties ++;
        }
      }
      System.out.println("# of instances = " + countInstances);
      System.out.println("# of datatype properties = " + literalProperties);
      System.out.println("# of object properties = " + countInstances);
      //System.out.println(x)
//    while (objectProperties.hasNext()) {
//      ObjectProperty objectProperty = objectProperties.next();
//      System.out.println(objectProperty.getURI());
//    }
//      StmtIterator iterStatement = rootClass.listProperties();
//      int resourceProperties = 0, literalProperties = 0;
//      while(iterStatement.hasNext()) {
//        Statement stmt = iterStatement.next();
//        RDFNode object = stmt.getObject(); 
//        if(object.isLiteral()) literalProperties ++;
//        else if(object.isResource()) resourceProperties ++;
//      }
      
      rootClassIndex ++;
    }
    // TODO: is this what we want?
    // Count object properties
    int countObjectProperties = 0;
    Iterator<ObjectProperty> iterObjectProperty = model.listObjectProperties();
    while(iterObjectProperty.hasNext()) {
      ObjectProperty property = iterObjectProperty.next();
      System.out.println(property);
      countObjectProperties ++;
    }
    System.out.println("# of object properties in model = " + countObjectProperties);
    // TODO: is this what we want?
    // Count datatype properties
    int countDatatypeProperties = 0;
    Iterator<DatatypeProperty> iterDatatypeProperty = model.listDatatypeProperties();
    while(iterDatatypeProperty.hasNext()) {
      DatatypeProperty property = iterDatatypeProperty.next();
      System.out.println(property);
      countDatatypeProperties ++;
    }
    System.out.println("# of datatype properties in model = " + countDatatypeProperties);
    
    
//    Iterator<OntClass> ontClasses = model.listHierarchyRootClasses();
//    while (ontClasses.hasNext()) {
//      OntClass ontClass = ontClasses.next();
//      System.out.println("-----" + ontClass.getURI() + "-----");
//      
//    }
//
//    System.out.println("\n------ List Individuals ------");
//    Iterator<Individual> individuals = model.listIndividuals();
//    while (individuals.hasNext()) {
//      Individual individual = individuals.next();
//      System.out.println(individual.getURI());
//    }
//
//    System.out.println("\n------ List ObjectProperties ------");
//    Iterator<ObjectProperty> objectProperties = model
//        .listObjectProperties();
//    while (objectProperties.hasNext()) {
//      ObjectProperty objectProperty = objectProperties.next();
//      System.out.println(objectProperty.getURI());
//    }
//
//    System.out.println("\n------ List DatatypeProperties ------");
//    Iterator<DatatypeProperty> datatypeProperties = model
//        .listDatatypeProperties();
//    while (datatypeProperties.hasNext()) {
//      DatatypeProperty datatypeProperty = datatypeProperties.next();
//      System.out.println(datatypeProperty.getURI());
//    }
  }
}