package org.springlayer.core.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * @Author Hzhi
 * @Date 2022-05-05 15:51
 * @description
 **/
public class ConsumerTest {

    public static void main(String[] args) throws Exception {
        // 创建消费者，并指定消费者组名
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("myconsumer-group");
        // 设置NameServer地址
        consumer.setNamesrvAddr("192.168.1.253:9876");
        // 指定消费者订阅的主题和标签
        consumer.subscribe("myTopic", "*");
//        consumer.setConsumeMessageBatchMaxSize(1000);
        // 设置回调函数
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            // 处理消息
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                System.out.println("接收到的消息:" + list);
                // 消费成功
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        // 启动消费者
        consumer.start();
    }
}
