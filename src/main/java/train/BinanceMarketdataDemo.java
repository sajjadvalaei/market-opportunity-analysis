package train;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.*;
import com.binance.api.client.exception.BinanceApiException;

import java.util.List;

public class BinanceMarketdataDemo {
    public static <BookTicker> void main(String[] args) throws Exception {
        JavaAPI.testURL("https://www.binance.com/en");
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiRestClient client = factory.newRestClient();
        // Getting depth of a symbol
        //OrderBook orderBook = client.getOrderBook("NEOETH", 10);
        //System.out.println(orderBook.getAsks());

        // Getting latest price of a symbol
        //while(true) {
            //Thread.sleep(1000);
            System.out.println(client.getServerTime());
            TickerStatistics tickerStatistics = client.get24HrPriceStatistics("WBTCETH");
            List<Candlestick> candlestickList = client.getCandlestickBars("WBTCETH",CandlestickInterval.ONE_MINUTE);

            System.out.println(candlestickList.get(candlestickList.size()-1));

            //System.out.println(tickerStatistics);
        //}
/*
        // Getting all latest prices
        List<TickerPrice> allPrices = client.getAllPrices();
        System.out.println(allPrices);

        // Getting agg trades
        List<AggTrade> aggTrades = client.getAggTrades("NEOETH");
        System.out.println(aggTrades);

        // Weekly candlestick bars for a symbol
        List<Candlestick> candlesticks = client.getCandlestickBars("NEOETH", CandlestickInterval.WEEKLY);
        System.out.println(candlesticks);

        // Getting all book tickers
        List<BookTicker> allBookTickers = (List<BookTicker>) client.getBookTickers();
        System.out.println(allBookTickers);

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
