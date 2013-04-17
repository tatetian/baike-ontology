package me.tatetian.ke;

import java.util.Arrays;
import java.util.Map;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class TestCategoriesHandler extends junit.framework.TestCase {
  public void testHandleLine() {
    OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
    CategoriesHandler ch = new CategoriesHandler(model);
    
    String[] lines = {"Root\t艺术;技术;文化;生活;地理;社会;人物;经济;科学;历史;自然;体育",
                      "体育\t体操Mid;棋牌运动;田径运动;体育周边;冰雪运动Mid;" +
                          "室内运动Mid;体育组织;运动会Mid;对抗运动;健美健身;电子竞技;" +
                          "射击Mid;赛事;球类运动;水上项目;奥运会;武术;户外运动;极限运动",
    		               "极限运动\t小轮车;蹦极;冲浪;滑板;摩托艇;轮滑;攀岩;滑水;登山",
    		               "户外运动\t马术;公路自行车赛;马术比赛;自行车"};
    for(String line : lines)
      ch.handleLine(line);

    // debug info
//    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));
//    model.write(out, "RDF/XML");
//    ch.statistics();
    
    // Check root categories
    BaikeStatistics bs = new BaikeStatistics(model);
    String[] realCategories = bs.getRootCategories(); 
    String[] expectedCategories = {"艺术","技术","文化","生活","地理","社会",
                                   "人物","经济","科学","历史","自然","体育"};
    // Check # of root categories
    assertEquals(expectedCategories.length, realCategories.length);
    // Sort categories first so that we can compare one by one
    Arrays.sort(realCategories);
    Arrays.sort(expectedCategories);
    // Check categories
    for(int i = 0; i < expectedCategories.length; i++) 
      assertEquals(BaikeOntology.NS_C + expectedCategories[i], realCategories[i]);
    
    // Get subcategories' statistics
    Map<String, Integer> subCategoriesCount = bs.countSubCategories();
    // Check count of "体育" category
    int expectedTiyuCount = 19 + 13;
    int realTiyuCount = subCategoriesCount.get(BaikeOntology.NS_C + "体育");
    assertEquals(expectedTiyuCount, realTiyuCount);
  }
}
