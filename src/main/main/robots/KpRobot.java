package main.robots;

import lombok.extern.log4j.Log4j;
import main.dao.NewsSiteDao;
import main.entities.NewsSiteAuthor;
import main.entities.NewsSiteData;
import main.entities.model.kp.*;
import main.tools.TextTools;
import main.tools.Tools;
import main.tools.ToolsKp;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Log4j
public class KpRobot {

    public void updateKp() {
        log.debug("Kp update started");

        new Thread(() -> {
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                List<NewsSiteData> siteData = new ArrayList<>();
                int pageCounter = 1;
                int lastPage = 0;
                int lastYear = 0;
                int lastMonth = 0;
                int lastMonthForLoop = 12;
                HttpResponse pageResponse = Tools.getResponse(Tools.buildRequest(ToolsKp.getPageUrl(pageCounter, 2022, 1), false), client);
                Feed obj = (Feed) Tools.parseJson(pageResponse.getEntity().getContent(), Feed.class);
                for (MetaKp meta : obj.getMeta()) {
                    switch (meta.getName()) {
                        case ("pages"):
                            lastPage = meta.getValue();
                            break;
                        case ("current-year"):
                            lastYear = meta.getValue();
                            break;
                        case ("current-month"):
                            lastMonth = meta.getValue();
                            break;
                    }
                }

                for (int year = 2012; year <= lastYear; year++) {
                    if (year == lastYear)
                        lastMonthForLoop = lastMonth;
                    for (int month = 1; month <= lastMonthForLoop; month++) {
                        pageResponse = Tools.getResponse(Tools.buildRequest(ToolsKp.getPageUrl(pageCounter, year, month), false), client);
                        obj = (Feed) Tools.parseJson(pageResponse.getEntity().getContent(), Feed.class);
                        for (MetaKp meta : obj.getMeta()) {
                            if ("pages".equals(meta.getName()))
                                lastPage = meta.getValue();
                        }
                        while (pageCounter < lastPage) {
                            pageCounter++;
                            pageResponse = Tools.getResponse(Tools.buildRequest(ToolsKp.getPageUrl(pageCounter, year, month), false), client);
                            obj = (Feed) Tools.parseJson(pageResponse.getEntity().getContent(), Feed.class);
                            List<ChildKp> lck = obj.getChildren();
                            for (ChildKp ck : lck) {
                                NewsSiteData data = new NewsSiteData();
                                data.setSource(NewsSiteData.Source.KP);
                                parseArticle(client, ck, data);
                                siteData.add(data);
                            }
                        }
                        pageCounter = 1;
                    }
                }


                /*RestHighLevelClient restClient = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200)));
                NewsSiteDao dao = new NewsSiteDao();

                testMinHash(siteData);
                testSearch(restClient, dao);
                restClient.close();*/

                log.debug("Kp update finished");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }, "KpUpdateThread").start();
    }

    private void parseArticle(CloseableHttpClient client, ChildKp ck, NewsSiteData data) throws IOException {
        String url = "";
        if (ck.getId() != null)
            url = ToolsKp.getArticleUrl(ck.getId());
        else if (ck.getIdReserve() != null)
            url = ToolsKp.getArticleUrl(ck.getIdReserve());
        else {
            log.warn("Can't get article's id!");
            return;
        }
        data.setLink(url);
        HttpResponse pageResponse = Tools.getResponse(Tools.buildRequest(url, true), client);
        Document doc = Jsoup.parse(EntityUtils.toString(pageResponse.getEntity()));

        Node dataElement = doc.select("script[type=application/ld+json]").first().childNode(0);
        if (dataElement == null) {
            log.warn("Can't get article's data from page: " + url);
            return;
        }
        ArticleKp ak = (ArticleKp) Tools.parseJson(dataElement.toString(), ArticleKp.class);
        if (ak == null) {
            log.warn("Can't parse article's data from page: " + url);
            return;
        }

        List<NewsSiteAuthor> nsaList = new LinkedList<>();
        LocalDate publicationDate = null;
        String text = ak.getDescription() + ak.getArticleBody();
        String title = ak.getHeadline();
        String publicationDateStr = ak.getDate();

        if (publicationDateStr != null)
            publicationDate = ToolsKp.getPubDateLD(publicationDateStr);

        if (ak.getAuthor().size() > 0) {
            for (ArticleAuthorKp aak : ak.getAuthor()) {
                NewsSiteAuthor nsa = new NewsSiteAuthor();
                nsa.setAuthorLink(aak.getUrl());
                nsa.setAuthorName(aak.getName());
                nsaList.add(nsa);
            }
        }

        data.setPublicationDate(publicationDate);
        data.setText(text);
        data.setTitle(title);
        data.setAuthor(nsaList);
    }

    private void testSearch(RestHighLevelClient client, NewsSiteDao dao) throws IOException {
        dao.getAggregationByCount(client, "author");
        List<NewsSiteData> allArticles = dao.getAllArticles(client);
        List<NewsSiteData> dataById = dao.getArticlesById(client, "21f28a1e8de738c52bee7a8166165cf0b1a08860e3b60ab13ce76421ef292901");
        List<NewsSiteData> dataByTitle = dao.getArticleByTitle(client, "DFM");
        List<NewsSiteData> dataByAuthor = dao.getArticlesByAuthor(client, "Иннокентий Кишкурно");
        log.debug("End search test");
    }
}
