import auxiliary.CandlestickAux;
import module.Candlestick;
import module.Interval;
import module.OHLC;
import module.Period;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import rule.Rule;
import rule.RuleMemoryService;
import rule.SMARule;
import rule.SMARuleMemoryService;

public class SMARuleMemoryServiceTest {
    public static RuleMemoryService memory;
    @BeforeClass
    public static void setup(){
        Rule rule = Rule.Factory.create("SMA 10 D OPEN > 11 D CLOSE");
        memory = rule.getMemory();
    }
    @Test
    public void getLastTest_addOneCandlestick_mustReturnThat(){
        Candlestick candlestick = CandlestickAux.createRandomCandlestick();
        memory.append(candlestick);
        Assert.assertEquals(candlestick,
                memory.getLast(candlestick.getSymbol()));
    }
    @Test
    public void getLastTest_addTwoVariousCandlestick_mustReturnFirstOne(){
        Candlestick candlestick = CandlestickAux.createRandomCandlestick();
        Candlestick other = CandlestickAux.createRandomCandlestick();
        memory.append(candlestick);
        memory.append(other);
        Assert.assertEquals(candlestick,
                memory.getLast(candlestick.getSymbol()));
    }

    @Test
    public void getLastTest_addTwoSameCandlestick_mustReturnLastOne(){
        Candlestick candlestick = CandlestickAux.createRandomCandlestick();
        Candlestick other = new Candlestick(0.0,0.0,0.0,0.0,1L,2L, candlestick.getSymbol());
        memory.append(candlestick);
        memory.append(other);
        Assert.assertEquals(other,
                memory.getLast(candlestick.getSymbol()));
    }

    @Test
    public void getAverageTest_addTwoCandle_checkTheAverage() {
        Candlestick[] candles = new Candlestick[2];
        candles[0] = CandlestickAux.createRandomCandlestick();
        candles[1] = new Candlestick(0.0,0.0,0.0,0.0,1L,2L, candles[0].getSymbol());
        memory.append(candles[0]);
        memory.append(candles[1]);
        Double actual = memory.getAverage( new Period(2, Interval.M, OHLC.OPEN ), candles[0].getSymbol());
        System.out.println(actual);
        Assert.assertEquals((candles[0].getOpen() + candles[1].getOpen())/2.0,actual,0.01);
    }
}
