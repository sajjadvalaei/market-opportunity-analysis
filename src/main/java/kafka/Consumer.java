package kafka;

import common.candlestick.Candlestick;
import common.candlestick.CandlestickDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class Consumer {
    private KafkaConsumer<String, Candlestick> kafkaConsumer;
    public Consumer(String address, String topicName, String groupId) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,address);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                CandlestickDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
        kafkaConsumer = new KafkaConsumer<>(props);
        kafkaConsumer.subscribe(Collections.singleton(topicName));
    }
    public List<Candlestick> getRecords(int minimumNumRecord){
        List<Candlestick> candles = new ArrayList<>();
        while (minimumNumRecord > 0){
            ConsumerRecords<String, Candlestick> records = kafkaConsumer.poll(Duration.ofMillis(1000));
            minimumNumRecord -= records.count();
            for (ConsumerRecord<String, Candlestick> record : records) {
                candles.add(record.value());
            }

        }
        return candles;
    }

    public void close() {
        kafkaConsumer.close();
    }
}
