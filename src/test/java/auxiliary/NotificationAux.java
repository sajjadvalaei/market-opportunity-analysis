package auxiliary;

import common.notification.Notification;
import common.notification.NotificationSQLService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class NotificationAux {
    public static Notification createRandomNotification() {
        Random random = new Random();
        return new Notification(randomRuleNameGenerator(),
                CandlestickAux.randomSymbolGenerator(),
                random.nextDouble(), random.nextLong());
    }


    public static String randomRuleNameGenerator() {
        Random random = new Random();
        return Double.toString(random.nextDouble());
    }

    public static String selectAllQueryStringStatement() {
        return NotificationSQLService.getSelectAllQueryStringStatement();
    }

    public static boolean resultSetContains(ResultSet resultSet, Notification notification) throws SQLException {
        while (resultSet.next()){
            Notification other = NotificationSQLService.extractNotification(resultSet);
            if (Objects.equals(notification, other))
                return true;
        }
        return false;
    }

    public static List<Notification> createRandomNotificationList(int num) {
        List<Notification> notifications = new ArrayList<>();
        for(int index = 0; index < num; index++) {
            Notification notif = createRandomNotification();
            notifications.add(notif);
        }
        return notifications;
    }
}
