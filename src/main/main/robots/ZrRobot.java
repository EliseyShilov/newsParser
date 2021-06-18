package main.robots;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import main.dao.AutoSiteDao;
import main.entities.AutoSiteData;
import main.tools.TextTools;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ZrRobot {

    private static final Logger log = LoggerFactory.getLogger(ZrRobot.class);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public ZrRobot() {
    }

    public void updateZr() {
        log.debug("Zr update started");

        new Thread(() -> {
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                int pageCounter = 0;
                List<AutoSiteData> siteData = new ArrayList<>();
                while (pageCounter < 1) {
                    HttpResponse pageResponse = client.execute(new HttpGet("https://www.zr.ru/news/?p=" + pageCounter));
                    Document doc = Jsoup.parse(EntityUtils.toString(pageResponse.getEntity()));
                    Elements articleBlocks = doc.getElementsByTag("article");
                    if (articleBlocks.size() == 0) {
                        log.warn("Can't find articles or this is last page: " + pageCounter);
                        break;
                    }
                    for (Element articleBlock : articleBlocks) {
                        if (articleBlock.getElementsByTag("a").size() == 0)
                            throw new RuntimeException("Can't find element with link to article!");
                        String link = "https://www.zr.ru" + articleBlock.getElementsByTag("a").first().attr("href");
                        AutoSiteData data = new AutoSiteData();
                        data.setSource(AutoSiteData.Source.ZR);
                        parseArticle(client, link, data);
                        siteData.add(data);
                    }
                    pageCounter++;
                }

                RestHighLevelClient restClient = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200)));
                AutoSiteDao dao = new AutoSiteDao();

                testMinHash(siteData);
                testSearch(restClient, dao);
                restClient.close();

                log.debug("Zr update finished");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }, "ZrUpdateThread").start();
    }

    private void testSearch(RestHighLevelClient client, AutoSiteDao dao) throws IOException {
        dao.getAggregationByCount(client, "author");
        List<AutoSiteData> allArticles = dao.getAllArticles(client);
        List<AutoSiteData> dataById = dao.getArticlesById(client, "21f28a1e8de738c52bee7a8166165cf0b1a08860e3b60ab13ce76421ef292901");
        List<AutoSiteData> dataByTitle = dao.getArticleByTitle(client, "DFM");
        List<AutoSiteData> dataByAuthor = dao.getArticlesByAuthor(client, "Иннокентий Кишкурно");
        log.debug("End search test");
    }

    private void testMinHash(List<AutoSiteData> siteData) throws IOException {
        TextTools textTools = new TextTools();
        List<String> normalWords = textTools.textToNormalForm(siteData.get(2));
        List<List<String>> shingles = textTools.getShingles(normalWords, 10);
        Integer hash = textTools.getMinHash(shingles);

        List<String> normalWords1 = textTools.textToNormalForm(siteData.get(3));
        List<List<String>> shingles1 = textTools.getShingles(normalWords1, 10);
        Integer hash1 = textTools.getMinHash(shingles1);
        double jacarta = textTools.jacarta(hash, hash1);

        System.out.println("For articles: \"" + siteData.get(0).getTitle() + "\" and \"" + siteData.get(1).getTitle() + "\" Jacarta coefficient is " + jacarta);
    }

    private void parseArticle(CloseableHttpClient client, String link, AutoSiteData data) throws IOException {
        data.setLink(link);
        HttpResponse pageResponse = client.execute(new HttpGet(link));
        Document doc = Jsoup.parse(EntityUtils.toString(pageResponse.getEntity()));
        if (doc.getElementsByClass("head").size() > 0)
            data.setTitle(doc.getElementsByClass("head").first().text());
        else
            log.warn("Can't find header in article " + link);

        if (doc.getElementsByClass("info__author").size() > 0) {
            data.setAuthor(doc.getElementsByClass("info__author").first().text());
        } else
            log.warn("Can't find info about author in article " + link);

        if (doc.getElementsByClass("d-date").size() > 0) {
            String publicationDate = doc.getElementsByClass("d-date").last().text();
            if (publicationDate.matches("\\d\\d.\\d\\d.\\d\\d\\d\\d"))
                data.setPublicationDate(LocalDate.parse(publicationDate, formatter));
            else
                log.warn("Unknown date's format: " + publicationDate);
        } else
            log.warn("Can't find publication date in article " + link);

        if (doc.getElementsByClass("content").size() > 0)
            data.setText(doc.getElementsByClass("content").first().text());
        else
            log.warn("Can't find text in article " + link);
    }
}
