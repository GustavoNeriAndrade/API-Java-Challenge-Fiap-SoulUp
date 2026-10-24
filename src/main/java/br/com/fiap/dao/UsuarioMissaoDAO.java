package br.com.fiap.dao;

import br.com.fiap.conexao.ConexaoBancoDados;
import br.com.fiap.model.UsuarioMissao;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UsuarioMissaoDAO {

    private static final String COLUNAS =
            "Usuario_id_usuario, Missao_id_missao, data_conclusao, pontos_ganhos";

    // Create: registra a conclusão e credita os pontos na mesma transação
    public void concluir(UsuarioMissao um) throws SQLException {
        String sqlInsert = "INSERT INTO Usuario_missao "
                + "(Usuario_id_usuario, Missao_id_missao, data_conclusao, pontos_ganhos) "
                + "VALUES (?, ?, ?, ?)";
        String sqlPontos = "UPDATE Usuario SET pontos_usuario = pontos_usuario + ? "
                + "WHERE id_usuario = ?";

        try (Connection conexao = ConexaoBancoDados.getConnection()) {
            conexao.setAutoCommit(false);

            try (PreparedStatement insere = conexao.prepareStatement(sqlInsert);
                 PreparedStatement credita = conexao.prepareStatement(sqlPontos)) {

                insere.setInt(1, um.getIdUsuario());
                insere.setInt(2, um.getIdMissao());
                insere.setTimestamp(3, Timestamp.valueOf(um.getDataConclusao()));
                insere.setInt(4, um.getPontosGanhos());
                insere.executeUpdate();

                credita.setInt(1, um.getPontosGanhos());
                credita.setInt(2, um.getIdUsuario());
                credita.executeUpdate();

                conexao.commit();
            } catch (SQLException e) {
                conexao.rollback();
                throw e;
            }
        }
    }

    // Read
    public UsuarioMissao buscar(int idUsuario, int idMissao) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM Usuario_missao "
                + "WHERE Usuario_id_usuario = ? AND Missao_id_missao = ?";

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idMissao);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? montarUsuarioMissao(rs) : null;
            }
        }
    }

    public boolean jaConcluiu(int idUsuario, int idMissao) throws SQLException {
        String sql = "SELECT 1 FROM Usuario_missao "
                + "WHERE Usuario_id_usuario = ? AND Missao_id_missao = ?";

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idMissao);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<UsuarioMissao> listarPorUsuario(int idUsuario) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM Usuario_missao "
                + "WHERE Usuario_id_usuario = ? ORDER BY data_conclusao DESC";

        List<UsuarioMissao> lista = new ArrayList<>();

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(montarUsuarioMissao(rs));
                }
            }
        }
        return lista;
    }

    // Update
    public boolean atualizar(UsuarioMissao um) throws SQLException {
        String sql = "UPDATE Usuario_missao SET data_conclusao = ?, pontos_ganhos = ? "
                + "WHERE Usuario_id_usuario = ? AND Missao_id_missao = ?";

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(um.getDataConclusao()));
            stmt.setInt(2, um.getPontosGanhos());
            stmt.setInt(3, um.getIdUsuario());
            stmt.setInt(4, um.getIdMissao());
            return stmt.executeUpdate() > 0;
        }
    }

    // Delete: remove só o registro da conclusão (não estorna pontos: isso seria regra do BO)
    public boolean deletar(int idUsuario, int idMissao) throws SQLException {
        String sql = "DELETE FROM Usuario_missao WHERE Usuario_id_usuario = ? AND Missao_id_missao = ?";

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idMissao);
            return stmt.executeUpdate() > 0;
        }
    }

    private UsuarioMissao montarUsuarioMissao(ResultSet rs) throws SQLException {
        return new UsuarioMissao(
                rs.getInt("Usuario_id_usuario"),
                rs.getInt("Missao_id_missao"),
                rs.getTimestamp("data_conclusao").toLocalDateTime(),
                rs.getInt("pontos_ganhos"));
    }
}