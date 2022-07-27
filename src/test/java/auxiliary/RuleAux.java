package auxiliary;

import module.Interval;
import module.Period;
import rule.Rule;
import rule.SMARule;

import java.util.Random;

public class RuleAux {
    private static final int MAXIMUM_HOUR_LIMIT = 24;
    static Random random = new Random();
    public static Rule createRandomRule(){
        return new SMARule(randomPeriod(),randomPeriod());
    }

    private static Period randomPeriod() {
        return new Period(random.nextInt(MAXIMUM_HOUR_LIMIT),
                Interval.values()[random.nextInt(Interval.values().length)]);
    }
}
