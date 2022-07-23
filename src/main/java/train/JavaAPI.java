package train;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.bitmex.BitmexExchange;
import org.knowm.xchange.bitmex.service.BitmexMarketDataServiceRaw;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.service.marketdata.MarketDataService;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class JavaAPI {
    public static void main(String[] args) throws Exception {
        testURL("https://www.binance.com/en");
        // Use the factory to get Bitmex exchange API using default settings
        //ExchangeSpecification exSpec = new BitmexExchange().getDefaultExchangeSpecification();
        //exSpec.setUserName("Untitled Key");
        //exSpec.setApiKey("8pINhRCOi4PIaWswF1PIQXut");
        //exSpec.setSecretKey("L5bmf6wx8FflzppA-MVXdNKHqde8q1fLzDvQY-_hCJM2_nzD");
        Exchange bitstamp = ExchangeFactory.INSTANCE.createExchange(BitmexExchange.class);

        // Interested in the public market data feed (no authentication)
        MarketDataService marketDataService = bitstamp.getMarketDataService();

        generic(marketDataService);
        raw((BitmexMarketDataServiceRaw) marketDataService);
    }

    private static void generic(MarketDataService marketDataService) throws IOException {

        Ticker ticker = marketDataService.getTicker(CurrencyPair.BTC_USD);

        System.out.println(ticker.toString());
    }

    private static void raw(BitmexMarketDataServiceRaw marketDataService) throws IOException {

        //BitmexTicker bitstampTicker = marketDataService.getBinanceTicker(CurrencyPair.BTC_USD);


        //System.out.println(bitstampTicker.toString());
    }

    public static void testURL(String strUrl) throws Exception {

        try {
            URL url = new URL(strUrl);
            //Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost",42209));
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.connect();

            System.out.println( urlConn.getResponseCode());
        } catch (IOException e) {
            System.err.println("Error creating HTTP connection");
            e.printStackTrace();
            throw e;
        }
        System.setProperty("java.net.useSystemProxies", "false");
    }
}
