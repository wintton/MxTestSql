import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Logger;

public class KafKaTest {

    String serverName = "192.168.168.130:2181";
    String topic = "talking";

    @Test
    public void doTestJuc(){
        Logger logger = Logger.getLogger(this.getClass().getName());
        logger.info("Hello");
    }

    @Test
    public void doTestProduce(){
        Properties prop = new Properties();

        prop.put("bootstrap.servers",serverName);
        prop.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        prop.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        prop.put("acks","0");
        prop.put("retries",0);
        prop.put("batch.size",16384);
        prop.put("linger.ms",1);
        prop.put("buffer.memory",33554432);

        String message = "Hello World";

        try{

            KafkaProducer<String, String> producer = new KafkaProducer<>(prop);
            producer.send(new ProducerRecord<String,String>(topic,message,topic));
            producer.close();
            System.out.println("发送消息" + message);
        } catch (Exception e){

            e.printStackTrace();

        }

    }

    @Test
    public void doTestConsumer(){
        try{
            Properties prop = new Properties();

            prop.put("bootstrap.servers",serverName);
            prop.put("key.deserializer",
                    "org.apache.kafka.common.serialization.StringDeserializer");

            prop.put("value.deserializer",
                    "org.apache.kafka.common.serialization.StringDeserializer");
            prop.put("group.id","con-1");
            prop.put("auto.offset.reset","latest");
            //自动提交偏移量
            prop.put("auto.commit.intervals.ms","true");
            //自动提交时间
            prop.put("auto.commit.interval.ms","1000");
            KafkaConsumer<String, String> consumer = new KafkaConsumer<>(prop);
            ArrayList<String> topics = new ArrayList<>();
            //可以订阅多个消息
            topics.add(topic);
            consumer.subscribe(topics);
            System.out.println("开始接受消息");
            while(true){
                System.out.println("等待消息");
                ConsumerRecords<String,String> poll = consumer.poll(Duration.ofSeconds(20));
                for(ConsumerRecord<String,String> consumerRecord :poll){
                    System.out.println(consumerRecord);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }


    }
}
