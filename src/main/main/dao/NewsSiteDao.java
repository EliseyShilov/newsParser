package main.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import main.entities.NewsSiteData;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.Cardinality;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;

public class NewsSiteDao {

    private static final Logger log = LoggerFactory.getLogger(NewsSiteDao.class);
    private static final String INDEX_NAME = "auto_sites_articles";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public void saveData(RestHighLevelClient client, NewsSiteData data) {
        try {
            IndexRequest request = new IndexRequest(INDEX_NAME);
            if (data.getText() != null)
                request.id(sha256Hex(data.getText()));
            else {
                log.error("Can't find text in data!");
                return;
            }
            request.source(toJson(data), XContentType.JSON);

            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            if (!response.getResult().equals(DocWriteResponse.Result.CREATED))
                log.error("Can't send new data to elastic!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public List<NewsSiteData> getAllArticles(RestHighLevelClient client) throws IOException {
        return search(client, null, null);
    }

    public List<NewsSiteData> getArticlesById(RestHighLevelClient client, String id) throws IOException {
        return search(client, id, "_id");
    }

    public List<NewsSiteData> getArticleByTitle(RestHighLevelClient client, String title) throws IOException {
        return search(client, title, "title");
    }

    public List<NewsSiteData> getArticlesByAuthor(RestHighLevelClient client, String author) throws IOException {
        return search(client, author, "author");
    }

    public void deleteAllData(RestHighLevelClient client) throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest(INDEX_NAME);
        client.indices().delete(request, RequestOptions.DEFAULT);
    }

    public void getAggregationByCount(RestHighLevelClient client, String field) throws IOException {
        String name = "agg_" + field;
        SearchRequest sr = new SearchRequest(INDEX_NAME);
        SearchSourceBuilder ssb = new SearchSourceBuilder();
        ssb.query(QueryBuilders.matchAllQuery());
        ssb.aggregation(AggregationBuilders.cardinality(name).field(field));
        sr.source(ssb.size(1000));
        SearchResponse response = client.search(sr, RequestOptions.DEFAULT);
        Cardinality cardinality = response.getAggregations().get(name);
        System.out.println("Cardinality for " + field + ": " + cardinality.getValue());
    }

    private List<NewsSiteData> search(RestHighLevelClient client, String content, String fieldName) throws IOException {
        SearchRequest sr = new SearchRequest(INDEX_NAME);
        SearchSourceBuilder ssb = new SearchSourceBuilder();
        if (content == null || fieldName == null)
            ssb.query(QueryBuilders.matchAllQuery());
        else
            ssb.query(QueryBuilders.matchQuery(fieldName, content));
        sr.source(ssb.size(1000));
        SearchResponse response = client.search(sr, RequestOptions.DEFAULT);
        return responseToData(response);
    }

    private List<NewsSiteData> responseToData(SearchResponse response) {
        List<NewsSiteData> dataList = new ArrayList<>();
        /*for (SearchHit hit : response.getHits()) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            NewsSiteData data = new NewsSiteData();
            dataList.add(data);
            data.setTitle((String) sourceAsMap.get("title"));
            data.setLink((String) sourceAsMap.get("link"));
            data.setText((String) sourceAsMap.get("text"));
            data.setAuthor((String) sourceAsMap.get("author"));
            Map publicationDate = (Map) sourceAsMap.get("publicationDate");
            if (publicationDate != null) {
                int year = (int) publicationDate.get("year");
                int month = (int) publicationDate.get("monthValue");
                int day = (int) publicationDate.get("dayOfMonth");
                String publicationDateStr = (String.valueOf(day).length() == 1 ? "0" + day : day) + "." + (String.valueOf(month).length() == 1 ? "0" + month : month) + "." + year;
                data.setPublicationDate(LocalDate.parse(publicationDateStr, formatter));
            }
            /*if (sourceAsMap.get("source") != null) {
                switch ((String) sourceAsMap.get("source")) {
                    case "ZR":
                        data.setSource(NewsSiteData.Source.ZR);
                        break;
                    case "AR":
                        data.setSource(NewsSiteData.Source.AR);
                }
            }
        }*/
        return dataList;
    }

    private String toJson(NewsSiteData data) throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(data);
    }
}


