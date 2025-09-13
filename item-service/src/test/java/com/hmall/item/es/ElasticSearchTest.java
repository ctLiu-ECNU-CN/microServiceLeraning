package com.hmall.item.es;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hmall.common.utils.CollUtils;
import com.hmall.item.domain.po.Item;
import com.hmall.item.domain.po.ItemDoc;
import com.hmall.item.service.IItemService;
import org.apache.http.HttpHost;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.transform.Source;
import java.io.IOException;
import java.util.List;

//@SpringBootTest
public class ElasticSearchTest {

    private RestHighLevelClient client;

    /**
     * 测试MatchALL
     */
    @Test
    void testMatchAll() throws IOException {
//        创建request对象

        SearchRequest request = new SearchRequest("items");
//        配置request参与
        request.source().query(
                QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchQuery("name","脱脂牛奶"))
                        .filter(QueryBuilders.termQuery("brand","德亚"))
                        .filter(QueryBuilders.rangeQuery("price").lt(30000))
        );

//        发送请求
        SearchResponse searchResponse = client.search(request, RequestOptions.DEFAULT);
//        System.out.println(searchResponse.toString());

        showResponse(searchResponse);
    }

    private static void showResponse(SearchResponse searchResponse) {
        //        解析结果
        SearchHits hits = searchResponse.getHits();
        TotalHits totalHits = hits.getTotalHits();//得到总条数
        System.out.println(totalHits);
        SearchHit[] hitsArray = hits.getHits();
        for(SearchHit hit : hitsArray){
            String json = hit.getSourceAsString();
            System.err.println(json);
        }
    }


    /**
     * 测试分页获取
     * @throws IOException
     */
    @Test
    void testPageQuery() throws IOException {

//        模拟前端传递的分页参数
        int pageNo = 2, pageSize = 5;
        //        创建request对象

        SearchRequest request = new SearchRequest("items");
//        配置request参数
        request.source().query(QueryBuilders.matchAllQuery());
//        分页查询
        request.source().from((pageNo-1) * pageSize).size(5);

//        排序
        request.source().sort("sold", SortOrder.DESC).sort("price", SortOrder.ASC);
//        发送请求
        SearchResponse searchResponse = client.search(request, RequestOptions.DEFAULT);
//        System.out.println(searchResponse.toString());

        showResponse(searchResponse);
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
