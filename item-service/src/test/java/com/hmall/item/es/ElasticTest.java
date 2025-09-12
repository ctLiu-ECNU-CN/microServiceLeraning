package com.hmall.item.es;


import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ElasticTest {

    private RestHighLevelClient client;


    @Test
    void testClient() throws IOException {
//        准备request对象
        CreateIndexRequest request = new CreateIndexRequest("items");
//        准备请求体
        request.source(MAPPING_TEMPLATE, XContentType.JSON);

//        调用indices发送请求
        client.indices().create(request, RequestOptions.DEFAULT);
    }

    @Test
    void testGetClient() throws IOException {
//        准备request对象
        GetIndexRequest request = new GetIndexRequest("items");

//        调用indices发送请求
//        client.indices().get(request, RequestOptions.DEFAULT);
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    @Test
    void testConnection(){
        System.out.println(client);
    }

    @BeforeEach
    void SetUp(){
        client = new RestHighLevelClient(RestClient.builder(new HttpHost("192.168.5.67", 9202, "http")));
    }

    @AfterEach
    void tearDown() throws IOException {
        if(client != null){
            client.close();
        }
    }

    public static final String MAPPING_TEMPLATE = "{\n" +
            "  \"mappings\": {\n" +
            "    \"properties\": {\n" +
            "      \"id\": {\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"name\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"ik_smart\"\n" +
            "      },\n" +
            "      \"price\": {\n" +
            "        \"type\": \"long\"\n" +
            "      },\n" +
            "      \"image\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"category\": {\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"brand\": {\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"sold\": {\n" +
            "        \"type\": \"long\"\n" +
            "      },\n" +
            "      \"commentCount\": {\n" +
            "        \"type\": \"long\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"isAD\": {\n" +
            "        \"type\": \"boolean\"\n" +
            "      },\n" +
            "      \"updateTime\": {\n" +
            "        \"type\": \"date\",\n" +
            "        \"format\": \"yyyy-MM-dd HH:mm:ss||epoch_millis\"\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";
}
