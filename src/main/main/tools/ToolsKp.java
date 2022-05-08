package main.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Log4j
public class ToolsKp {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String PAGE_URL_MAIN = "https://s02.api.yc.kpcdn.net/content/api/1/pages/get.json?pages.age.month=";
    private static final String PAGE_URL_YEAR = "&pages.age.year=";
    private static final String PAGE_URL_NUM = "&pages.direction=page&pages.number=";
    private static final String PAGE_URL_END = "&pages.target.class=100&pages.target.id=0";
    private static final String ARTICLE_URL_JSON = "https://s02.api.yc.kpcdn.net/content/api/1/pages/get.json?pages.direction=current&pages.target.class=10&pages.target.id=";
    private static final String ARTICLE_URL = "https://www.kp.ru/online/news/";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssZZZZZ");

    public static String getPageUrl(int pageNum, int year, int month) {
        return PAGE_URL_MAIN + month + PAGE_URL_YEAR + year + PAGE_URL_NUM + pageNum + PAGE_URL_END;
    }

    public static String getArticleUrl(int id) {
        return ARTICLE_URL + id;
    }

    public static LocalDate getPubDateLD(String pubDateStr) {
        LocalDate publicationDate = null;
        if (pubDateStr.matches("\\d{2}\\s\\D{3,8}\\s\\d{4}"))
            publicationDate = LocalDate.parse(pubDateStr, formatter);
        return publicationDate;
    }


}
