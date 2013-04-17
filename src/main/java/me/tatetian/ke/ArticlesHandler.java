package me.tatetian.ke;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Bag;

public class ArticlesHandler extends Handler {
  private int countLines = 0;
  private String currentArticleURI = null;
  private OntResource currentArticle = null;
  private PropertyHelper propertyHelper = null;
  
  public ArticlesHandler(OntModel model) {
    super(model);
    propertyHelper = new PropertyHelper(this);
    newArticle();
  } 
  
  @Override
  public void handleLine(String line) {
    countLines ++;
    if(countLines % 10000 == 0) {
      float remaining = 100 - (float)100.0 * countLines / 20700000;
      BaikeUtil.logMemory();
      BaikeUtil.log("Processed " + countLines + " lines(" + remaining + "%% remaining)");
    }

    // Empty line indicates the beginning of a new article 
    line = line.trim();
    if(line.length() == 0) {
      newArticle();
      return;
    }
    
    // Parse
    int codeLen = line.indexOf(':');
    String rest = codeLen > 0 ? line.substring(codeLen + 1) : null;
    String[] fields = rest != null ? rest.split("::;") : null;
    // ID, T(title), R(related entities), A(abstract), L(links), C(categories)
    // and S(synonym)
    if(codeLen == 1) {
      char type = line.charAt(0);
      switch(type) {
      case 'I': // ID
        assert(currentArticle == null);
        String id = rest;
        currentArticleURI = BaikeUtil.getArticleURI(id); 
        currentArticle = model.createOntResource(currentArticleURI);
        break;
      case 'T': // Title
        // Add a label for description purpose
        String title = rest; // rest is title
        currentArticle.addLabel(title, "CN");
        break;
      case 'R': // Related entities
        propertyHelper.assignRelatedEntities(currentArticleURI, fields);
        break;
      case 'A': // Abstract
        currentArticle.addComment(rest, "CN");
        break;
      case 'L': // Links
        propertyHelper.assignLinks(currentArticleURI, fields);
        break;
      case 'C': // Categories
        for(String categoryName : fields) {
          propertyHelper.assignCategory(currentArticleURI, categoryName);
        }
        break;
      case 'S': // Synonym
        propertyHelper.assignSynonyms(currentArticleURI, fields);
        break;
      default:
        throw new RuntimeException("Unexpected code: " + type);
      }
    }
    // IB(infobox)
    else if(codeLen == 2 && line.substring(0, codeLen).equals("IB")){
      for(String field : fields) {
        String[] keyValue = field.split("::=");
        if(keyValue.length == 2) {
          String infoName = keyValue[0].trim();
          String infoVal  = keyValue[1].trim();
          propertyHelper.assignInfo(infoName, infoVal);
        }  
      }
    }
    // This should not happen!
    else {
      throw new RuntimeException("Unexpected line: " + line);
    }
  }
  
  private void newArticle() {
    currentArticleURI = null;
    currentArticle = null;
  }
  
  private static class PropertyHelper {
    private ArticlesHandler handler = null;
    private OntProperty hasInfoBox = null;
    private OntProperty hasInfoBoxName = null;
    private OntProperty hasInfoBoxValue = null;
    private OntProperty hasSynonyms = null;
    private OntProperty hasLinks = null;
    private OntProperty hasRelatedEntities = null;
    
    public PropertyHelper(ArticlesHandler handler) {
      this.handler = handler;
      initSchema();
    }

    private void initSchema() {
      hasInfoBox = handler.model.createObjectProperty(BaikeUtil.getPropertyURI("hasInfo"));
      hasInfoBoxName = handler.model.createDatatypeProperty(BaikeUtil.getPropertyURI("hasInfoBoxName"));
      hasInfoBoxValue = handler.model.createDatatypeProperty(BaikeUtil.getPropertyURI("hasInfoBoxValue"));
      
      hasSynonyms = handler.model.createObjectProperty(BaikeUtil.getPropertyURI("hasSynonyms"));
      hasLinks = handler.model.createObjectProperty(BaikeUtil.getPropertyURI("hasLinks"));
      hasRelatedEntities = handler.model.createObjectProperty(BaikeUtil.getPropertyURI("hasRelatedEntities"));
    }
    
    public void assignCategory(String articleURI, String categoryName) {
      // Get corresponding category
      OntClass category = handler.model.getOntClass(BaikeUtil.getCategoryURI(categoryName));
      // Add articles only to leaf categories
      if(category != null && category.getSubClass() == null) {
        category.createIndividual(articleURI);
      }
    }
    
    public void assignInfo(String infoName, String infoVal) {
      if(infoName.length() == 0 || infoVal.length() == 0)
        return;
      // hasInfoBox property has a blank node with two properties 
      // hasInfoBoxName and hasInfoBoxValue
      handler.currentArticle.addProperty(hasInfoBox, 
                                         handler.model.createResource()
                                                      .addProperty(hasInfoBoxName, infoName)
                                                      .addProperty(hasInfoBoxValue, infoVal));
    }
    
    public void assignSynonyms(String currentArticleURI, String[] synonyms) {
      if(synonyms.length > 0) {
        Bag bag = createBag(synonyms);
        handler.currentArticle.addProperty(hasSynonyms, bag);
      }
    }

    public void assignLinks(String currentArticleURI, String[] links) {
      if(links.length > 0) {
        Bag bag = createBag(links);
        handler.currentArticle.addProperty(hasLinks, bag);
      }
    }

    public void assignRelatedEntities(String currentArticleURI, String[] entities) {
      if(entities.length > 0) {
        Bag bag = createBag(entities);
        handler.currentArticle.addProperty(hasRelatedEntities, bag);
      }
    }
    
    private Bag createBag(String[] items) {
      Bag bag = handler.model.createBag();
      for(String s : items) {
        bag.add(s);
      }
      return bag;
    }
  }
}
