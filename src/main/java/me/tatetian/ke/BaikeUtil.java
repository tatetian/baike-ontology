package me.tatetian.ke;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.hp.hpl.jena.ontology.OntModel;

public class BaikeUtil {
  public static final String CATEGORIES_FILE = "data/baidu-taxonomy.dat";
  public static final String ARTICLES_FILE_SMALL = "data/baidu-article.dat.1000";
  public static final String ARTICLES_FILE = "data/baidu-article.dat";

  public static void loadCategoriesFile(OntModel model, String categoriesFile) throws IOException {
    System.out.println("Loading categories file...");
    // Init handler
    CategoriesHandler ch = new CategoriesHandler(model);
    // Process file
    processLines(categoriesFile, ch);
  }
  
  public static String getCategoryURI(String name) {
    return BaikeOntology.NS_C + name;
  }
  
  public static String getArticleURI(String title) {
    return BaikeOntology.NS_A + title;
  }
  
  public static String getPropertyURI(String property) {
    return BaikeOntology.NS_P + property;
  }
  
  public static String getInfoPropertyURI(String property) {
    return BaikeOntology.NS_P + "hasInfo" + property+"";
  }
  
  public static void loadArticlesFile(OntModel model, String articlesFile) throws IOException {
    System.out.println("Loading articles...");
    // Init handler
    ArticlesHandler ah = new ArticlesHandler(model);
    // Process file
    processLines(articlesFile, ah);
    
    // Debug
    FileOutputStream fout = new FileOutputStream("output.txt");
    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fout));
    model.write(out, "RDF/XML");
  }
  
  private static void processLines(String file, Handler handler) throws IOException {
    // Read file
    BufferedReader br = new BufferedReader(new FileReader(file));
    String line;
    try {
      // Process each line
      line = br.readLine();
      while (line != null) {
        handler.handleLine(line);
        line = br.readLine();
      }
    } 
    catch(Exception e) {
      System.out.println(e);
    }
    finally {
      br.close();
    }
  }
}
