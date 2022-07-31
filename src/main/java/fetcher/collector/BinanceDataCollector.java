package fetcher.collector;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.CandlestickInterval;
import common.config.Configuration;
import common.candlestick.Candlestick;
import fetcher.exception.SymbolNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;
import java.util.List;

import static common.config.Configuration.PROPS;

/***
 *  This singleton class just has a considerable method from its interface.
 *  Other methods are just trying to connect in the case of exceptions.
 *  TODO: cache candlesticks.
 */
public class BinanceDataCollector  implements  DataCollector {
    private static final int TRY_NUM = Integer.parseInt(PROPS.getProperty("api.maximumTryNumber"));
    static DataCollector dataCollector;
    private BinanceApiRestClient client;
    Logger LOGGER = LoggerFactory.getLogger(BinanceDataCollector.class);
    private BinanceDataCollector() {
        Configuration.setSystemProxy();
        dataCollector = this;
        client = getClientFromFactory();
    }


    public static DataCollector getDataCollector() {
        if( dataCollector == null)
            dataCollector = new BinanceDataCollector();
        return dataCollector;
    }

   @Override
    public Candlestick getLastCandlestick(String symbol) throws ConnectException, SymbolNotFoundException {
        try {
            return getLastCandlestickExceptionNotHandled(symbol);
        } catch (Exception e){
            if (e.getMessage().startsWith("Invalid") )
                throw new SymbolNotFoundException("Invalid symbol: " + symbol);
            LOGGER.error("Binance api is not working. " + e);
            return tryGetLastCandlestick(symbol, 1);
        }
    }



    private Candlestick tryGetLastCandlestick(String symbol, int i) throws ConnectException {
        LOGGER.info("Try requesting again... " + i);
        try {
            Thread.sleep(10);
            return getLastCandlestickExceptionNotHandled(symbol);
        } catch (Exception ignored){
           // nothing to do
        } finally {
            if (i > TRY_NUM) {
                LOGGER.error("Tried to get last Candlestick for the " + TRY_NUM + " time but coudn't.");
                return tryChangeConnectionAndGetLastCandlestick(symbol, 1);
            }
            return tryGetLastCandlestick(symbol, i+1);
        }
    }

    private Candlestick tryChangeConnectionAndGetLastCandlestick(String symbol, int i) throws ConnectException {
        LOGGER.info("Try connecting again... " + i);
        try{
            Thread.sleep(10);
            dataCollector = new BinanceDataCollector();
            return getLastCandlestickExceptionNotHandled(symbol);
        } catch (Exception e){
            //
        } finally {
            if( i > TRY_NUM ){
                throw new ConnectException("Couldn't connect to the server after many tries. ");
            }
            return tryChangeConnectionAndGetLastCandlestick(symbol, i+1);
        }
    }
    private Candlestick getLastCandlestickExceptionNotHandled(String symbol) {
        List list = client.getCandlestickBars(symbol, CandlestickInterval.ONE_MINUTE);
        return makeCandlestick(getLastOfCandlestickList(list), symbol);
    }

    private com.binance.api.client.domain.market.Candlestick getLastOfCandlestickList(List list){
        return (com.binance.api.client.domain.market.Candlestick) list.get(list.size() - 2);
    }

    private BinanceApiRestClient getClientFromFactory() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        return factory.newRestClient();
    }

    private static Candlestick makeCandlestick
            (com.binance.api.client.domain.market.Candlestick candlestick, String binanceKey){
        return new Candlestick(Double.parseDouble(candlestick.getOpen()),
                Double.parseDouble(candlestick.getClose()),
                Double.parseDouble(candlestick.getHigh()),
                Double.parseDouble(candlestick.getLow()),
                candlestick.getOpenTime(), candlestick.getCloseTime(),binanceKey );
    }
}
