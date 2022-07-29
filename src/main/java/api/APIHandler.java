package api;

import api.StandardResponse;
import api.StatusResponse;
import com.google.gson.Gson;
import database.MySQLDatabase;
import module.Notification;

import java.util.List;

import static config.Configuration.DATABASE_NAME;
import static spark.Spark.get;

public class APIHandler {
    static MySQLDatabase database;
    static{
        MySQLDatabase.start(DATABASE_NAME);
        database = MySQLDatabase.getDatabase();
    }
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
