package me.tatetian.ke;

import java.io.IOException;

public class TestBaikeOntology extends junit.framework.TestCase {
  private final String categoriesFile = "data/baidu-taxonomy.dat";
  private final String articlesFile   = "data/baidu-article.dat";
  
	public void test() throws IOException {
    // Get current size of heap in bytes
    long heapSize = Runtime.getRuntime().totalMemory(); 
    // Get maximum size of heap in bytes. The heap cannot grow beyond this size.// Any attempt will result in an OutOfMemoryException.
    long heapMaxSize = Runtime.getRuntime().maxMemory();
    // Get amount of free memory within the heap in bytes. This size will increase // after garbage collection and decrease as new objects are created.
    long heapFreeSize = Runtime.getRuntime().freeMemory(); 
    System.out.format("heapSize = %d, heapMaxSize = %d, heapFreeSize = %d", heapSize, heapMaxSize, heapFreeSize);

		BaikeOntology bo = new BaikeOntology(categoriesFile, articlesFile);
    System.out.println("Processing...");
		bo.process();
    System.out.println("Printing statistics...");
		bo.print();
	}
}
