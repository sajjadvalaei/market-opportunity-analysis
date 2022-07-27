package rule;

import module.Period;
import scala.concurrent.java8.FuturesConvertersImpl;

import java.util.Arrays;

public class SMARule implements Rule {
    private Period[] period = new Period[2];
    public SMARule(Period period0, Period period1) {
        period[0] = period0;
        period[1] = period1;
    }
    // Parse the exact output of toString in addition with > sign between.
    public static SMARule parseSMA(String input) {
        Period[] periods = new Period[2];
        String[] orders = input.split(" ");
        int i = getSwapIndex(orders[3]);
        periods[i] = Period.parsePeriod(orders[1]+" "+orders[2]);
        periods[1-i] = Period.parsePeriod(orders[4]+" "+orders[5]);
        return new SMARule(periods[0],periods[1]);
    }

    private static int getSwapIndex(String order) {
        if (order.equals("<"))
            return 0;
        return 1;
    }

    @Override
    public boolean satisfy() {
        return false;
    }
    @Override
    public String toString(){
        return "SMA " + period[0] + " < " + period[1];
    }
    @Override
    public boolean equals(Object other){
        if( !(other instanceof SMARule) )
            return false;
        SMARule o = (SMARule) other;
        return Arrays.equals(period,o.period);
    }
}
