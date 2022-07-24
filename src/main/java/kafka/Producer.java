package kafka;

import module.Candlestick;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class Producer{
    private final static Logger LOGGER = LoggerFactory.getLogger(Producer.class);
    private static KafkaProducer<String, Candlestick> kafkaProducer;
    private static Producer producer;

    public Producer(String brokerAddress) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerAddress);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CandlestickSerializer.class.getName());
        kafkaProducer = new KafkaProducer<>(props);
    }

    public static Producer start(String brokerAddress) {
        if(isRunning())
            return producer.returnProducerWithLog();
        producer = new Producer(brokerAddress);
        return producer;
    }

    public static Producer getProducer() {
        if(!isRunning())
            throw new NullPointerException("Server has not yet been set, producer is not started");
        return producer;
    }

    public static boolean isRunning(){
        return kafkaProducer != null;
    }

    public void send(String topicName, Candlestick candlestick) {
        try {
            ProducerRecord<String, Candlestick> record =
                    new ProducerRecord<>(topicName,candlestick.getKey(),candlestick);
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
        LOGGER.trace("Producer is shutting down.");
        kafkaProducer.close();
        producer = null;
        kafkaProducer = null;
    }

    private Producer returnProducerWithLog() {
        LOGGER.warn("Server address is already set" + Arrays.toString(Thread.currentThread().getStackTrace()));
        return getProducer();
    }
}
