package com.dagong.service;

import com.alibaba.fastjson.JSON;
import com.dagong.pojo.Company;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

/**
 * Created by liuchang on 16/4/5.
 */
@Service
public class SearchService {

    private TransportClient transportClient = null;
    private static final int PAGE_SIZE = 10;
    private final String INDEX = "company";
    private final String TYPE = "info";

    @Value("${searchEngine.address}")
    private String searchAddress = null;

    @Value("${searchEngine.port}")
    private int searchPort = 0;

    @PostConstruct
    public void init() {
        try {
            transportClient = TransportClient.builder().build()
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(searchAddress), searchPort));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }


    public Map getCompany(String id) {
        GetResponse response = transportClient.prepareGet(INDEX, TYPE, id).execute().actionGet();
        if (response.isExists()) {
            return response.getSource();
        }
        return null;
    }


    public boolean create(List<Company> list) {
        if (list == null || list.isEmpty()) {
            return false;
        }
        BulkRequestBuilder bulkRequestBuilder = transportClient.prepareBulk();
        list.forEach(company -> {

            bulkRequestBuilder.add(transportClient.prepareIndex(INDEX, TYPE, company.getId()).setSource(JSON.toJSONString(company)));
        });
        BulkResponse bulkItemResponses = bulkRequestBuilder.execute().actionGet();
        return true;
    }


    public boolean update(List<Company> list) {
        if (list == null || list.isEmpty()) {
            return false;
        }
        BulkRequestBuilder bulkRequestBuilder = transportClient.prepareBulk();
        list.forEach(company -> {
            bulkRequestBuilder.add(transportClient.prepareUpdate(INDEX, TYPE, company.getId()).setDoc(JSON.toJSONString(company)));
        });
        BulkResponse bulkItemResponses = bulkRequestBuilder.execute().actionGet();

        return true;
    }


}
