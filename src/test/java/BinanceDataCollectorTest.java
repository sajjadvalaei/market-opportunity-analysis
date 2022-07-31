import fetcher.collector.BinanceDataCollector;
import fetcher.collector.DataCollector;
import fetcher.exception.SymbolNotFoundException;
import common.candlestick.Candlestick;
import auxiliary.BinanceDataCollectorAux;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.ConnectException;

public class BinanceDataCollectorTest {
    private final static double ACCEPTABLE_ERROR = 1.0;
    private final static String BINANCE_KEY = "WBTCETH";
    private static final long DATA_UPDATE_INTERVAL = 60000;
    private static DataCollector dataCollector;
    @Before
    public void setDataCollector(){
        dataCollector = BinanceDataCollector.getDataCollector();
    }

    @Test
    public void checkRequestWithApiDifference_shouldBeEqual() throws SymbolNotFoundException, ConnectException {
        Candlestick checkCandle = BinanceDataCollectorAux.getLastCandlestick(BINANCE_KEY);
        Candlestick candle = dataCollector.getLastCandlestick(BINANCE_KEY);
        checkTwoNearCandleDifference_ShouldBeSmall(candle,checkCandle,0.01);
    }
    @Test (expected = Exception.class)
    public void nullKeyException() throws SymbolNotFoundException, ConnectException {
        dataCollector.getLastCandlestick(null);
    }
    @Test (expected = SymbolNotFoundException.class)
    public void uppercaseWrongKeyException() throws SymbolNotFoundException, ConnectException {
        dataCollector.getLastCandlestick("WRONG");
    }
    @Test // take 1 minute to execute
    public void checkTwoNearRequestDifference_shouldBeSmallMustNotBeEqual() throws InterruptedException, ConnectException, SymbolNotFoundException {
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
