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
            "id_conclusao, Usuario_id_usuario, Missao_id_missao, data_conclusao, pontos_ganhos, periodo_ref";

    // Create: registra a conclusão e credita os pontos na mesma transação
    public void concluir(UsuarioMissao um) throws SQLException {
        String sqlInsert = "INSERT INTO Usuario_missao "
                + "(Usuario_id_usuario, Missao_id_missao, data_conclusao, pontos_ganhos, periodo_ref) "
                + "VALUES (?, ?, ?, ?, ?)";
        String sqlPontos = "UPDATE Usuario SET pontos_usuario = pontos_usuario + ? "
                + "WHERE id_usuario = ?";

        try (Connection conexao = ConexaoBancoDados.getConnection()) {
            conexao.setAutoCommit(false);

            try (PreparedStatement insere = conexao.prepareStatement(sqlInsert, new String[]{"id_conclusao"});
                 PreparedStatement credita = conexao.prepareStatement(sqlPontos)) {

                insere.setInt(1, um.getIdUsuario());
                insere.setInt(2, um.getIdMissao());
                insere.setTimestamp(3, Timestamp.valueOf(um.getDataConclusao()));
                insere.setInt(4, um.getPontosGanhos());
                insere.setString(5, um.getPeriodoRef());
                insere.executeUpdate();

                try (ResultSet chaves = insere.getGeneratedKeys()) {
                    if (chaves.next()) {
                        um.setId(chaves.getInt(1));
                    }
                }

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
    public UsuarioMissao buscarPorId(int idConclusao) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM Usuario_missao WHERE id_conclusao = ?";

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idConclusao);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? montarUsuarioMissao(rs) : null;
            }
        }
    }

    // A missão já foi concluída neste período?
    public boolean jaConcluiu(int idUsuario, int idMissao, String periodoRef) throws SQLException {
        String sql = "SELECT 1 FROM Usuario_missao "
                + "WHERE Usuario_id_usuario = ? AND Missao_id_missao = ? AND periodo_ref = ?";

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idMissao);
            stmt.setString(3, periodoRef);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<UsuarioMissao> listarPorUsuario(int idUsuario) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM Usuario_missao "
                + "WHERE Usuario_id_usuario = ? ORDER BY data_conclusao DESC, id_conclusao DESC";

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
        String sql = "UPDATE Usuario_missao SET data_conclusao = ?, pontos_ganhos = ?, periodo_ref = ? "
                + "WHERE id_conclusao = ?";

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(um.getDataConclusao()));
            stmt.setInt(2, um.getPontosGanhos());
            stmt.setString(3, um.getPeriodoRef());
            stmt.setInt(4, um.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    // Delete: remove só o registro da conclusão (não estorna pontos: isso seria regra do BO)
    public boolean deletar(int idConclusao) throws SQLException {
        String sql = "DELETE FROM Usuario_missao WHERE id_conclusao = ?";

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idConclusao);
            return stmt.executeUpdate() > 0;
        }
    }

    private UsuarioMissao montarUsuarioMissao(ResultSet rs) throws SQLException {
        return new UsuarioMissao(
                rs.getInt("id_conclusao"),
                rs.getInt("Usuario_id_usuario"),
                rs.getInt("Missao_id_missao"),
                rs.getTimestamp("data_conclusao").toLocalDateTime(),
                rs.getInt("pontos_ganhos"),
                rs.getString("periodo_ref"));
    }
}