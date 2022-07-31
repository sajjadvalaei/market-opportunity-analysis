package api;

import com.google.gson.Gson;
import common.database.MySQLDatabase;
import common.notification.Notification;

import java.util.List;

import static common.config.Configuration.*;
import static spark.Spark.get;

public class APIHandler {
    static MySQLDatabase database = new MySQLDatabase(DATABASE_URL,USER,PASSWORD,DATABASE_NAME);
    public static void main(String[] args) {
        get("/notifications", (request, response) -> {
            response.type("application/json");
            return new Gson().toJson(
                    new StandardResponse(StatusResponse.SUCCESS,new Gson()
                            .toJsonTree(getNotificationListFromDatabase())));
        });
    }

    private static List<Notification> getNotificationListFromDatabase() {
        return database.getNotificationList();
    }
}
