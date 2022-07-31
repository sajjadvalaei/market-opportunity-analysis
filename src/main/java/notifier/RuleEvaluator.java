package notifier;


import common.database.Database;
import common.candlestick.Candlestick;
import common.notification.Notification;
import notifier.rule.RuleGroupService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.time.Duration;
import java.util.List;

/***
 *  receive candlesticks and creates notifications and store them to database.
 *  TODO: handle bunch of consumer exceptions.
 *  @DesignNote: This class is designed to have just one group of rules( need one memory type. For example, SMARules).
 *  This constraint is to keep the design simple stupid enough.
 *  In addition, for adding another group of rules, we can just start another thread of RuleEvaluator
 *  with another RuleGroupService and another consumer( with different groupId). However, in case of inefficiency,
 *  we can simply add a list of RuleGroupServices instead of one.
 */
public class RuleEvaluator extends Thread{
    private static final Logger LOGGER = LoggerFactory.getLogger(RuleEvaluator.class);
    private final RuleGroupService ruleGroupService;
    private final Database database;
    private final KafkaConsumer<String, Candlestick> consumer;
    public RuleEvaluator(RuleGroupService rgs, KafkaConsumer<String, Candlestick> con, Database db) throws FileNotFoundException {
        super();
        this.database = db;
        this.consumer = con;
        this.ruleGroupService = rgs;
    }
    @Override
    public void run(){
        while (true) {
            ConsumerRecords<String, Candlestick> records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String,Candlestick> record : records) {
                LOGGER.info("Proccessing on candlestick " + record.value());
                List<Notification> notifications = ruleGroupService.process(record.value());
                notifications.forEach(database::insert);
            }
            consumer.commitAsync();
        }
    }


}
