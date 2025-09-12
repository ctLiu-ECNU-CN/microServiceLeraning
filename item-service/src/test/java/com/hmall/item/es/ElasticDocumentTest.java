package com.hmall.item.es;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.hmall.item.domain.po.Item;
import com.hmall.item.domain.po.ItemDoc;
import com.hmall.item.service.IItemService;
import org.apache.http.HttpHost;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class ElasticDocumentTest {

    private RestHighLevelClient client;

    @Autowired
    private IItemService itemService;

    @Test
    void testIndex() throws IOException {

//        查询商品信息
        Item testItem = itemService.getById(100000011127L);

//        商品信息转换为文档数据
        ItemDoc testItemDoc = BeanUtil.copyProperties(testItem, ItemDoc.class);
//        准备request对象
        IndexRequest request = new IndexRequest("items").id(testItemDoc.getId());
//        准备请求体
        request.source(JSONUtil.toJsonStr(testItemDoc),XContentType.JSON);

//        调用indices发送请求
        client.index(request, RequestOptions.DEFAULT);
    }

    @Test
    void testGetDocument() throws IOException {
        GetRequest request = new GetRequest("items").id("100000011127");//接受的参数是String类型
//        发送请求
        GetResponse docResponse = client.get(request, RequestOptions.DEFAULT);
//        解析相应结果
        String strItem = docResponse.getSourceAsString();
//        String格式文档 => Doc对象
        ItemDoc itemDoc = JSONUtil.toBean(strItem, ItemDoc.class);



        System.out.println("doc = " + itemDoc);
    }

    @Test
    void testUpdateDocument() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("items", "100000011127");

        updateRequest.doc(
                "price","27600"
        );
        client.update(updateRequest, RequestOptions.DEFAULT);
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


}
