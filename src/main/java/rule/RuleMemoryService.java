package rule;

import module.Candlestick;
import module.Period;

public interface RuleMemoryService {

    void append(Candlestick candle);


    Double getAverage(Period period, String symbol);
}
