import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.CandlestickInterval;
import common.config.Configuration;
import fetcher.collector.BinanceDataCollector;
import fetcher.collector.DataCollector;
import fetcher.exception.SymbolNotFoundException;

import java.net.ConnectException;

public class test {
    static{
        System.out.println("salam");
        Configuration.setSystemProxy();
    }
    final static DataCollector dataCollector = BinanceDataCollector.getDataCollector();
    public static void main(String[] args) throws SymbolNotFoundException, ConnectException {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiRestClient client = factory.newRestClient();
        System.out.println(client.getCandlestickBars("NEOETH", CandlestickInterval.ONE_MINUTE));
        System.out.println(dataCollector.getLastCandlestick("NEOETH"));
    }

}
