package module;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class Notification {
    private final static String TABLE_NAME = "Notification";
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

    public static String getSelectAllQueryStringStatement() {
        return "SELECT "+ getSQLFieldsStatement() +" FROM " +TABLE_NAME;
    }

    private static String getSQLFieldsStatement() {
        return RULE_NAME_KEY+", "+SYMBOL_KEY+", "+CURRENT_PRICE_KEY+", "+ OPEN_TIME_KEY;
    }

    public static String createTableStatement() {
        return "CREATE TABLE IF NOT EXISTS "
                + Notification.class.getSimpleName() + " ( "
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + RULE_NAME_KEY +"  VARCHAR(255) NOT NULL, "
                + SYMBOL_KEY + "  VARCHAR(255) NOT NULL, "
                + CURRENT_PRICE_KEY + " DOUBLE, "
                + OPEN_TIME_KEY +" BIGINT )";
    }

    public static String getInsertAllQueryStatement() {
        return "INSERT INTO " + Notification.class.getSimpleName()
                + "("+ getSQLFieldsStatement()+") "
                + "VALUES(?,?,?,?)";
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

    public void setParameters(PreparedStatement pstmt) throws SQLException {
        pstmt.setString(1,ruleName);
        pstmt.setString(2,symbol);
        pstmt.setDouble(3,currentPrice);
        pstmt.setLong(4,openTime);
    }

    public String print() {
        return ruleName +" "+ symbol + " " + currentPrice + " " + openTime;
    }
}



