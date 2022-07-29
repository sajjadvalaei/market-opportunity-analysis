package data;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.api.client.exception.BinanceApiException;
import config.Configuration;
import module.Candlestick;

import java.net.SocketTimeoutException;
import java.util.List;

public class BinanceDataCollector  implements  DataCollector {
    static DataCollector dataCollector;
    private BinanceApiRestClient client;
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
    public Candlestick getLastCandlestick(String symbol)  throws BinanceApiException  {
        try {
            System.out.println("saam");
            List list = client.getCandlestickBars(symbol, CandlestickInterval.ONE_MINUTE);
            return makeCandlestick(getLastOfCandlestickList(list), symbol);
        } catch (Exception e){
            return getLastCandlestick(symbol);
        }
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
