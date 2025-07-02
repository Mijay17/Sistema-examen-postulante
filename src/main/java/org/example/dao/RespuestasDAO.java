package org.example.dao;

import org.example.models.Respuestas;
import org.example.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class RespuestasDAO {

    /**
     * Obtiene todas las respuestas
     */
    public List<Respuestas> obtenerTodas() throws SQLException {
        List<Respuestas> respuestas = new ArrayList<>();
        String sql = "SELECT * FROM respuestas";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Respuestas respuesta = new Respuestas();
                respuesta.setId(rs.getInt("id"));
                respuesta.setCodigoPostulante(rs.getString("codigo_postulante"));
                respuesta.setRespuesta(rs.getString("respuesta"));
                respuestas.add(respuesta);
            }
        }

        return respuestas;
    }

    /**
     * Obtiene respuestas por código de postulante
     */
    public Respuestas obtenerPorCodigo(String codigoPostulante) throws SQLException {
        String sql = "SELECT * FROM respuestas WHERE codigo_postulante = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigoPostulante);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Respuestas respuesta = new Respuestas();
                respuesta.setId(rs.getInt("id"));
                respuesta.setCodigoPostulante(rs.getString("codigo_postulante"));
                respuesta.setRespuesta(rs.getString("respuesta"));
                return respuesta;
            }
        }

        return null;
    }

    /**
     * Inserta nuevas respuestas
     */
    public boolean insertar(Respuestas respuesta) throws SQLException {
        String sql = "INSERT INTO respuestas (codigo_postulante, respuesta) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, respuesta.getCodigoPostulante());
            stmt.setString(2, respuesta.getRespuesta());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Insertar lote de respuestas (optimizado para paralelismo)
     */
    public void insertarLote(List<Respuestas> respuestas) throws SQLException {
        String sql = "INSERT INTO respuestas (codigo_postulante, respuesta) VALUES (?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            stmt = conn.prepareStatement(sql);

            for (Respuestas respuesta : respuestas) {
                stmt.setString(1, respuesta.getCodigoPostulante());
                stmt.setString(2, respuesta.getRespuesta());
                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    /**
     * Obtiene mapa de código -> respuestas para procesamiento paralelo
     */
    public Map<String, String> obtenerMapaRespuestas() throws SQLException {
        Map<String, String> mapa = new HashMap<>();
        String sql = "SELECT codigo_postulante, respuesta FROM respuestas";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                mapa.put(rs.getString("codigo_postulante"), rs.getString("respuesta"));
            }
        }

        return mapa;
    }
}