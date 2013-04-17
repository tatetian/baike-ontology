package me.tatetian.ke;

import java.io.IOException;
import java.util.Arrays;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class TestBaikeUtil extends junit.framework.TestCase {
  public void testLoadCategoriesFile() throws IOException {
    // Init model
    OntModel model= ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
    // Load categories
    BaikeUtil.loadCategoriesFile(model, BaikeUtil.CATEGORIES_FILE);
    // Check root categories
    BaikeStatistics bs = new BaikeStatistics(model);
    System.out.println(Arrays.toString(bs.getRootCategories()));
    int countRootCategories = bs.getRootCategories().length;
    assertEquals(12, countRootCategories);
  }
  
  public void testLoadArticlesFile() throws IOException {
    // Init model
    OntModel model= ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
    // Load categories
    BaikeUtil.loadCategoriesFile(model, BaikeUtil.CATEGORIES_FILE);
    // Load articles
    BaikeUtil.loadArticlesFile(model, BaikeUtil.ARTICLES_FILE_SMALL);
    // Check # of articles
    BaikeStatistics bs = new BaikeStatistics(model);
    int countArticles = bs.countArticles();
    assertEquals(95, countArticles);
  }
}
