package ru.nstu.logbook.net;

import ru.nstu.logbook.notes.Note;
import ru.nstu.logbook.notes.Reminder;

import java.io.*;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class DBManager {
    private static String URL;
    private static String USERNAME;
    private static String PASSWORD;
    private static final String SETTINGS_FILE_DIR = "./src/main/resources/ru/nstu/logbook/db";
    private static final String SETTINGS_FILE = "settings.ini";
    private static Connection connection;

    static {
        loadDatabaseSettings();
        try {
            if (getConnection() != null) {
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void loadDatabaseSettings() {
        Properties properties = new Properties();
        File file = new File(SETTINGS_FILE_DIR, SETTINGS_FILE);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                properties.load(fis);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try (FileWriter writer = new FileWriter(file)) {
                Class.forName("org.postgresql.Driver").getDeclaredConstructor().newInstance();
                properties.setProperty("URL", "jdbc:postgresql://127.0.0.1:5432/postgres");
                properties.setProperty("USERNAME", "postgres");
                properties.setProperty("PASSWORD", "password");
                properties.store(writer, "Database settings");
            } catch (Exception e) {
                ;
            }
        }
        URL = properties.getProperty("URL");
        USERNAME = properties.getProperty("USERNAME");
        PASSWORD = properties.getProperty("PASSWORD");
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            if (URL.isEmpty() || USERNAME.isEmpty() || PASSWORD.isEmpty()) {
                throw new SQLException("Database settings are not configured properly.");
            }
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null)
                connection.close();
        } catch (SQLException e) {
            System.err.println("Cant close the connection");
            throw new RuntimeException(e);
        }
    }

    //пользователи и бд
    public static int registerUser(String username, String password) throws SQLException {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.executeUpdate();
            return authenticateUser(username, password);
        }
    }

    public static boolean checkUserExists(String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public static int authenticateUser(String username, String password) throws SQLException {
        String sql = "SELECT user_id FROM users WHERE username = ? AND password = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("user_id");
                }
            }
        }
        return -1;
    }

    //записки
    public static Map<LocalDate, Note> getNotesForPeriod(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        Map<LocalDate, Note> notes = new HashMap<>();
        String query = "SELECT * FROM notes WHERE user_id = ? AND date BETWEEN ? AND ? ORDER BY date";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setDate(2, Date.valueOf(startDate));
            statement.setDate(3, Date.valueOf(endDate));
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                LocalDate date = resultSet.getDate("date").toLocalDate();
                String topic = resultSet.getString("topic");
                String content = resultSet.getString("inners");
                notes.put(date, new Note(date, topic, content));
            }
        }
        return notes;
    }

    public static void addNoteForUser(int userId, Note note) throws SQLException {
        String query = "INSERT INTO notes (user_id, date, topic, inners) VALUES (?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setDate(2, Date.valueOf(note.getDate()));
            statement.setString(3, note.getTopic());
            statement.setString(4, note.getTopic());
            statement.executeUpdate();
        }
    }

    public static void deleteNoteForUser(int userId, LocalDate date) throws SQLException {
        String sql = "DELETE FROM notes WHERE user_id = ? AND date = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setDate(2, Date.valueOf(date));

            statement.executeUpdate();
        }
    }

    public static Map<LocalDate, Note> getNotesForUser(int userId) throws SQLException {
        Map<LocalDate, Note> notes = new HashMap<>();
        String sql = "SELECT * FROM notes WHERE user_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    LocalDate date = resultSet.getDate("date").toLocalDate();
                    String topic = resultSet.getString("topic");
                    String content = resultSet.getString("inners");
                    notes.put(date, new Note(date, topic, content));
                }
            }
        }
        return notes;
    }

    //напоминания

    public static ArrayList<Reminder> getRemindsForPeriod(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        ArrayList<Reminder> reminders = new ArrayList<>();
        String query = "SELECT * FROM reminds WHERE user_id = ? AND date BETWEEN ? AND ? ORDER BY date";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setDate(2, Date.valueOf(startDate));
            statement.setDate(3, Date.valueOf(endDate));
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                LocalDate date = resultSet.getDate("date").toLocalDate();
                LocalTime time = resultSet.getTime("time").toLocalTime();
                String topic = resultSet.getString("topic");
                String content = resultSet.getString("inners");

                reminders.add(new Reminder(date, time, topic, content));
            }
        }
        return reminders;
    }

    public static void addReminderForUser(int userId, Reminder reminder) throws SQLException {
        String query = "INSERT INTO reminds (user_id, date, time, topic, inners) VALUES (?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setDate(2, Date.valueOf(reminder.getExpirationDate()));
            statement.setTime(3, Time.valueOf(reminder.getExpirationTime()));
            statement.setString(4, reminder.getTopic());
            statement.setString(5, reminder.getContent());
            statement.executeUpdate();
        }
    }

    public static void deleteReminderForUser(int userId, Reminder reminder) throws SQLException {
        String sql = "DELETE FROM reminds WHERE user_id = ? AND date = ? AND time = ? AND topic = ? AND inners = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setDate(2, Date.valueOf(reminder.getExpirationDate()));
            statement.setTime(3, Time.valueOf(reminder.getExpirationTime()));
            statement.setString(4, reminder.getTopic());
            statement.setString(5, reminder.getContent());

            statement.executeUpdate();
        }
    }

    public static ArrayList<Reminder> getRemindsForUser(int userId) throws SQLException {
        ArrayList<Reminder> reminders = new ArrayList<>();
        String sql = "SELECT * FROM reminds WHERE user_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    LocalDate date = resultSet.getDate("date").toLocalDate();
                    LocalTime time = resultSet.getTime("time").toLocalTime();
                    String topic = resultSet.getString("topic");
                    String content = resultSet.getString("inners");
                    reminders.add(new Reminder(date, time, topic, content));
                }
            }
        }
        return reminders;
    }

    //планы

    public static void addPlanForUser(int userId, String plan) throws SQLException {
        String query = "INSERT INTO bigplans (user_id, inners) VALUES (?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        }
    }

    public static void deletePlanForUser(int userId, LocalDate date) throws SQLException {
        String sql = "DELETE FROM bigplans WHERE user_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        }
    }

    public static String getPlanForUser(int userId) throws SQLException {
        String plan = "";
        String sql = "SELECT inners FROM bigplans WHERE user_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();//??
                plan = resultSet.getString("inners");
            }
        }
        return plan;
    }

}

