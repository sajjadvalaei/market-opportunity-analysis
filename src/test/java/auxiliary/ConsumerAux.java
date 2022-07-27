package auxiliary;

import kafka.CandlestickDeserializer;
import module.Candlestick;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.Assert;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ConsumerAux {
    public static final Object GROUP_ID_CONFIG = "test-group-id";

    public static void checkRecordListEquality
            (List<ConsumerRecord<String, Candlestick>> records, List<Candlestick> rCandleList) {
        for(int index = 0; index < records.size();index++)
            Assert.assertEquals(records.get(index).value(), rCandleList.get(index));

    }



    public static List<ConsumerRecord<String, Candlestick> > getAllRecords(KafkaConsumer<String, Candlestick> consumer, int numRecord) {
        List<ConsumerRecord<String, Candlestick> > retRecords = new ArrayList<>();
        while(numRecord>0){
            ConsumerRecords<String, Candlestick> records = consumer.poll(Duration.ofMillis(1000));
            numRecord -= records.count();
            records.forEach(record -> retRecords.add(record));
        }
        return retRecords;
    }

    public static KafkaConsumer<String, Candlestick> createCandlestickConsumer(String address) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,address);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID_CONFIG);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CandlestickDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
        return new KafkaConsumer<>(props);
    }


}
