package classes;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class DBHelper implements AutoCloseable{

    private static DBHelper instance;
    private static Connection connection;

    private static final Logger logger = LogManager.getLogManager().getLogger(DBHelper.class.getName());

    private static PreparedStatement selectStatement;
    private static PreparedStatement updateStatement;

    private DBHelper() {}

    public static DBHelper getInstance() {
        if(instance == null) {
            initConnection();
            initPreparedStatements();

            instance = new DBHelper();
            logger.log(Level.CONFIG, "Создан экземпляр класса DBHelper");
        }

        return instance;
    }

    private static void initConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
        } catch (ClassNotFoundException | SQLException e) {
            logger.log(Level.SEVERE, "Не удалось подключиться к базе данных");
            e.printStackTrace();
        }
    }

    private static void initPreparedStatements() {
        try {
            selectStatement = connection.prepareStatement("SELECT username, password FROM users " +
                    "WHERE LOWER(username) = LOWER(?) AND password = ?");
            updateStatement = connection.prepareStatement("UPDATE users SET username = LOWER(?) WHERE LOWER(username) = LOWER(?)");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при инициализации PreparedStatements");
            e.printStackTrace();
        }
    }

    public static String authorization(String username, String password) {
        try {
            selectStatement.setString(1, username);
            selectStatement.setString(2, password);
            ResultSet resultSet = selectStatement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getString("username");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Возникла ошибка при авторизации пользователя");
            e.printStackTrace();
        }

        return null;
    }

    public static int updateUsername(String oldUsername, String newUsername) {
        try {
            updateStatement.setString(1, newUsername);
            updateStatement.setString(2, oldUsername);
            return updateStatement.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Возникла ошибка при изменение имени пользователя");
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public void close() throws Exception {
        connection.close();
        selectStatement.close();
        updateStatement.close();
    }
}
