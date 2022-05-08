package main.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.cglib.core.Local;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


@Log4j
public class ToolsVe {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String PAGE_URL = "https://www.vesti.ru/api/news?page=";
    private static final String ARTICLE_URL = "https://www.vesti.ru/article/";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy").withLocale(new Locale("ru"));

    public static String getPageUrl(int pageNum) {
        return PAGE_URL + pageNum;
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
