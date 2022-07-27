package auxiliary;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class NotificationAux {
    public static module.Notification createRandomNotification() {
        Random random = new Random();
        return new module.Notification(randomRuleNameGenerator(),
                CandlestickAux.randomSymbolGenerator(),
                random.nextDouble(), random.nextLong());
    }


    public static String randomRuleNameGenerator() {
        Random random = new Random();
        return Double.toString(random.nextDouble());
    }

    public static String selectAllQueryStringStatement() {
        return module.Notification.getSelectAllQueryStringStatement();
    }

    public static boolean resultSetContains(ResultSet resultSet, module.Notification notification) throws SQLException {
        while (resultSet.next()){
            module.Notification other = new module.Notification(resultSet);
            if (Objects.equals(notification, other))
                return true;
        }
        return false;
    }

    public static List<module.Notification> createRandomNotificationList(int num) {
        List<module.Notification> notifications = new ArrayList<>();
        for(int index = 0; index < num; index++) {
            module.Notification notif = createRandomNotification();
            notifications.add(notif);
        }
        return notifications;
    }
}
