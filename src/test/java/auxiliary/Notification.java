package auxiliary;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Random;

public class Notification {
    public static module.Notification createRandomNotification() {
        Random random = new Random();
        return new module.Notification(randomRuleNameGenerator(),
                Candlestick.randomSymbolGenerator(),
                random.nextDouble(), random.nextLong());
    }


    public static String randomRuleNameGenerator() {
        Random random = new Random();
        return Double.toString(random.nextDouble());
    }

    public static String selectAllQueryStringStatement() {
        return module.Notification.selectAllQueryStringStatement();
    }

    public static boolean resultSetContains(ResultSet resultSet, module.Notification notification) throws SQLException {
        while(resultSet.next()){
            module.Notification other = new module.Notification(resultSet);
            if(Objects.equals(notification, other))
                return true;
        }
        return false;
    }
}
