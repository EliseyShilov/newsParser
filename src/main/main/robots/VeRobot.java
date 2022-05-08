package main.robots;

import lombok.extern.log4j.Log4j;
import main.entities.NewsSiteData;
import main.entities.model.ve.DataListVe;
import main.entities.model.ve.DataVe;
import main.tools.Tools;
import main.tools.ToolsKp;
import main.tools.ToolsVe;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Log4j
public class VeRobot {

    public void updateVe() {
        log.debug("Ve update started");

        new Thread(() -> {
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                List<NewsSiteData> siteData = new ArrayList<>();
                int pageCounter = 1;
                for (; ; ) {
                    HttpResponse pageResponse = Tools.getResponse(Tools.buildRequest(ToolsVe.getPageUrl(pageCounter), true), client);
                    DataListVe obj = (DataListVe) Tools.parseJson(pageResponse.getEntity().getContent(), DataListVe.class);
                    List<DataVe> dvList = obj.getData();
                    if (dvList.size() == 0)
                        break;

                    for (DataVe dv : dvList) {
                        NewsSiteData data = new NewsSiteData();
                        data.setSource(NewsSiteData.Source.VE);
                        parseArticle(client, dv, data);
                        siteData.add(data);
                    }
                    pageCounter++;
                }

                log.debug("Ve update finished");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }, "KpUpdateThread").start();
    }

    private static void parseArticle(CloseableHttpClient client, DataVe dv, NewsSiteData data) throws IOException {
        String url;
        if (dv.getId() != null)
            url = ToolsKp.getArticleUrl(dv.getId());
        else {
            log.warn("Can't get article's id!");
            return;
        }
        data.setLink(url);

        if (dv.getDatePub() != null && dv.getDatePub().getDay() != null) {
            String pubDateStr = dv.getDatePub().getDay();
            LocalDate ld = ToolsVe.getPubDateLD(pubDateStr);
            if (ld != null)
                data.setPublicationDate(ld);
            else {
                log.warn("Can't get article's publication date (unknown structure)!");
                return;
            }
        } else {
            log.warn("Can't get article's publication date!");
            return;
        }
        data.setTitle(dv.getTitle());
    }


}
