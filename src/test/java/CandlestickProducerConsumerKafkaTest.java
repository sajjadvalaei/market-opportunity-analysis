import ir.sahab.kafkarule.KafkaRule;
import kafka.CandlestickDeserializer;
import kafka.Producer;
import module.Candlestick;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
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

    private static Producer mainProducer;


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

        Candlestick candlestick = createRandomCandlestick();
        mainProducer.send(TOPIC_NAME, candlestick);
        mainProducer.close();

        KafkaConsumer<String, Candlestick> consumer = createCandlestickConsumer();
        consumer.subscribe(Collections.singletonList(TOPIC_NAME));
        List<ConsumerRecord<String, Candlestick> > records = getAllRecords(consumer, 1);
        Assert.assertEquals(1,records.size());
        records.forEach( record->
                Assert.assertEquals(record.value(),candlestick) );
        consumer.close();

    }

    @Test
    public void severalCandleListSendTest_shouldBeOrdered(){
        checkTopicIsClear();

        List<Candlestick> rCandleList = randomCandlestickList(RECORD_NUMBER);
        mainProducer.send(TOPIC_NAME,rCandleList);
        mainProducer.close();

        KafkaConsumer<String, Candlestick> consumer = createCandlestickConsumer();
        consumer.subscribe(Collections.singletonList(TOPIC_NAME));
        List<ConsumerRecord<String, Candlestick> > records = getAllRecords(consumer, RECORD_NUMBER);
        Assert.assertEquals(RECORD_NUMBER,records.size());
        checkRecordListEquality(records,rCandleList);
        consumer.close();

    }

    private void checkRecordListEquality
            (List<ConsumerRecord<String, Candlestick> > records, List<Candlestick> rCandleList) {
        for(int index = 0; index < records.size();index++)
            Assert.assertEquals(records.get(index).value(), rCandleList.get(index));

    }

    private List<Candlestick> randomCandlestickList(int num) {
        ArrayList<Candlestick> list = new ArrayList<>();
        while(num > 0 ) {
            list.add(createRandomCandlestick());
            num--;
        }
        return list;
    }

    private List<ConsumerRecord<String, Candlestick> > getAllRecords(KafkaConsumer<String, Candlestick> consumer, int numRecord) {
        List<ConsumerRecord<String, Candlestick> > retRecords = new ArrayList<>();
        while(numRecord>0){
            ConsumerRecords<String, Candlestick> records = consumer.poll(Duration.ofMillis(1000));
            numRecord -= records.count();
            records.forEach(record -> retRecords.add(record));
        }
        return retRecords;
    }

    private KafkaConsumer<String, Candlestick> createCandlestickConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaRule.getBrokerAddress());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID_CONFIG);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CandlestickDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
        return new KafkaConsumer<>(props);
    }

    private Candlestick createRandomCandlestick() {
        Random random = new Random();
        return new Candlestick(random.nextDouble(), random.nextDouble(), 
                random.nextDouble(), random.nextDouble(),
                random.nextLong(), random.nextLong(), Double.toString(random.nextDouble()));
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
