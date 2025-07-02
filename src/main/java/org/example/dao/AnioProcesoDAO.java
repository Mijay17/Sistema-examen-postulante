package org.example.dao;

import org.example.models.AnioProceso;
import org.example.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnioProcesoDAO {

    /**
     * Obtiene todos los procesos
     */
    public List<AnioProceso> obtenerTodos() throws SQLException {
        List<AnioProceso> procesos = new ArrayList<>();
        String sql = "SELECT * FROM anio_proceso ORDER BY year DESC, proceso";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                AnioProceso proceso = new AnioProceso();
                proceso.setId(rs.getInt("id"));
                proceso.setYear(rs.getInt("year"));
                proceso.setProceso(rs.getString("proceso"));
                procesos.add(proceso);
            }
        }

        return procesos;
    }

    /**
     * Obtiene proceso actual (más reciente)
     */
    public AnioProceso obtenerActual() throws SQLException {
        String sql = "SELECT * FROM anio_proceso ORDER BY year DESC, id DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                AnioProceso proceso = new AnioProceso();
                proceso.setId(rs.getInt("id"));
                proceso.setYear(rs.getInt("year"));
                proceso.setProceso(rs.getString("proceso"));
                return proceso;
            }
        }

        return null;
    }

    /**
     * Insertar nuevo proceso
     */
    public boolean insertar(AnioProceso proceso) throws SQLException {
        String sql = "INSERT INTO anio_proceso (year, proceso) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, proceso.getYear());
            stmt.setString(2, proceso.getProceso());

            return stmt.executeUpdate() > 0;
        }
    }
}
