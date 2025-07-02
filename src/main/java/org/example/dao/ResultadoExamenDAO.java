package org.example.dao;

import org.example.models.ResultadoExamen;
import org.example.utils.DatabaseConnection;
import java.sql.*;
        import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResultadoExamenDAO {

    /**
     * Inserta un resultado de examen
     */
    public boolean insertar(ResultadoExamen resultado) throws SQLException {
        String sql = """
            INSERT INTO resultado_examen 
            (codigo_postulante, puntaje, merito, observacion, respuestas_correctas, 
             respuestas_incorrectas, respuestas_nulas) 
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, resultado.getCodigoPostulante());
            stmt.setDouble(2, resultado.getPuntaje());
            stmt.setString(3, resultado.getMerito());
            stmt.setString(4, resultado.getObservacion() != null ? resultado.getObservacion().getValor() : null);
            stmt.setInt(5, resultado.getRespuestasCorrectas());
            stmt.setInt(6, resultado.getRespuestasIncorrectas());
            stmt.setInt(7, resultado.getRespuestasNulas());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Insertar lote de resultados (crítico para paralelismo)
     */
    public void insertarLote(List<ResultadoExamen> resultados) throws SQLException {
        String sql = """
            INSERT INTO resultado_examen 
            (codigo_postulante, puntaje, merito, observacion, respuestas_correctas, 
             respuestas_incorrectas, respuestas_nulas) 
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            stmt = conn.prepareStatement(sql);

            for (ResultadoExamen resultado : resultados) {
                stmt.setString(1, resultado.getCodigoPostulante());
                stmt.setDouble(2, resultado.getPuntaje());
                stmt.setString(3, resultado.getMerito());
                stmt.setString(4, resultado.getObservacion() != null ? resultado.getObservacion().getValor() : null);
                stmt.setInt(5, resultado.getRespuestasCorrectas());
                stmt.setInt(6, resultado.getRespuestasIncorrectas());
                stmt.setInt(7, resultado.getRespuestasNulas());
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
     * Obtiene resultados por escuela
     */
    public List<ResultadoExamen> obtenerPorEscuela(int idEscuela) throws SQLException {
        List<ResultadoExamen> resultados = new ArrayList<>();
        String sql = """
            SELECT re.*, p.nombre, p.idescuela 
            FROM resultado_examen re
            JOIN postulante p ON re.codigo_postulante = p.codigo_postulante
            WHERE p.idescuela = ?
            ORDER BY re.puntaje DESC
            """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEscuela);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ResultadoExamen resultado = mapearResultado(rs);
                resultados.add(resultado);
            }
        }

        return resultados;
    }

    /**
     * Obtiene top N resultados
     */
    public List<ResultadoExamen> obtenerTopResultados(int limite) throws SQLException {
        List<ResultadoExamen> resultados = new ArrayList<>();
        String sql = """
            SELECT re.*, p.nombre, p.idescuela 
            FROM resultado_examen re
            JOIN postulante p ON re.codigo_postulante = p.codigo_postulante
            ORDER BY re.puntaje DESC
            LIMIT ?
            """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limite);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ResultadoExamen resultado = mapearResultado(rs);
                resultados.add(resultado);
            }
        }

        return resultados;
    }

    /**
     * Actualizar méritos en lote
     */
    public void actualizarMeritos(Map<String, Integer> meritos) throws SQLException {
        String sql = "UPDATE resultado_examen SET merito = ? WHERE codigo_postulante = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            stmt = conn.prepareStatement(sql);

            for (Map.Entry<String, Integer> entry : meritos.entrySet()) {
                stmt.setString(1, entry.getValue().toString());
                stmt.setString(2, entry.getKey());
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
     * Eliminar resultados de un proceso (para pruebas)
     */
    public void limpiarResultados() throws SQLException {
        String sql = "DELETE FROM resultado_examen";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();
        }
    }

    private ResultadoExamen mapearResultado(ResultSet rs) throws SQLException {
        ResultadoExamen resultado = new ResultadoExamen();
        resultado.setId(rs.getInt("id"));
        resultado.setCodigoPostulante(rs.getString("codigo_postulante"));
        resultado.setPuntaje(rs.getDouble("puntaje"));
        resultado.setMerito(rs.getString("merito"));

        String observacion = rs.getString("observacion");
        if (observacion != null) {
            resultado.setObservacion(ResultadoExamen.ObservacionEnum.fromString(observacion));
        }

        resultado.setRespuestasCorrectas(rs.getInt("respuestas_correctas"));
        resultado.setRespuestasIncorrectas(rs.getInt("respuestas_incorrectas"));
        resultado.setRespuestasNulas(rs.getInt("respuestas_nulas"));
        resultado.setFechaEvaluacion(rs.getTimestamp("fecha_evaluacion").toLocalDateTime());

        return resultado;
    }
}