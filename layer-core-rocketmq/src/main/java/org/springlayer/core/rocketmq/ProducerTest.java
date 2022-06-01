package org.springlayer.core.rocketmq;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

/**
 * @Author Hzhi
 * @Date 2022-05-05 15:47
 * @description
 **/
public class ProducerTest {

    public static void main(String[] args) throws Exception {
        // 创建消息生产者，并指定生产者组名
        DefaultMQProducer producer = new DefaultMQProducer("myproducer-group");
        // 设置NameServer地址
        producer.setNamesrvAddr("192.168.1.253:9876");
        // 启动生产者
        producer.start();
        // 创建消息对象
        Message message = new Message("myTopic", "myTag", ("侯志侯志").getBytes());
        // 发送消息，设置超时时间
        SendResult result = producer.send(message, 10000);
        producer.setRetryTimesWhenSendFailed(10);
        System.out.println(result);
        // 关闭生产者
        producer.shutdown();
    }
}