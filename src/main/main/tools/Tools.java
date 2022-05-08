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


@Log4j
public class Tools {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static CloseableHttpResponse getResponse(HttpUriRequest hur, CloseableHttpClient client) {
        CloseableHttpResponse response = null;
        try {
            if (hur != null) {
                response = client.execute(hur);
                if (response.getStatusLine().getStatusCode() != 200)
                    log.warn("Bad response: " + response.getStatusLine());
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return response;
    }

    public static HttpUriRequest buildRequest(String url, boolean isJson) {
        HttpUriRequest request = null;
        if (url != null) {
            if (isJson)
                request = RequestBuilder.get()
                        .setUri(url)
                        .setHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8")
                        .build();
            else
                request = RequestBuilder.get()
                        .setUri(url)
                        .build();
        }
        return request;
    }

    public static Object parseJson(String json, Class<?> classType) throws IOException {
        return parseJson(new ByteArrayInputStream(json.getBytes()), classType);
    }

    public static Object parseJson(InputStream json, Class<?> classType) throws IOException {
        Object object = null;
        if (json != null)
            object = mapper.readValue(json, classType);
        return object;
    }

}
