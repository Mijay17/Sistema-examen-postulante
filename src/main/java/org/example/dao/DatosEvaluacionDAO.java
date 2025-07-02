package org.example.dao;

import org.example.models.dto.DatosPostulante;
import org.example.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatosEvaluacionDAO {

    /**
     * Obtiene todos los datos necesarios para evaluación paralela
     * Crítico para el rendimiento del procesamiento
     */
    public List<DatosPostulante> obtenerDatosCompletos() throws SQLException {
        List<DatosPostulante> datos = new ArrayList<>();
        String sql = """
            SELECT 
                p.codigo_postulante,
                p.nombre,
                p.idescuela,
                p.idproceso,
                r.respuesta,
                e.nombre as nombre_escuela
            FROM postulante p
            JOIN respuestas r ON p.codigo_postulante = r.codigo_postulante
            JOIN escuela_profesional e ON p.idescuela = e.id
            ORDER BY p.idescuela, p.codigo_postulante
            """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                DatosPostulante dato = new DatosPostulante(
                        rs.getString("codigo_postulante"),
                        rs.getString("nombre"),
                        rs.getInt("idescuela"),
                        rs.getString("respuesta")
                );
                dato.setNombreEscuela(rs.getString("nombre_escuela"));
                datos.add(dato);
            }
        }

        return datos;
    }

    /**
     * Obtiene datos por escuela (para procesamiento paralelo por escuela)
     */
    public List<DatosPostulante> obtenerPorEscuela(int idEscuela) throws SQLException {
        List<DatosPostulante> datos = new ArrayList<>();
        String sql = """
            SELECT 
                p.codigo_postulante,
                p.nombre,
                p.idescuela,
                r.respuesta
            FROM postulante p
            JOIN respuestas r ON p.codigo_postulante = r.codigo_postulante
            WHERE p.idescuela = ?
            ORDER BY p.codigo_postulante
            """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEscuela);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                DatosPostulante dato = new DatosPostulante(
                        rs.getString("codigo_postulante"),
                        rs.getString("nombre"),
                        rs.getInt("idescuela"),
                        rs.getString("respuesta")
                );
                datos.add(dato);
            }
        }

        return datos;
    }

    /**
     * Obtiene el gabarito para un proceso específico
     */
    public String obtenerGabarito(int idProceso) throws SQLException {
        // Por ahora retorna gabarito fijo, en producción vendría de BD
        return "ABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCD";
    }
}