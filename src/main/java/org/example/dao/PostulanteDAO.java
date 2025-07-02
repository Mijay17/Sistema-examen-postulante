package org.example.dao;

import org.example.models.Postulante;
import org.example.models.dto.DatosPostulante;
import org.example.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostulanteDAO {

    public List<Postulante> obtenerTodos() throws SQLException {
        List<Postulante> postulantes = new ArrayList<>();
        String sql = "SELECT * FROM postulante";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Postulante postulante = new Postulante();
                postulante.setCodigoPostulante(rs.getString("codigo_postulante"));
                postulante.setNombre(rs.getString("nombre"));
                postulante.setIdEscuela(rs.getInt("idescuela"));
                postulante.setIdProceso(rs.getInt("idproceso"));
                postulantes.add(postulante);
            }
        }

        return postulantes;
    }

    public List<DatosPostulante> obtenerPostulantesConRespuestas() throws SQLException {
        List<DatosPostulante> datos = new ArrayList<>();
        String sql = """
            SELECT p.codigo_postulante, p.nombre, p.idescuela, r.respuesta
            FROM postulante p
            JOIN respuestas r ON p.codigo_postulante = r.codigo_postulante
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
                datos.add(dato);
            }
        }

        return datos;
    }
}