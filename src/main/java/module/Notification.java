package module;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class Notification {
    private final static String TABLE_NAME = "Notifications";
    private final static String RULE_NAME_KEY = "rule_name";
    private final static String SYMBOL_KEY = "symbol";
    private final static String CURRENT_PRICE_KEY = "current_price";
    private final static String OPEN_TIME_KEY = "open_time";
    private String ruleName;
    private String symbol;
    private Double currentPrice;
    private Long openTime;

    public Notification(String ruleName, String symbol, double currentPrice, long openTime) {
        this.ruleName = ruleName;
        this.symbol = symbol;
        this.currentPrice = currentPrice;
        this.openTime = openTime;
    }

    public Notification(ResultSet resultSet) throws SQLException {
        ruleName = resultSet.getString(RULE_NAME_KEY);
        symbol = resultSet.getString(SYMBOL_KEY);
        currentPrice = resultSet.getDouble(CURRENT_PRICE_KEY);
        openTime = resultSet.getLong(OPEN_TIME_KEY);
    }

    public static String selectAllQueryStringStatement() {
        return "SELECT "+RULE_NAME_KEY+", "+SYMBOL_KEY+", "+CURRENT_PRICE_KEY+", "+ OPEN_TIME_KEY +" FROM " +TABLE_NAME;
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
}



