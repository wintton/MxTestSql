package com.mx.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Future;

/**
 * Kafka生产者.
 *
 * @author xindaqi
 * @since 2022-08-02 9:59
 */
public class KafkaProducerTest {

        public static void main(String[] args) {
            Properties properties = new Properties();

            properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092,127.0.0.1:9093");
            properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
            properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
            properties.put(ProducerConfig.ACKS_CONFIG, "1");
            properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
            properties.put(ProducerConfig.RETRIES_CONFIG, 0);
            properties.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 300);
            properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);

            // KafkaProducer 是线程安全的，可以多个线程使用用一个 KafkaProducer
            KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);
            for (int i = 0; i < 100; i++) {
                ProducerRecord<String, String> record = new ProducerRecord<>("hellotopic", "value - (" + i + 1 + ")");
                kafkaProducer.send(record, new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata metadata, Exception exception) {
                        if (exception != null) {
                            System.err.println("发送数据到kafka中,发生了异常.");
                            exception.printStackTrace();
                            return;
                        }
                        System.out.println("topic: " + metadata.topic() + " offset: " + metadata.offset() + " partition: "
                                + metadata.partition());
                    }
                });
            }

            System.out.println("消息发送完成");
            kafkaProducer.close();
        }

}
