package me.tatetian.ke;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Map;

public class TestBaikeOntology extends junit.framework.TestCase {
  private final String categoriesFile = "data/baidu-taxonomy.dat";
  private final String articlesFile   = "data/baidu-article.dat.1000";
  
	public void test() throws IOException {
		BaikeOntology bo = new BaikeOntology(categoriesFile, articlesFile);
		BaikeUtil.logMemory();
    BaikeUtil.log("Processing...");
		bo.process();
		
		System.out.println();
		
		BaikeUtil.logMemory();
    BaikeUtil.log("Printing statistics...");
		bo.printFaster();
		
		System.out.println();
		
		BaikeUtil.logMemory();
    BaikeUtil.log("Do more statistics...");
    BaikeStatistics bs = new BaikeStatistics(bo.getModel());
    String[] rootCategories = bs.getRootCategories();
    BaikeUtil.log("Root categories = %s", Arrays.toString(rootCategories));
    Map<String, Integer> countSubCategories = bs.countSubCategories();
    BaikeUtil.log("# of categories in each root category = %s", countSubCategories.toString());
    int total = bs.countArticles();
    BaikeUtil.log("# of articles in model = %d", total);
    
//    FileOutputStream fout = new FileOutputStream("output.txt");
//    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fout));
//    bo.getModel().write(out, "RDF/XML");
	}
}
