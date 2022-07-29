package rule;

import kafka.Consumer;

import java.io.FileNotFoundException;

import static config.Configuration.*;

public class Main {
    private static final String RULE_FILE_ADDRESS = "src/main/resources/rule/rules.rl";
    private static final String CONSUMER_GROUP_ID = "consumer";

    public static void main(String[] args) {
        try {
            Consumer consumer = new Consumer(BROKER_ADDRESS, TOPIC_NAME, CONSUMER_GROUP_ID);
            RuleEvaluator ruleEvaluator
                    = new RuleEvaluator(RULE_FILE_ADDRESS, consumer, DATABASE_NAME);
            ruleEvaluator.run();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
