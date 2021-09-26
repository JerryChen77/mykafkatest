package com.qf.kafka;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * @author Cjl
 * @date 2021/8/23 18:42
 */
public class MySimpleProducer {
    private final static String TOPIC_NAME = "my-topics";

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        //1.设置参数
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.140.129:9092,192.168.140.129:9093,192.168.140.129:9094");

        //把发送的key从字符串序列化为字节数组
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        //把发送消息value从字符串序列化为字节数组
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "1");

        /*
        发送失败会重试，默认重试间隔100ms，重试能保证消息发送的可靠性，但是也可能造成消息重复发送，比如网络抖动，所以需要在
        接收者那边做好消息接收的幂等性处理
        */
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        //重试间隔设置
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 300);

        //2.创建生产消息的客户端，传入参数
        Producer<String,String> producer = new KafkaProducer<String, String>(props);

        //3.创建消息
        //key：作用是决定了往哪个分区上发，value：具体要发送的消息内容
        ProducerRecord<String,String> producerRecord = new ProducerRecord<>(TOPIC_NAME,"mykeyvalue","小王八蛋");

        //4.发送消息,得到消息发送的元数据并输出
        RecordMetadata metadata = producer.send(producerRecord).get();
        System.out.println("同步方式发送消息结果：" + "topic-" + metadata.topic() + "|partition-"
                + metadata.partition() + "|offset-" + metadata.offset());

        //5.异步发送消息
        //producer.send(producerRecord, new Callback() {
        //    @Override
        //    public void onCompletion(RecordMetadata metadata, Exception exception) {
        //        if (exception != null) {
        //            System.err.println("发送消息失败：" + exception.getStackTrace());
        //
        //        }
        //        if (metadata != null) {
        //            System.out.println("异步方式发送消息结果：" + "topic-" + metadata.topic() + "|partition-"
        //                    + metadata.partition() + "|offset-" + metadata.offset());
        //        }
        //    }
        //});
    }
}

