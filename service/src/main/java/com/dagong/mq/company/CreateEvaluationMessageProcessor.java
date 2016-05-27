package com.dagong.mq.company;

import com.alibaba.dubbo.common.json.JSON;
import com.alibaba.dubbo.common.json.ParseException;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.dagong.mq.MessageProcessor;
import com.dagong.service.CompanyService;
import org.apache.ibatis.ognl.Evaluation;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liuchang on 16/4/16.
 */

@Service
public class CreateCompanyMessageProcessor extends MessageProcessor {


    @Resource
    private CompanyService companyService;

    public CreateCompanyMessageProcessor() {
        this.setTopic("company");
        this.setTag("create");
    }

    @Override
    protected void process(List<MessageExt> list) {

        List<Evaluation> evaluationList = new ArrayList<>();
        list.forEach(messageExt -> {
            System.out.println("messageExt = " + messageExt.getTags());
            try {
                String json = new String(messageExt.getBody(), "UTF-8");
                HashMap<String, String> map = JSON.parse(json, HashMap.class);
                if (map == null || map.isEmpty()) {
                    return;
                }

                String type=map.get("type");
                String evaluationId = map.get("id");
                Evaluation evaluation = null;

                if(evaluation!=null){
                    evaluationList.add(evaluation);
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        if(!evaluationList.isEmpty()){
            evaluationService.addEvaluationToIndex(evaluationList);
        }
    }
}
