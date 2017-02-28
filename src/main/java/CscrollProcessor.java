import java.util.List;
import java.util.Arrays;

import org.apache.commons.collections.CollectionUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.JsonPathSelector;

public class CscrollProcessor implements PageProcessor {

  private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(20000);

  private static String TARGET_URL = "https://cscroll.herokuapp.com";
  private static final String JSON = "https://cscroll.herokuapp.com/doc.json";
  private static final String LIST_URL = "https://cscroll\\.herokuapp\\.com/doc\\.json";

  @Override
    public void process(Page page) {
      page.putField("main_title", page.getHtml().xpath("//h2/text()"));
      //第二个demo入口,抓取Json
      (new JsonProcessor()).running(JSON);
    }

  @Override
    public Site getSite() {
      return site;
    }

  public static void main(String[] args) {
    Spider.create(new CscrollProcessor()).addUrl(TARGET_URL).run();
  }

  public class JsonProcessor implements PageProcessor {
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(20000);

    public void running(String url) {
      Spider.create(new JsonProcessor()).addUrl(url).run();
    }

    @Override
      public void process(Page page) {

        if (page.getUrl().regex(LIST_URL).match()) {
          List<String> paths = new JsonPathSelector("$[*].url").selectList(page.getRawText());
          if( CollectionUtils.isNotEmpty(paths)) {
            for (String path : paths) {
              page.addTargetRequest(TARGET_URL + path);    
            }
          } 
        }else {
//          page.putField("title", new JsonPathSelector("$.title").select(page.getRawText()));
          page.putField("json_title", page.getHtml().xpath("//h2/text()"));
        }
      }

    @Override
      public Site getSite() {
        return site; 
      }
  }
}
