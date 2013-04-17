package me.tatetian.ke;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.junit.Assert;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class TestArticlesHandler extends junit.framework.TestCase {
  public void testHandleLine() throws IOException {
    // Prepare the model
    OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
    BaikeUtil.loadCategoriesFile(model, BaikeUtil.CATEGORIES_FILE);
    // Init article handler
    ArticlesHandler ah = new ArticlesHandler(model);
    // Read lines
    String[] lines = {
      "   ",
      "I:8",
      "T:灌汤包",
      "R:素菜包",
      "A:灌汤包，就是包子里面有汤。先认识武汉的四季美汤包而后结识灌汤包子的，知有灌汤包子一说。席间摆谈，知为灌汤包子是皇家食品，估计灌汤包子还是在前，四季美汤包在后。皆因四季美汤包落脚大武汉，享誉武汉三镇，商业大埠，南北东西交通枢纽，占了一个好地盘。灌汤包子特点：鲜香肉嫩，皮簿筋软、外形玲珑剔透、汤汁醇正浓郁、入口油而不腻。",
      "L:武汉::;武汉三镇::;交通枢纽::;面粉::;肉皮冻::;黄继善::;味精::;汤流::;菊花::;开封市::;山楂::;洛阳::;北京::;中国烹饪协会::;朱元璋::;浙江::;常遇春::;大将军::;五花肉::;酱油::;葱花::;皮冻::;面团::;鸡精::;香油::;高压锅::;红樱桃::;胡椒粉::;水蒸气::;顾客::;精华::;食品::;汤本::;肉冻::;佐料::;辣椒::;八宝粥::;景德镇::;背心::;美食家::;鲤鱼::;武汉::;开封::;武汉三镇::;交通枢纽::;散文::;汤::;西安::;民族::;贾三灌汤包子::;东西::;集团化::;价值::;中国文化::;饮食文化::;天津狗不理包子::;开封灌汤包",
      "C:饮食::;生活::;伊斯兰教::;穆斯林::;清真饮食",
      "IB:中文名::=灌汤包::;英文名::=Soup dumplings::;主要食材::=面粉，肉皮冻::;特点::=其特色是皮薄如纸，吹弹即破，皮内富有皮冻制成的卤汁::;分类::=中式包点",
      "",
      "I:182",
      "T:背影(张信哲演唱歌曲)",
      "R:存储卡::;大连国防技校::;a::;b::;c::;d::;f::;g::;H::;i::;J::;k::;l::;m::;n::;o::;p::;Q::;R::;s::;t::;u::;x::;Y::;z::;大气资源::;口风琴::;正压力::;世界预防自杀日::;夜难安寝::;社会劳动生产率::;w::;e::;探侦物语::;炙烤::;爱::;心灵鸡汤::;阿根廷总统::;匆匆::;安徽省政务服务中心::;鲁智深::;蒋兴宇::;温州生活网::;入若耶溪::;ad-aware::;齐庄公出猎::;世界市场价格变动与近代中国产业结构模式研究::;环境监测方法标准汇编::;骑桶者::;爱国人士::;QQ空间::;妙妙城::;延庆四中::;母狼紫岚::;社会科学中的价值问题研究::;高雄::;次级动力渗透盔甲::;紧水滩库区::;软起动柜",
      "L:张信哲::;思念::;张美贤::;木马",
      "C:娱乐::;音乐::;歌曲::;曲目"
    };
    for(String line : lines) {
      ah.handleLine(line);
    }
    // Debug
    FileOutputStream fout = new FileOutputStream("output.txt");
    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fout));
    model.write(out, "RDF/XML");
    // Check articles
    String articleURI = BaikeUtil.getArticleURI("8");
    OntResource article = model.getOntResource(articleURI);
    Assert.assertNotNull(article);
    articleURI = BaikeUtil.getArticleURI("182");
    article = model.getOntResource(articleURI);
    Assert.assertNotNull(article);
    // Check count
    BaikeStatistics bs = new BaikeStatistics(model);
    Assert.assertEquals(2, bs.countArticles());
  }
  
  public void testSpecialChars() throws IOException {
    // Prepare the model
    OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
    //BaikeUtil.loadCategoriesFile(model, BaikeUtil.CATEGORIES_FILE);
    // Init article handler
    ArticlesHandler ah = new ArticlesHandler(model);
    // Read lines
    String[] lines = {
      "   ",
      "I:8",
      "T:<>:::",
      "R:素菜包",
      "A:灌汤包，就是包子里面有汤。先认识武汉的四季美汤包而后结识灌汤包子的，知有灌汤包子一说。席间摆谈，知为灌汤包子是皇家食品，估计灌汤包子还是在前，四季美汤包在后。皆因四季美汤包落脚大武汉，享誉武汉三镇，商业大埠，南北东西交通枢纽，占了一个好地盘。灌汤包子特点：鲜香肉嫩，皮簿筋软、外形玲珑剔透、汤汁醇正浓郁、入口油而不腻。",
      "L:武汉::;武汉三镇::;交通枢纽::;面粉::;肉皮冻::;黄继善::;味精::;汤流::;菊花::;开封市::;山楂::;洛阳::;北京::;中国烹饪协会::;朱元璋::;浙江::;常遇春::;大将军::;五花肉::;酱油::;葱花::;皮冻::;面团::;鸡精::;香油::;高压锅::;红樱桃::;胡椒粉::;水蒸气::;顾客::;精华::;食品::;汤本::;肉冻::;佐料::;辣椒::;八宝粥::;景德镇::;背心::;美食家::;鲤鱼::;武汉::;开封::;武汉三镇::;交通枢纽::;散文::;汤::;西安::;民族::;贾三灌汤包子::;东西::;集团化::;价值::;中国文化::;饮食文化::;天津狗不理包子::;开封灌汤包",
      "C:饮食::;生活::;伊斯兰教::;穆斯林::;清真饮食",
      "IB:中文名::=灌汤包::;英文名::=Soup dumplings::;主要食材::=面粉，肉皮冻::;特点::=其特色是皮薄如纸，吹弹即破，皮内富有皮冻制成的卤汁::;分类::=中式包点",
      "",
      "I:182",
      "T:背影(张信哲演唱歌曲)",
      "R:存储卡::;大连国防技校::;a::;b::;c::;d::;f::;g::;H::;i::;J::;k::;l::;m::;n::;o::;p::;Q::;R::;s::;t::;u::;x::;Y::;z::;大气资源::;口风琴::;正压力::;世界预防自杀日::;夜难安寝::;社会劳动生产率::;w::;e::;探侦物语::;炙烤::;爱::;心灵鸡汤::;阿根廷总统::;匆匆::;安徽省政务服务中心::;鲁智深::;蒋兴宇::;温州生活网::;入若耶溪::;ad-aware::;齐庄公出猎::;世界市场价格变动与近代中国产业结构模式研究::;环境监测方法标准汇编::;骑桶者::;爱国人士::;QQ空间::;妙妙城::;延庆四中::;母狼紫岚::;社会科学中的价值问题研究::;高雄::;次级动力渗透盔甲::;紧水滩库区::;软起动柜",
      "L:张信哲::;思念::;张美贤::;木马",
      "C:娱乐::;音乐::;歌曲::;曲目"
    };
    for(String line : lines) {
      ah.handleLine(line);
    }
    // Debug
    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));
    model.write(out, "RDF/XML");
  }
}
