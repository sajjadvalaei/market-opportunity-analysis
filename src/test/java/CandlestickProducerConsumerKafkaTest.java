import ir.sahab.kafkarule.KafkaRule;
import kafka.Consumer;
import kafka.Producer;
import auxiliary.CandlestickAux;
import auxiliary.ConsumerAux;
import module.Candlestick;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.*;
import train.SimpleKafkaClient;

import java.time.Duration;
import java.util.*;

public class CandlestickProducerConsumerKafkaTest {

    private static final String TOPIC_NAME = "topic-test";
    private static final int NUM_PARTITION = 1;
    private static final String GROUP_ID_CONFIG = "test-group-id";
    private static final int RECORD_NUMBER = 100;

    @ClassRule
    public static KafkaRule kafkaRule = new KafkaRule();

    private Producer mainProducer;
    private Consumer mainConsumer;


    @BeforeClass
    public static void before() {
        kafkaRule.createTopic(TOPIC_NAME, NUM_PARTITION);
    }

    @Before
    public void setup(){
        mainProducer = new Producer(kafkaRule.getBrokerAddress());
        mainConsumer = new Consumer(kafkaRule.getBrokerAddress(), TOPIC_NAME, GROUP_ID_CONFIG);
    }

    @Test
    public void producerTest_sendOneCandle(){
        checkTopicIsClear();

        Candlestick candlestick = CandlestickAux.createRandomCandlestick();
        mainProducer.send(TOPIC_NAME, candlestick);
        mainProducer.close();


        KafkaConsumer<String, Candlestick> consumer = createCandlestickConsumerFromCurrentTestServer();
        consumer.subscribe(Collections.singletonList(TOPIC_NAME));
        List<Candlestick> records = ConsumerAux.getAllRecords(consumer, 1);
        Assert.assertEquals(1,records.size());
        Assert.assertTrue(records.contains(candlestick));
        consumer.close();

    }

    @Test
    public void producerTest_sendSeveralCandleList_shouldBeOrdered(){
        checkTopicIsClear();

        List<Candlestick> rCandleList = CandlestickAux.randomCandlestickList(RECORD_NUMBER);
        mainProducer.send(TOPIC_NAME,rCandleList);
        mainProducer.close();

        KafkaConsumer<String, Candlestick> consumer = createCandlestickConsumerFromCurrentTestServer();
        consumer.subscribe(Collections.singletonList(TOPIC_NAME));
        List<Candlestick> records = ConsumerAux.getAllRecords(consumer, RECORD_NUMBER);
        Assert.assertEquals(RECORD_NUMBER,records.size());
        Assert.assertTrue(records.containsAll(rCandleList));
        consumer.close();

    }


    private KafkaConsumer<String, Candlestick> createCandlestickConsumerFromCurrentTestServer() {
        return  ConsumerAux.createCandlestickConsumer(kafkaRule.getBrokerAddress());
    }

    @Test
    public void consumerTest_sendOneCandle(){
        checkTopicIsClear();

        Candlestick candlestick = CandlestickAux.createRandomCandlestick();
        mainProducer.send(TOPIC_NAME, candlestick);
        mainProducer.close();


        List<Candlestick> records = mainConsumer.getRecords( 1);
        Assert.assertEquals(1,records.size());
        Assert.assertTrue( records.contains(candlestick) );
        mainConsumer.close();

    }

    @Test
    public void consumerTest_sendSeveralCandleList_shouldBeOrdered() {
        checkTopicIsClear();

        List<Candlestick> rCandleList = CandlestickAux.randomCandlestickList(RECORD_NUMBER);
        mainProducer.send(TOPIC_NAME, rCandleList);
        mainProducer.close();

        List<Candlestick> records = mainConsumer.getRecords( RECORD_NUMBER);
        Assert.assertEquals(RECORD_NUMBER,records.size());
        Assert.assertTrue(records.containsAll(rCandleList));
        mainConsumer.close();
    }


    private void makeTopicDirty() {
        KafkaProducer<byte[], byte[]> kafkaProducer = kafkaRule.newProducer();
        kafkaProducer.send(new ProducerRecord<>(TOPIC_NAME, "key".getBytes(), "value".getBytes()));
        kafkaProducer.close();
    }

    private void checkTopicIsClear() {
        KafkaConsumer<byte[], byte[]> kafkaConsumer = kafkaRule.newConsumer();
        kafkaConsumer.subscribe(Collections.singleton(TOPIC_NAME));
        for(int i = 0;i < 10; i++) {
            ConsumerRecords<byte[], byte[]> records = kafkaConsumer.poll(Duration.ofMillis(1000));
            Assert.assertTrue(records.isEmpty());
        }
        kafkaConsumer.close();
    }

}
