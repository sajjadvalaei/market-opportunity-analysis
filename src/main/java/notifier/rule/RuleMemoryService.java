package notifier.rule;

import common.candlestick.Candlestick;

public interface RuleMemoryService {

    void append(Candlestick candle);


    Candlestick getLast(String symbol);

    boolean isDuplicated(Candlestick candlestick);
}
