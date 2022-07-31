package common.database;

import common.notification.Notification;

import java.util.List;

public interface Database {

    void insert(Notification notification);

    List<Notification> getNotificationList();
}
