import com.binance.api.client.exception.BinanceApiException;
import data.DataCollector;
import module.Candlestick;
import auxiliary.BinanceDataCollectorAux;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BinanceDataCollectorTest {
    private final static double ACCEPTABLE_ERROR = 1.0;
    private final static String BINANCE_KEY = "WBTCETH";
    private static final long DATA_UPDATE_INTERVAL = 60000;
    private static DataCollector dataCollector;
    @Before
    public void setDataCollector(){
        dataCollector = data.BinanceDataCollector.loadDataCollector();
    }

    @Test
    public void checkRequestWithApiDifference_shouldBeEqual() {
        Candlestick checkCandle = BinanceDataCollectorAux.getLastCandlestick(BINANCE_KEY);
        Candlestick candle = dataCollector.getLastCandlestick(BINANCE_KEY);
        checkTwoNearCandleDifference_ShouldBeSmall(candle,checkCandle,0.0001);
    }

    @Test (expected = BinanceApiException.class)
    public void wrongKeyException() {
        dataCollector.getLastCandlestick("wrong");
    }
    @Test (expected = BinanceApiException.class)
    public void nullKeyException() {
        dataCollector.getLastCandlestick(null);
    }
    @Test (expected = BinanceApiException.class)
    public void uppercaseWrongKeyException() {
        dataCollector.getLastCandlestick("WRONG");
    }

//    @Test // take 1 minute to execute
    public void checkTwoNearRequestDifference_shouldBeSmallMustNotBeEqual() throws InterruptedException {
        Candlestick oldCandle = dataCollector.getLastCandlestick(BINANCE_KEY);
        Thread.sleep(DATA_UPDATE_INTERVAL+1000);
        Candlestick candle = dataCollector.getLastCandlestick(BINANCE_KEY);
        checkTwoNearCandleDifference_ShouldBeSmall(candle,oldCandle, ACCEPTABLE_ERROR);
        Assert.assertNotEquals(candle.getOpenTime(),oldCandle.getOpenTime());
    }

    private static void checkTwoNearCandleDifference_ShouldBeSmall
            (Candlestick candle, Candlestick oldCandle,double error){Assert.assertEquals(oldCandle.getClose(), candle.getClose(), ACCEPTABLE_ERROR );
        Assert.assertEquals(oldCandle.getOpen(), candle.getOpen(), error );
        Assert.assertEquals(oldCandle.getLow(), candle.getLow(), error );
        Assert.assertEquals(oldCandle.getHigh(), candle.getHigh(), error );
        Assert.assertEquals(oldCandle.getClose(),candle.getClose(),error);
    }


}
