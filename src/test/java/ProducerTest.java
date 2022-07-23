import ir.sahab.kafkarule.KafkaRule;
import ir.sahab.zookeeperrule.ZooKeeperRule;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.*;
import train.SimpleKafkaClient;

import java.io.IOException;
import java.net.ServerSocket;
import java.time.Duration;
import java.util.Collections;

public class ProducerTest {
    private static final String ZK_ADDRESS = "127.0.1.1:" + anOpenPort();

    static int anOpenPort() {
        try {
            ServerSocket socket = new ServerSocket(0);
            Throwable var1 = null;

            int var2;
            try {
                var2 = socket.getLocalPort();
            } catch (Throwable var12) {
                var1 = var12;
                throw var12;
            } finally {
                if (socket != null) {
                    if (var1 != null) {
                        try {
                            socket.close();
                        } catch (Throwable var11) {
                            var1.addSuppressed(var11);
                        }
                    } else {
                        socket.close();
                    }
                }

            }

            return var2;
        } catch (IOException var14) {
            throw new AssertionError("Unable to find an open port.", var14);
        }
    }
    private static final String TOPIC_NAME = "demo";

    @ClassRule
    public static KafkaRule kafkaRule = new KafkaRule(ZK_ADDRESS);

    @ClassRule
    public static ZooKeeperRule zkRule = new ZooKeeperRule(ZK_ADDRESS);

    @ClassRule
    public static KafkaRule kafkaRuleWithSelfManagedZk = new KafkaRule();

    @BeforeClass
    public static void before() {
        kafkaRuleWithSelfManagedZk.createTopic(TOPIC_NAME, 1);
        kafkaRule.createTopic(TOPIC_NAME, 1);
    }
    @Before
    public void setup(){

    }
    @Test
    public void test(){
        System.out.println(ZK_ADDRESS);
        KafkaProducer<byte[], byte[]> kafkaProducer= kafkaRuleWithSelfManagedZk.newProducer();
        kafkaProducer.send(new ProducerRecord<>(TOPIC_NAME,"key".getBytes(),"value".getBytes()));
        kafkaProducer.close();

        KafkaConsumer<byte[], byte[]> kafkaConsumer = kafkaRuleWithSelfManagedZk.newConsumer();
        kafkaConsumer.subscribe(Collections.singleton(TOPIC_NAME));
        int count = 0;
        while(count < 1) {
            ConsumerRecords<byte[], byte[]> records = kafkaConsumer.poll(Duration.ofMillis(1000));
            System.out.println("salam");
            System.out.println(records.count());
            count += records.count();
            for (ConsumerRecord<byte[], byte[]> record : records) {
                Assert.assertArrayEquals("key".getBytes(), record.key());
                Assert.assertArrayEquals("value".getBytes(), record.value());
                System.out.println(record.key() + " " + record.value());
            }
        }
        kafkaConsumer.close();

    }

    @Test
    public void testDefaultProducerAndConsumer() {
        checkTopicIsClear();

        final String prefixKey = "test-key-";
        final String prefixValue = "test-value-";
        final int numRecords = 1000;

        KafkaProducer<byte[], byte[]> kafkaProducer = kafkaRule.newProducer();
        for (int i = 0; i < numRecords; i++) {
            ProducerRecord<byte[], byte[]> record =
                    new ProducerRecord<>(TOPIC_NAME, (prefixKey + i).getBytes(),
                            (prefixValue + i).getBytes());
            kafkaProducer.send(record);
        }
        kafkaProducer.close();

        KafkaConsumer<byte[], byte[]> kafkaConsumer = kafkaRule.newConsumer();
        kafkaConsumer.subscribe(Collections.singletonList(TOPIC_NAME));

        int count = 0;
        while (count < numRecords) {
            ConsumerRecords<byte[], byte[]> records = kafkaConsumer.poll(Duration.ofMillis(1000));
            System.out.println("salam");
            System.out.println(records.count());
            for (ConsumerRecord<byte[], byte[]> record : records) {
                Assert.assertArrayEquals((prefixKey + count).getBytes(), record.key());
                Assert.assertArrayEquals((prefixValue + count).getBytes(), record.value());
                count++;
            }
        }
        kafkaConsumer.close();
        makeTopicDirty();


    }
    @Test
    public void SimpleKafkaClientTest() {
        System.out.println(":" + kafkaRule.getBrokerAddress().split(":")[1] + ":");
        System.out.println(kafkaRuleWithSelfManagedZk.getBrokerAddress().split(":")[1]);
        SimpleKafkaClient.run(kafkaRuleWithSelfManagedZk.getBrokerAddress());
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
