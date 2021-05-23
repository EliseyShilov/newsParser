package main.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import main.entities.AutoSiteData;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;

public class AutoSiteDao {

    private static final Logger log = LoggerFactory.getLogger(AutoSiteDao.class);
    private static final String INDEX_NAME = "auto_sites_articles";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public void saveData(RestHighLevelClient client, AutoSiteData data) {
        try {
            IndexRequest request = new IndexRequest(INDEX_NAME);
            request.id(sha256Hex(data.getText()));
            request.source(toJson(data), XContentType.JSON);

            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            if (!response.getResult().equals(DocWriteResponse.Result.CREATED))
                log.error("Can't send new data to elastic!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public List<AutoSiteData> getArticlesById(RestHighLevelClient client, String id) throws IOException {
        return search(client, id, "_id");
    }

    public List<AutoSiteData> getArticleByTitle(RestHighLevelClient client, String title) throws IOException {
        return search(client, title, "title");
    }

    public List<AutoSiteData> getArticlesByAuthor(RestHighLevelClient client, String author) throws IOException {
        return search(client, author, "author");
    }

    private List<AutoSiteData> search(RestHighLevelClient client, String content, String fieldName) throws IOException {
        SearchRequest sr = new SearchRequest(INDEX_NAME);
        SearchSourceBuilder ssb = new SearchSourceBuilder();
        ssb.query(QueryBuilders.matchQuery(content, fieldName));
        sr.source(ssb);
        SearchResponse response = client.search(sr, RequestOptions.DEFAULT);
        return responseToData(response);
    }

    private List<AutoSiteData> responseToData(SearchResponse response) {
        List<AutoSiteData> dataList = new ArrayList<>();
        for (SearchHit hit : response.getHits()) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            AutoSiteData data = new AutoSiteData();
            dataList.add(data);
            data.setTitle((String) sourceAsMap.get("title"));
            data.setLink((String) sourceAsMap.get("link"));
            data.setText((String) sourceAsMap.get("text"));
            data.setPublicationDate(((String) sourceAsMap.get("publicationDate")).matches("\\d\\d.\\d\\d.\\d\\d\\d\\d") ? LocalDate.parse((String) sourceAsMap.get("publicationDate"), formatter) : null);
            //data.setAuthor((String) sourceAsMap.get("author"));
            switch ((String) sourceAsMap.get("source")) {
                case "ZR":
                    data.setSource(AutoSiteData.Source.ZR);
                    break;
                case "AR":
                    data.setSource(AutoSiteData.Source.AR);
            }
        }
        return dataList;
    }

    private String toJson(AutoSiteData data) throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(data);
    }
}
