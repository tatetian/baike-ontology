package me.tatetian.ke;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
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
  
  public OntModel getModel() {
    return model;
  }
  
  public void process() throws IOException {
    BaikeUtil.loadCategoriesFile(model, categoriesFile);
    BaikeUtil.loadArticlesFile(model, articlesFile);
  }
  
  
  public static class Statistics {
    private String[] rootClassURIs;
    private int[] countSubClasses;
    private int[] countInstances;
    private int[] countObjectProperties;
    private int[] countDatatypeProperties;
    private int currentClassIndex;
    
    private Model model;
    private InfModel inf;
    
    public Statistics(Model model) {
      this.model = model;
      Reasoner reasoner = ReasonerRegistry.getTransitiveReasoner();
      this.inf   = ModelFactory.createInfModel(reasoner, model);
    }
    
    public void visitRoot(OntClass _rootClass) {
      // Get root classes
      List<OntClass> rootClasses = new ArrayList<OntClass>();
      ExtendedIterator<OntClass> iterRootClass = _rootClass.listSubClasses();
      while(iterRootClass.hasNext()) {
        OntClass rootClass = iterRootClass.next();
        rootClasses.add(rootClass);
      }
      // Get root names
      int numRootClasses = rootClasses.size();
      rootClassURIs = new String[numRootClasses];
      countSubClasses = new int[numRootClasses];
      countInstances = new int[numRootClasses];
      countObjectProperties = new int[numRootClasses];
      countDatatypeProperties = new int[numRootClasses];
      for(int i = 0; i < rootClassURIs.length; i++)
        rootClassURIs[i] = rootClasses.get(i).getURI();
      BaikeUtil.log("Root categories: " + Arrays.toString(rootClassURIs));
      // Statistics of each root
      for(currentClassIndex = 0; currentClassIndex < numRootClasses; currentClassIndex++) {
        System.out.println("===========================================================");
        BaikeUtil.log("Visiting root category: " + rootClassURIs[currentClassIndex] + "...");
        visit2(rootClasses.get(currentClassIndex));
        BaikeUtil.log("# of subcategories = " + countSubClasses[currentClassIndex]);
        BaikeUtil.log("# of instances = " + countInstances[currentClassIndex]);
        BaikeUtil.log("# of object properties = " + countObjectProperties[currentClassIndex]);
        BaikeUtil.log("# of datatype properties = " + countDatatypeProperties[currentClassIndex]);
      }
    }
    
    private void visit2(OntClass rootClass) {
      String categoryURI = "<" + rootClass.getURI() + ">";
      countSubClasses[currentClassIndex] = BaikeStatistics.countSubClasses(inf, categoryURI);
      countInstances[currentClassIndex] = BaikeStatistics.countArticles(inf, categoryURI);
      countDatatypeProperties[currentClassIndex] = BaikeStatistics.countDatatypeProperties(inf, categoryURI);
      countObjectProperties[currentClassIndex] = BaikeStatistics.countObjectProperties(inf, categoryURI);
    }
    
    private void visit(OntClass subClass) {
      // If non-leaf class
      if(subClass.hasSubClass()) {
        ExtendedIterator<OntClass> iterRootClass = subClass.listSubClasses();
        while(iterRootClass.hasNext()) {
          // Increase counter
          countSubClasses[currentClassIndex] ++;
          // Visit sub classes
          OntClass subSubClass = iterRootClass.next();
          visit(subSubClass);
        }
      }
      // If leaf class
      else {
        // Increase subclass counter
        countSubClasses[currentClassIndex] ++;
        // Increase instances counter
        countInstances[currentClassIndex] += BaikeStatistics.countArticles(model, "<" + subClass.getURI() + ">");
      }
    }
  }
  
  public void printFaster() {
    // Get root classes
    String rootClassURI = BaikeUtil.getCategoryURI("Root");
    OntClass _rootClass  = model.getOntClass(rootClassURI);
    assert(_rootClass != null);
    // Iterate classes
    Statistics st = new Statistics(model);
    st.visitRoot(_rootClass);
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
      BaikeUtil.log("-------Class " + rootClass.getURI() + ": <" + rootClassURI + ">-------");
      BaikeUtil.logMemory();
      // Count subclasses
      int countSubclasses = 0;
      ExtendedIterator<OntClass> iterSubclass = rootClass.listSubClasses(false);
      while(iterSubclass.hasNext()) {
        iterSubclass.next();
        countSubclasses ++;
      } 
      BaikeUtil.log("# of subclasses = " + countSubclasses);
      BaikeUtil.logMemory();
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
      BaikeUtil.logMemory();
      BaikeUtil.log("# of instances = " + countInstances);
      BaikeUtil.log("# of datatype properties = " + literalProperties);
      BaikeUtil.log("# of object properties = " + countInstances);
      //BaikeUtil.log(x)
//    while (objectProperties.hasNext()) {
//      ObjectProperty objectProperty = objectProperties.next();
//      BaikeUtil.log(objectProperty.getURI());
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
    
    System.out.println();
    
//    // TODO: is this what we want?
//    // Count object properties
//    int countObjectProperties = 0;
//    Iterator<ObjectProperty> iterObjectProperty = model.listObjectProperties();
//    while(iterObjectProperty.hasNext()) {
//      ObjectProperty property = iterObjectProperty.next();
//      BaikeUtil.log(property.toString());
//      countObjectProperties ++;
//    }
//    BaikeUtil.log("# of object properties in model = " + countObjectProperties);
//    // TODO: is this what we want?
//    // Count datatype properties
//    int countDatatypeProperties = 0;
//    Iterator<DatatypeProperty> iterDatatypeProperty = model.listDatatypeProperties();
//    while(iterDatatypeProperty.hasNext()) {
//      DatatypeProperty property = iterDatatypeProperty.next();
//      BaikeUtil.log(property.toString());
//      countDatatypeProperties ++;
//    }
//    BaikeUtil.log("# of datatype properties in model = " + countDatatypeProperties);
    
    
//    Iterator<OntClass> ontClasses = model.listHierarchyRootClasses();
//    while (ontClasses.hasNext()) {
//      OntClass ontClass = ontClasses.next();
//      BaikeUtil.log("-----" + ontClass.getURI() + "-----");
//      
//    }
//
//    BaikeUtil.log("\n------ List Individuals ------");
//    Iterator<Individual> individuals = model.listIndividuals();
//    while (individuals.hasNext()) {
//      Individual individual = individuals.next();
//      BaikeUtil.log(individual.getURI());
//    }
//
//    BaikeUtil.log("\n------ List ObjectProperties ------");
//    Iterator<ObjectProperty> objectProperties = model
//        .listObjectProperties();
//    while (objectProperties.hasNext()) {
//      ObjectProperty objectProperty = objectProperties.next();
//      BaikeUtil.log(objectProperty.getURI());
//    }
//
//    BaikeUtil.log("\n------ List DatatypeProperties ------");
//    Iterator<DatatypeProperty> datatypeProperties = model
//        .listDatatypeProperties();
//    while (datatypeProperties.hasNext()) {
//      DatatypeProperty datatypeProperty = datatypeProperties.next();
//      BaikeUtil.log(datatypeProperty.getURI());
//    }
  }
}
