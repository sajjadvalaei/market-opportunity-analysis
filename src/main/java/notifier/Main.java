package notifier;

import common.database.Database;
import common.database.MySQLDatabase;
import common.candlestick.CandlestickDeserializer;
import common.candlestick.Candlestick;
import notifier.exception.MemoryNotAcceptableException;
import notifier.rule.Rule;
import notifier.rule.RuleGroupService;
import notifier.rule.SMARuleMemoryService;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static common.config.Configuration.*;

public class Main {
    private static final String RULE_FILE_ADDRESS = PROPS.getProperty("rule.fileAddress");
    private static final String CONSUMER_GROUP_ID = "consumer";
    private static final Logger LOGGER = LoggerFactory.getLogger(RuleEvaluator.class);

    public static void main(String[] args) {
        try {
            final KafkaConsumer<String, Candlestick> consumer = createConsumer(BROKER_ADDRESS, TOPIC_NAME, CONSUMER_GROUP_ID);
            final Database database = new MySQLDatabase(DATABASE_URL, USER, PASSWORD, DATABASE_NAME);
            final List<Rule> smaRules = Rule.Factory.load(RULE_FILE_ADDRESS);
            final RuleGroupService ruleGroupService = new RuleGroupService(smaRules, new SMARuleMemoryService());
            RuleEvaluator ruleEvaluator
                    = new RuleEvaluator(ruleGroupService, consumer, database);
            ruleEvaluator.start();
        } catch (FileNotFoundException e) {
            LOGGER.error("rule file not found" + e);
        } catch (MemoryNotAcceptableException e) {
            LOGGER.error("you assigned wrong rules to rule group service. " + e);
        }
    }

    private static KafkaConsumer<String,Candlestick> createConsumer(String address, String topicName, String groupId) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,address);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CandlestickDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
        KafkaConsumer<String, Candlestick> kafkaConsumer = new KafkaConsumer<>(props);
        kafkaConsumer.subscribe(Collections.singleton(topicName));
        return kafkaConsumer;
    }

}
