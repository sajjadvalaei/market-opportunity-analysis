package common.notification;

import java.util.Objects;

public class Notification {

    private final String ruleName;
    private final String symbol;
    private final Double currentPrice;
    private final Long openTime;

    public Notification(String ruleName, String symbol, double currentPrice, long openTime) {
        this.ruleName = ruleName;
        this.symbol = symbol;
        this.currentPrice = currentPrice;
        this.openTime = openTime;
    }
    String getRuleName(){
        return ruleName;
    }
    String getSymbol(){
        return symbol;
    }
    Double getCurrentPrice(){
        return currentPrice;
    }
    Long getOpenTime(){
        return openTime;
    }

    @Override
    public boolean equals(Object other){
        if(!(other instanceof Notification ))
            return false;
        Notification o = (Notification) other;
        return Objects.equals(o.openTime, openTime) &&
                Objects.equals(o.symbol, symbol) &&
                Objects.equals(o.currentPrice,currentPrice) &&
                Objects.equals(o.ruleName, ruleName);
    }

    

    public String print() {
        return ruleName +" "+ symbol + " " + currentPrice + " " + openTime;
    }

}



