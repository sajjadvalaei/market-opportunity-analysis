package kafka;

import common.candlestick.Candlestick;
import common.candlestick.CandlestickSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

/***
 * deprecated.
 * TODO: remove it and its tests.
 */
public class Producer{
    private final static Logger LOGGER = Logger.getLogger(Producer.class.getName());
    private final KafkaProducer<String, Candlestick> kafkaProducer;
    public Producer(String brokerAddress) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerAddress);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CandlestickSerializer.class.getName());
        kafkaProducer = new KafkaProducer<>(props);
    }
    public void send(String topicName, Candlestick candlestick) {
        try {
            ProducerRecord<String, Candlestick> record =
                    new ProducerRecord<>(topicName,candlestick.getSymbol(),candlestick);
            RecordMetadata metadata = kafkaProducer.send(record).get();
        }
        catch (ExecutionException | InterruptedException e) {
            System.out.println("Error in sending record");
            e.printStackTrace();
        }

    }
    public void send(String topicName, List<Candlestick> rCandleList) {
        rCandleList.forEach(candle->send(topicName,candle));
    }
    public void close() {
        LOGGER.fine("Producer is shutting down.");
        kafkaProducer.close();
    }

}
