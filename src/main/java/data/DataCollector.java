package data;

import module.Candlestick;

public interface DataCollector {
    public Candlestick getLastCandlestick(String binanceKey);

}
