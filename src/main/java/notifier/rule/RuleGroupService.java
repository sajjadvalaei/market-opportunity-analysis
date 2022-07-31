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

/***
 * This class is the best idea I have for connecting Rule and RuleMemory. I chose it from 6 designs I had in mind.
 * by defining this class, I could disconnect the RuleEvaluator from Rule and RuleMemory, and it's more logical.
 * @ruleList contains all rules that use a particular type of RuleMemory.
 * @process adds candlestick to the RuleMemory and returns all notifications are between rules and candlestick.
 */
public class RuleGroupService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RuleGroupService.class);
    RuleMemory memory;
    List<Rule> ruleList;
    public RuleGroupService(List<Rule> ruleList, RuleMemory memory) throws MemoryNotAcceptableException {
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
