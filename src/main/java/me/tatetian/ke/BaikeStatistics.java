package me.tatetian.ke;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;

public class BaikeStatistics {
  private static final String ROOT_CLASS = "<" + BaikeOntology.NS_C + "Root>";
  
  private OntModel model = null; 
  private InfModel inf = null;
  
  public BaikeStatistics(OntModel model) {
    this.model = model;
    Reasoner reasoner = ReasonerRegistry.getRDFSSimpleReasoner(); 
    this.inf      = ModelFactory.createInfModel(reasoner, model);
  }
  
  public String[] getRootCategories() {
    String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " + 
        "SELECT ?subClass " +
        "WHERE { ?subClass  rdfs:subClassOf  " + ROOT_CLASS +" }" ;
    Query query = QueryFactory.create(queryString) ;
    QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
    ResultSet results = qexec.execSelect() ;
    ArrayList<String> res = new ArrayList<String>();  
    for ( ; results.hasNext() ; )
    {
      QuerySolution soln = results.nextSolution();
      Resource subClass = soln.getResource("subClass") ; // Get a result variable - must be a resource
      res.add(subClass.getURI());
    }
    return res.toArray(new String[res.size()]);
  }
  
  public Map<String, Integer> countSubCategories() {
    // This query is inspired by the following two examples.
    // Examples on how to use count: 
    //  http://answers.semanticweb.com/questions/14860/sparql-using-count-in-group-by
    // Examples on how to select immeidate subclass:
    //  http://answers.semanticweb.com/questions/14699/get-immediate-subclasses-of-a-class
    //
    // sub0 is direct categories of ROOT
    // sub is sub categories of sub0, direct or indirect
    String queryString = 
               "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
               "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " + 
               "SELECT ?sub0 (COUNT(?sub) AS ?count) " +
               "WHERE { ?sub   rdfs:subClassOf ?sub0 . " +
                       "?sub0  rdfs:subClassOf " + ROOT_CLASS + " . " + 
               "FILTER ( ?sub0 != " + ROOT_CLASS + " ) . " +
               "FILTER ( ?sub0 != ?sub ) . " +
               "FILTER NOT EXISTS {" +  
                 "?sub0 rdfs:subClassOf ?x . " +
                 "?x rdfs:subClassOf " + ROOT_CLASS + " . " +
                 "FILTER (?x != ?sub0 && ?x != " + ROOT_CLASS + " ) } " +
               "} " +
               "GROUP BY ?sub0";
    Query query = QueryFactory.create(queryString) ;
    QueryExecution qexec = QueryExecutionFactory.create(query, inf) ;
    ResultSet results = qexec.execSelect() ;
    Map<String, Integer> res = new HashMap<String, Integer>();
    for ( ; results.hasNext() ; )
    {
      QuerySolution soln = results.nextSolution();
      Resource subClass  = soln.getResource("sub0") ;
      Literal countLtr   = soln.getLiteral("count");
      String subClassURI = subClass.getURI();
      int count = countLtr.getInt();
      res.put(subClassURI, count);
    }
    return res;
  }
  
  public int countArticles() {
    String queryString = 
        "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " + 
        "SELECT (COUNT(?article) AS ?count) " +
        "WHERE { ?article rdf:type " + ROOT_CLASS + " }";
    Query query = QueryFactory.create(queryString) ;
    QueryExecution qexec = QueryExecutionFactory.create(query, inf) ;
    ResultSet results = qexec.execSelect() ;
    int count = -1;
    for ( ; results.hasNext() ; )
    {
      QuerySolution soln = results.nextSolution();
      Literal countLtr   = soln.getLiteral("count");
      count = countLtr.getInt();
    }
    return count;
  }
}
