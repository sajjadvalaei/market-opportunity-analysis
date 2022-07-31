package fetcher.collector;

import common.candlestick.Candlestick;
import fetcher.exception.SymbolNotFoundException;

import java.net.ConnectException;

public interface DataCollector {
    public Candlestick getLastCandlestick(String symbol) throws ConnectException, SymbolNotFoundException;

}
