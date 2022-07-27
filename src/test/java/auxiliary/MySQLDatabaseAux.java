package auxiliary;

public class MySQLDatabaseAux {

    public static void changeJDBCURLForTest(String jdbcUrl) {
        jdbcUrl = dropDatabaseName(jdbcUrl);
        database.MySQLDatabase.DB_URL = jdbcUrl;
    }

    private static String dropDatabaseName(String jdbcUrl) {
        int lastSlashIndex = jdbcUrl.lastIndexOf("/");
        return jdbcUrl.substring(0,lastSlashIndex + 1);

    }
}
