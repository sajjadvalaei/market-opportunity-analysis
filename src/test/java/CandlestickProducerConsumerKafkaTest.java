import ir.sahab.kafkarule.KafkaRule;
import kafka.Producer;
import auxiliary.CandlestickAux;
import auxiliary.ConsumerAux;
import module.Candlestick;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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
    private static final Object GROUP_ID_CONFIG = "test-group-id";
    private static final int RECORD_NUMBER = 100;

    @ClassRule
    public static KafkaRule kafkaRule = new KafkaRule();

    private Producer mainProducer;


    @BeforeClass
    public static void before() {
        kafkaRule.createTopic(TOPIC_NAME, NUM_PARTITION);
    }

    @Before
    public void setup(){
        mainProducer = Producer.start(kafkaRule.getBrokerAddress());
        mainProducer = Producer.getProducer();
    }

    @Test
    public void oneCandleSendCheck(){
        checkTopicIsClear();

        module.Candlestick candlestick = CandlestickAux.createRandomCandlestick();
        mainProducer.send(TOPIC_NAME, candlestick);
        mainProducer.close();


        KafkaConsumer<String, Candlestick> consumer = createCandlestickConsumerFromCurrentServer();
        consumer.subscribe(Collections.singletonList(TOPIC_NAME));
        List<ConsumerRecord<String, Candlestick> > records = ConsumerAux.getAllRecords(consumer, 1);
        Assert.assertEquals(1,records.size());
        records.forEach( record->
                Assert.assertEquals(record.value(),candlestick) );
        consumer.close();

    }

    @Test
    public void severalCandleListSendTest_shouldBeOrdered(){
        checkTopicIsClear();

        List<module.Candlestick> rCandleList = CandlestickAux.randomCandlestickList(RECORD_NUMBER);
        mainProducer.send(TOPIC_NAME,rCandleList);
        mainProducer.close();

        KafkaConsumer<String, module.Candlestick> consumer = createCandlestickConsumerFromCurrentServer();
        consumer.subscribe(Collections.singletonList(TOPIC_NAME));
        List<ConsumerRecord<String, module.Candlestick> > records = ConsumerAux.getAllRecords(consumer, RECORD_NUMBER);
        Assert.assertEquals(RECORD_NUMBER,records.size());
        ConsumerAux.checkRecordListEquality(records,rCandleList);
        consumer.close();

    }

    private KafkaConsumer<String, module.Candlestick> createCandlestickConsumerFromCurrentServer() {
        return  ConsumerAux.createCandlestickConsumer(kafkaRule.getBrokerAddress());
    }




    public void SimpleKafkaClientTest() {
        SimpleKafkaClient.run(kafkaRule.getBrokerAddress());
    }
    private void makeTopicDirty() {
        KafkaProducer<byte[], byte[]> kafkaProducer = kafkaRule.newProducer();
        kafkaProducer.send(new ProducerRecord<>(TOPIC_NAME, "key".getBytes(), "value".getBytes()));
        kafkaProducer.close();
    }

    private void checkTopicIsClear() {
        KafkaConsumer<byte[], byte[]> kafkaConsumer = kafkaRule.newConsumer();
        kafkaConsumer.subscribe(Collections.singleton(TOPIC_NAME));
        ConsumerRecords<byte[], byte[]> records = kafkaConsumer.poll(Duration.ofMillis(1000));
        Assert.assertTrue(records.isEmpty());
        kafkaConsumer.close();
    }

}
