package com.itheima.sms.consumer;

import com.aliyuncs.exceptions.ClientException;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import util.SmsUtil;

import javax.sound.midi.Soundbank;
import java.util.Map;

@Component
@RabbitListener(queues = "sms")
public class SmsConsumer {

    @Value("${aliyun.sms.templateCode}")
    private String templateCode;

    @Value("${aliyun.sms.signName}")
    private String signName;

    @Autowired
    private SmsUtil smsUtil;

    @RabbitHandler
    public void sendSms(Map<String,String> messageMap){
        String mobile = messageMap.get("mobile");
        String validateCode = messageMap.get("validateCode");
        System.out.println(String.format("mobile=%s, validateCode=%s", mobile, validateCode));
        // 调用 发送短信
        //String mobile,
        // String template_code,
        // String sign_name,
        // String param
        String param = String.format("{\"code\": %s}",validateCode);
        try {
            smsUtil.sendSms(mobile, templateCode, signName, param);
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
}
