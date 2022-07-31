package notifier.rule;

import common.candlestick.Candlestick;

/***
 * Each RuleMemory attached to one or more rules.
 */
public interface RuleMemory {

    // Add candlestick to its market(defined by candle.symbol) usually last of that.
    void append(Candlestick candle);

    // Get last candlestick of the symbol market.
    Candlestick getLast(String symbol);

    // Return True if the candlestick is duplicated and added previously.
    boolean isDuplicated(Candlestick candlestick);
}
