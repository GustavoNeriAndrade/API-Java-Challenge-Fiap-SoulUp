package br.com.fiap.dao;

import br.com.fiap.conexao.ConexaoBancoDados;
import br.com.fiap.model.ImpactoAmbiental;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ImpactAmbientalDAO {

    private static final String COLUNAS =
            "id_impacto, km_percorridos, co2_economizado, arvores_equivalentes, "
                    + "data_registro, Usuario_id_usuario";

    // Create
    public void inserir(ImpactoAmbiental impacto) throws SQLException {
        String sql = "INSERT INTO ImpactoAmbiental "
                + "(km_percorridos, co2_economizado, arvores_equivalentes, data_registro, Usuario_id_usuario) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql, new String[]{"id_impacto"})) {

            stmt.setBigDecimal(1, impacto.getKmPercorridos());
            stmt.setBigDecimal(2, impacto.getCo2Economizado());
            stmt.setBigDecimal(3, impacto.getArvoresEquivalentes());
            stmt.setTimestamp(4, Timestamp.valueOf(impacto.getDataRegistro()));
            stmt.setInt(5, impacto.getIdUsuario());
            stmt.executeUpdate();

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    impacto.setId(chaves.getInt(1));
                }
            }
        }
    }

    // Read
    public ImpactoAmbiental buscarPorId(int id) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM ImpactoAmbiental WHERE id_impacto = ?";

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? montarImpacto(rs) : null;
            }
        }
    }

    public List<ImpactoAmbiental> listarPorUsuario(int idUsuario) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM ImpactoAmbiental "
                + "WHERE Usuario_id_usuario = ? ORDER BY data_registro DESC, id_impacto DESC";

        List<ImpactoAmbiental> lista = new ArrayList<>();

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(montarImpacto(rs));
                }
            }
        }
        return lista;
    }

    // Update
    public boolean atualizar(ImpactoAmbiental impacto) throws SQLException {
        String sql = "UPDATE ImpactoAmbiental SET km_percorridos = ?, co2_economizado = ?, "
                + "arvores_equivalentes = ? WHERE id_impacto = ?";

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setBigDecimal(1, impacto.getKmPercorridos());
            stmt.setBigDecimal(2, impacto.getCo2Economizado());
            stmt.setBigDecimal(3, impacto.getArvoresEquivalentes());
            stmt.setInt(4, impacto.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    // Delete
    public boolean deletar(int id) throws SQLException {
        String sql = "DELETE FROM ImpactoAmbiental WHERE id_impacto = ?";

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private ImpactoAmbiental montarImpacto(ResultSet rs) throws SQLException {
        return new ImpactoAmbiental(
                rs.getInt("id_impacto"),
                rs.getBigDecimal("km_percorridos"),
                rs.getBigDecimal("co2_economizado"),
                rs.getBigDecimal("arvores_equivalentes"),
                rs.getTimestamp("data_registro").toLocalDateTime(),
                rs.getInt("Usuario_id_usuario"));
    }
}