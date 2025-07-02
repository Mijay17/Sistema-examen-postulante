package org.example.dao;

import org.example.models.EscuelaProfesional;
import org.example.models.Local;
import org.example.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EscuelaProfesionalDAO {

    /**
     * Obtiene todas las escuelas con sus locales
     */
    public List<EscuelaProfesional> obtenerTodas() throws SQLException {
        List<EscuelaProfesional> escuelas = new ArrayList<>();
        String sql = """
            SELECT e.id, e.nombre, e.idlocal, l.nombre as nombre_local
            FROM escuela_profesional e
            LEFT JOIN local l ON e.idlocal = l.id
            ORDER BY e.nombre
            """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                EscuelaProfesional escuela = new EscuelaProfesional();
                escuela.setId(rs.getInt("id"));
                escuela.setNombre(rs.getString("nombre"));
                escuela.setIdLocal(rs.getInt("idlocal"));

                // Crear objeto Local asociado
                Local local = new Local();
                local.setId(rs.getInt("idlocal"));
                local.setNombre(rs.getString("nombre_local"));
                escuela.setLocal(local);

                escuelas.add(escuela);
            }
        }

        return escuelas;
    }

    /**
     * Obtiene escuela por ID
     */
    public EscuelaProfesional obtenerPorId(int id) throws SQLException {
        String sql = """
            SELECT e.id, e.nombre, e.idlocal, l.nombre as nombre_local
            FROM escuela_profesional e
            LEFT JOIN local l ON e.idlocal = l.id
            WHERE e.id = ?
            """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                EscuelaProfesional escuela = new EscuelaProfesional();
                escuela.setId(rs.getInt("id"));
                escuela.setNombre(rs.getString("nombre"));
                escuela.setIdLocal(rs.getInt("idlocal"));

                Local local = new Local();
                local.setId(rs.getInt("idlocal"));
                local.setNombre(rs.getString("nombre_local"));
                escuela.setLocal(local);

                return escuela;
            }
        }

        return null;
    }

    /**
     * Insertar nueva escuela
     */
    public boolean insertar(EscuelaProfesional escuela) throws SQLException {
        String sql = "INSERT INTO escuela_profesional (nombre, idlocal) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, escuela.getNombre());
            stmt.setInt(2, escuela.getIdLocal());

            return stmt.executeUpdate() > 0;
        }
    }
}
