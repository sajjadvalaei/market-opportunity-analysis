package controller;

import api.StandardResponse;
import api.StatusResponse;
import com.google.gson.Gson;
import database.MySQLDatabase;
import module.Notification;

import java.util.List;

import static spark.Spark.get;

public class APIHandler {
    private static final String DATABASE_NAME = "Market";
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
