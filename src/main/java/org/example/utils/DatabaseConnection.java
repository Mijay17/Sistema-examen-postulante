package org.example.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Configuración de la base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/db_examen_admision";
    private static final String USERNAME = "root";
    private static final String PASSWORD = ""; // Cambia por tu contraseña
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    // Instancia única (Singleton)
    private static DatabaseConnection instance;

    // Constructor privado
    private DatabaseConnection() {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error al cargar el driver de MySQL", e);
        }
    }

    // Método para obtener la instancia única
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    // Método para obtener una conexión
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    // Método para probar la conexión
    public boolean testConnection() {
        try (Connection connection = getConnection()) {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
            return false;
        }
    }

    // Método para cerrar una conexión de forma segura
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}