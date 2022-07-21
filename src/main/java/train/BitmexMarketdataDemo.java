package train;
import java.io.IOException;
import java.util.List;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.bitmex.BitmexExchange;
import org.knowm.xchange.bitmex.dto.account.BitmexTicker;
import org.knowm.xchange.bitmex.service.BitmexMarketDataServiceRaw;
import org.knowm.xchange.service.marketdata.MarketDataService;

public class BitmexMarketdataDemo {

    public static void main(String[] args) throws IOException {

        Exchange exchange = BitmexDemoUtils.createExchange();
        MarketDataService service = exchange.getMarketDataService();

        ticker(service);
    }

    private static void ticker(MarketDataService service) throws IOException {

        // Get the ticker/markets information
        BitmexMarketDataServiceRaw serviceRaw = (BitmexMarketDataServiceRaw) service;
        List<BitmexTicker> tickers = serviceRaw.getActiveTickers();
        System.out.println(tickers);

        tickers = serviceRaw.getTicker("Xbt");
        System.out.println(tickers);

        List<BitmexTicker> ticker = serviceRaw.getTicker("XBt");
        System.out.println(ticker);
    }
}


class BitmexDemoUtils {

    public static Exchange createExchange() {
        System.setProperty("java.net.useSystemProxies", "true");
        // Use the factory to get Bitmex exchange API using default settings
        Exchange bitmex = ExchangeFactory.INSTANCE.createExchange(BitmexExchange.class);

        ExchangeSpecification bitmexSpec = bitmex.getDefaultExchangeSpecification();

        // bitmexSpec.setApiKey("");
        // bitmexSpec.setSecretKey("");

        bitmex.applySpecification(bitmexSpec);

        return bitmex;
    }
}