package com.mx.kafka;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

/**
 * Kafka消费者.
 *
 * @author xindaqi
 * @since 2022-08-02 9:59
 */
public class KafkaConsumerTest {
    public static void main(String[] args) {

        String topic = "hellotopic";

        Properties properties = new Properties();

        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092,127.0.0.1:9093");
        properties.put("key.deserializer",   "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("session.timeout.ms", "30000");
        properties.setProperty("group.id","1111");

        KafkaConsumer<String,String> consumer = new KafkaConsumer<String, String>(properties);

        consumer.subscribe(Arrays.asList(topic));

        System.out.println("Subscribed to topic " + topic);

        while (true){
            ConsumerRecords<String, String> poll = consumer.poll(100);
            for (ConsumerRecord<String, String> stringStringConsumerRecord : poll) {
                System.out.printf("offset = %d, key = %s, value = %s\n",
                        stringStringConsumerRecord.offset(), stringStringConsumerRecord.key(), stringStringConsumerRecord.value());
            }
        }
    }

}
