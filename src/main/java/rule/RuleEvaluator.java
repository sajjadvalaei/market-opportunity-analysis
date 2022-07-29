package rule;


import database.MySQLDatabase;
import kafka.Consumer;
import module.Candlestick;
import module.Notification;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RuleEvaluator {
    private static final int MINIMUM_NUM_RECORD = 1;
    private final List<Rule> ruleList;
    private final Set<RuleMemoryService> ruleMemories = new HashSet<>();
    private final MySQLDatabase mySQLDatabase;
    private final Consumer consumer;
    public RuleEvaluator(String ruleFileAddress, Consumer consumer, String databaseName) throws FileNotFoundException {
        MySQLDatabase.start(databaseName);
        mySQLDatabase = MySQLDatabase.getDatabase(); 
        this.ruleList = Rule.Factory.load(ruleFileAddress);
        ruleList.forEach(rule-> ruleMemories.add(rule.getMemory()));
        this.consumer = consumer;
    }
    void run(){
        while (true) {
            List<Candlestick> candlesticks = consumer.getRecords(MINIMUM_NUM_RECORD);
            for (Candlestick candlestick : candlesticks) {
                if ( isDuplicated(candlestick) ) {
                    continue;
                }
                storeInMemories(candlestick);
                storeSatisfiedRulesInDatabase(candlestick);
            }
        }
    }

    private void storeSatisfiedRulesInDatabase(Candlestick candlestick) {
        ruleList.forEach(rule->{
            if( rule.satisfy(candlestick.getSymbol()) )
                storeToDatabase(rule, candlestick);
        });
    }

    private void storeInMemories(Candlestick candlestick) {
        ruleMemories.forEach(memory->memory.append(candlestick));
    }

    private void storeToDatabase(Rule rule, Candlestick candlestick) {
        mySQLDatabase.insert(createNotification(rule, candlestick));
    }

    private Notification createNotification(Rule rule, Candlestick candlestick) {
        return new Notification
                (rule.toString(), candlestick.getSymbol(),
                        candlestick.getClose(), candlestick.getOpenTime());
    }

    private boolean isDuplicated(Candlestick candlestick) {
        RuleMemoryService customMemory = ruleMemories.iterator().next();
        String symbol = candlestick.getSymbol();
        Candlestick last = customMemory.getLast(symbol);
        return last.getOpenTime() >= candlestick.getOpenTime();
    }
}
