package org.example.dao;

import org.example.models.Local;
import org.example.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocalDAO {

    /**
     * Obtiene todos los locales
     */
    public List<Local> obtenerTodos() throws SQLException {
        List<Local> locales = new ArrayList<>();
        String sql = "SELECT * FROM local ORDER BY nombre";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Local local = new Local();
                local.setId(rs.getInt("id"));
                local.setNombre(rs.getString("nombre"));
                locales.add(local);
            }
        }

        return locales;
    }

    /**
     * Obtiene local por ID
     */
    public Local obtenerPorId(int id) throws SQLException {
        String sql = "SELECT * FROM local WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Local local = new Local();
                local.setId(rs.getInt("id"));
                local.setNombre(rs.getString("nombre"));
                return local;
            }
        }

        return null;
    }

    /**
     * Insertar nuevo local
     */
    public boolean insertar(Local local) throws SQLException {
        String sql = "INSERT INTO local (nombre) VALUES (?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, local.getNombre());

            return stmt.executeUpdate() > 0;
        }
    }
}
