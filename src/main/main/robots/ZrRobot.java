package main.robots;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import main.dao.AutoSiteDao;
import main.entities.AuthorData;
import main.entities.AutoSiteData;
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
import java.util.HashSet;
import java.util.Set;

public class ZrRobot {

    private static final Logger log = LoggerFactory.getLogger(ZrRobot.class);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public void updateZr() {
        log.debug("Zr update started");

        new Thread(() -> {
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                int pageCounter = 0;
                Set<AutoSiteData> siteData = new HashSet<>();
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
                        siteData.add(data);
                        parseArticle(client, link, data);
                    }
                    pageCounter++;
                }

                RestHighLevelClient restClient = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost",9200)));
                AutoSiteDao dao = new AutoSiteDao();
                for (AutoSiteData dataForSave : siteData) {
                    dao.saveData(restClient, dataForSave);
                }

                restClient.close();
                getStat(siteData);
                log.debug("Zr update finished");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }, "ZrUpdateThread").start();
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
            AuthorData author = new AuthorData();
            author.setFio(doc.getElementsByClass("info__author").first().text());
            data.setAuthor(author);
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

    private void getStat(Set<AutoSiteData> siteData) {
        int VwCount = 0;
        int SkodaCount = 0;
        int MitsuCount = 0;
        int RenaultCount = 0;
        int LadaCount = 0;
        int UazCount = 0;
        for (AutoSiteData data : siteData) {
            if (data.getTags().contains(AutoSiteData.Tag.VW))
                VwCount++;
            else if (data.getTags().contains(AutoSiteData.Tag.SKODA))
                SkodaCount++;
            else if (data.getTags().contains(AutoSiteData.Tag.RENAULT))
                RenaultCount++;
            else if (data.getTags().contains(AutoSiteData.Tag.MITSUBISHI))
                MitsuCount++;
            else if (data.getTags().contains(AutoSiteData.Tag.LADA))
                LadaCount++;
        }
        System.out.println("Final stat:\nTotal count: " + siteData.size() + "\nVW: " + VwCount + ", " + (float) ((VwCount * 100) / siteData.size()) + "%");
        System.out.println("Skoda: " + SkodaCount + ", " + ((SkodaCount * 100) / siteData.size()) + "%");
        System.out.println("Mitshubishi: " + MitsuCount + ", " + ((MitsuCount * 100) / siteData.size()) + "%");
        System.out.println("Renault: " + RenaultCount + ", " + ((RenaultCount * 100) / siteData.size()) + "%");
        System.out.println("Lada: " + LadaCount + ", " + ((LadaCount * 100) / siteData.size()) + "%");
        System.out.println("Best cars: " + UazCount + ", " + ((UazCount * 100) / siteData.size()) + "%");
    }
}
