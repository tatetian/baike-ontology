package me.tatetian.ke;

import java.io.IOException;

public class TestBaikeOntology extends junit.framework.TestCase {
  private final String categoriesFile = "data/baidu-taxonomy.dat";
  private final String articlesFile   = "data/baidu-article.dat.1000";
  
	public void test() throws IOException {
		BaikeOntology bo = new BaikeOntology(categoriesFile, articlesFile);
		bo.process();
		bo.print();
	}
}
