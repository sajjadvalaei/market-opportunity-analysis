package notifier.rule;

import common.candlestick.Candlestick;
import common.notification.Notification;
import notifier.exception.MemoryNotAcceptableException;
import notifier.exception.NotEnoughDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RuleGroupService {
    RuleMemoryService memory;
    private static final Logger LOGGER = LoggerFactory.getLogger(RuleGroupService.class);
    List<Rule> ruleList;
    public RuleGroupService(List<Rule> ruleList, RuleMemoryService memory) throws MemoryNotAcceptableException {
        this.ruleList = ruleList;
        this.memory = memory;
        for (Rule rule : this.ruleList) {
            rule.setMemory(memory);
        }
    }

    public List<Notification> process(Candlestick candlestick){
        if ( memory.isDuplicated(candlestick) ){
            return Collections.emptyList();
        }
        memory.append(candlestick);
        return extractNotifications(candlestick);
    }

    private List<Notification> extractNotifications(Candlestick candlestick) {
        List<Notification> notifications = new ArrayList<>();
        ruleList.forEach(rule->{
            try {
                if( rule.satisfy(candlestick.getSymbol()) )
                    notifications.add( createNotification(rule, candlestick));
            } catch (NotEnoughDataException e) {
                LOGGER.error("Data is not sufficient for evaluating this rule: " + rule +" with this candle: " + candlestick);
            }
        });
        return notifications;
    }

    private Notification createNotification(Rule rule, Candlestick candlestick) {
        return new Notification
                (rule.toString(), candlestick.getSymbol(),
                        candlestick.getClose(), candlestick.getOpenTime());
    }
}
