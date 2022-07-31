package fetcher.collector;

import common.candlestick.Candlestick;
import fetcher.exception.SymbolNotFoundException;

import java.net.ConnectException;

public interface DataCollector {
    // last minute candle from the symbol market.
    public Candlestick getLastCandlestick(String symbol) throws ConnectException, SymbolNotFoundException;

}
