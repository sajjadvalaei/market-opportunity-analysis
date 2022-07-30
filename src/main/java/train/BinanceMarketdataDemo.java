package train;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.general.Asset;
import com.binance.api.client.domain.market.*;
import com.binance.api.client.exception.BinanceApiException;
import com.google.common.math.Quantiles;
import config.Configuration;
import data.BinanceDataCollector;
import data.DataCollector;
import data.Sender;
import data.SymbolNotFoundException;
import kafka.Producer;

import java.util.List;
import java.util.logging.Logger;

import static config.Configuration.BROKER_ADDRESS;

public class BinanceMarketdataDemo {
    Logger logger = Logger.getLogger(BinanceDataCollector.class.getName());

    private final static String APIS_FILE_ADDRESS = "src/main/resources/binance/symbols.smbl";
    public static void main(String[] args) throws Exception, SymbolNotFoundException {
        Configuration.setSystemProxy();
        System.out.println(APIS_FILE_ADDRESS);
        final Producer producer = new Producer(BROKER_ADDRESS);
        //JavaAPI.testURL("https://www.binance.com/en");
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiRestClient client = factory.newRestClient();
        // Getting depth of a symbol
        //OrderBook orderBook = client.getOrderBook("NEOETH", 10);
        //System.out.println(orderBook.getAsks());

        // Getting latest price of a symbol
        //while(true) {
            //Thread.sleep(1000);
          System.out.println(client.getServerTime());
        //  TickerStatistics tickerStatistics = client.get24HrPriceStatistics("WBTCETH");
          List<Candlestick> candlestickList = client.getCandlestickBars("USDTBIDR",CandlestickInterval.ONE_MINUTE);

          System.out.println(candlestickList.get(candlestickList.size()-2));

        DataCollector dataCollector = BinanceDataCollector.getDataCollector();
        System.out.println(dataCollector.getLastCandlestick("USDTBIDR"));
        producer.send("test",new module.Candlestick());
            //System.out.println(tickerStatistics);
        //}
        // Getting all latest prices
/*
        List<TickerPrice> allPrices = client.getAllPrices();
        System.out.println(allPrices);
        // Getting agg trades
        List<AggTrade> aggTrades = client.getAggTrades("NEOETH");
        System.out.println(aggTrades);
        // Weekly candlestick bars for a symbol
        List<Candlestick> candlesticks = client.getCandlestickBars("NEOETH", CandlestickInterval.ONE_MINUTE);
        System.out.println(candlesticks.size());
        System.out.println(candlesticks);
*/

/*
        // Getting all book tickers
        List<BookTicker> allBookTickers = (List<BookTicker>) client.getBookTickers();
        System.out.println(allBookTickers);
/*
        // Exception handling
        try {
            client.getOrderBook("UNKNOWN", 10);
        } catch (BinanceApiException e) {
            System.out.println(e.getError().getCode()); // -1121
            System.out.println(e.getError().getMsg());  // Invalid symbol
        }

 */


    }
}
