package com.qf.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

/**
 * @author Cjl
 * @date 2021/8/23 18:46
 */
public class MySimpleConsumer {
    private final static String TOPIC_NAME = "my-topics";
    private final static String CONSUMER_GROUP_NAME = "testGroup";

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.140.129:9092,192.168.140.129:9093,192.168.140.129:9094");
        // 消费分组名
        props.put(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP_NAME);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        //一次poll最大拉取消息的条数，可以根据消费速度的快慢来设置
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);
        //如果两次poll的时间如果超出了30s的时间间隔，kafka会认为其消费能力过弱，将其踢出消费组。将分区分配给其他消费者。-rebalance
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 30 * 1000);

        //1.创建一个消费者的客户端
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
        //2. 消费者订阅主题列表
        consumer.subscribe(Arrays.asList(TOPIC_NAME));

        while (true) {
            /*
             * 3.poll() API 是拉取消息的长轮询
             */
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, String> record : records) {
                //4.打印消息
                System.out.printf("收到消息：partition = %d,offset = %d, key = %s, value = %s%n", record.partition(),
                        record.offset(), record.key(), record.value());
            }
            //同步手动提交
            if (records.count() > 0) {//有消息
                // 手动同步提交offset，当前线程会阻塞直到offset提交成功
                // 一般使用同步提交，因为提交之后一般也没有什么逻辑代码了
                consumer.commitSync();//=======阻塞=== 提交成功
            }
            //异步手动提交
            //所有的消息已消费完
            //if (records.count() > 0) {
            //
            //    // 手动异步提交offset，当前线程提交offset不会阻塞，可以继续处理后面的程序逻辑
            //    consumer.commitAsync(new OffsetCommitCallback() {
            //        @Override
            //        public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) {
            //            if (exception != null) {
            //                System.err.println("Commit failed for " + offsets);
            //                System.err.println("Commit failed exception: " + exception.getStackTrace());
            //            }
            //        }
            //    });
        }

    }
}
