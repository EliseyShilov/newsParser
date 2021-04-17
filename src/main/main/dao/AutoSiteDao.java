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
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;

public class AutoSiteDao {

    private static final Logger log = LoggerFactory.getLogger(AutoSiteDao.class);

    public void saveData(RestHighLevelClient client, AutoSiteData data) {
        try {
            IndexRequest request = new IndexRequest("auto_sites_articles");
            request.id(sha256Hex(data.getText()));
            request.source(toJson(data), XContentType.JSON);

            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            if (!response.getResult().equals(DocWriteResponse.Result.CREATED))
                log.error("Can't send new data to elastic!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void getData(RestHighLevelClient client) {
    }

    private String toJson(AutoSiteData data) throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(data);
    }
}
