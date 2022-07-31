package auxiliary;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.CandlestickInterval;
import common.config.Configuration;
import common.candlestick.Candlestick;

import java.util.List;

public class BinanceDataCollectorAux {
    public static Candlestick getLastCandlestick(String binanceKey) {
        Configuration.setSystemProxy();
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiRestClient client = factory.newRestClient();
        List<com.binance.api.client.domain.market.Candlestick> candlestickList =
                client.getCandlestickBars( binanceKey, CandlestickInterval.ONE_MINUTE);
        com.binance.api.client.domain.market.Candlestick lastCandle = candlestickList.get(candlestickList.size()-1);

        return makeCandlestick(lastCandle, binanceKey);
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
