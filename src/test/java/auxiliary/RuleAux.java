package auxiliary;

import common.period.Interval;
import common.period.OHLC;
import common.period.Period;
import notifier.rule.Rule;
import notifier.rule.SMARule;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RuleAux {
    private static final int MAXIMUM_HOUR_LIMIT = 24;
    static Random random = new Random();
    public static Rule createRandomRule(){
        return new SMARule(randomPeriod(),randomPeriod());
    }

    private static Period randomPeriod() {
        return new Period(random.nextInt(MAXIMUM_HOUR_LIMIT),
                Interval.values()[random.nextInt(Interval.values().length)],
                OHLC.values()[random.nextInt(OHLC.values().length)]);
    }

    public static List<Rule> createRandomList(int num) {
        List<Rule> list = new ArrayList<>();
        while(num > 0){
            list.add( createRandomRule() );
            num--;
        }
        return list;
    }

    public static void writeToFile(List<Rule> rules, File file) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(file);
        rules.forEach(rule-> writer.println(rule));
        writer.close();
    }
}
