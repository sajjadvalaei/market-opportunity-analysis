package data;

import module.Candlestick;

import java.net.ConnectException;

public interface DataCollector {
    public Candlestick getLastCandlestick(String symbol) throws ConnectException, SymbolNotFoundException;
    public Candlestick getLastCandlestickExceptionNotHandled(String symbol);

}
