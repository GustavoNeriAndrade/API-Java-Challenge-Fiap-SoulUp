package br.com.fiap.dao;

import br.com.fiap.conexao.ConexaoBancoDados;
import br.com.fiap.model.ConversaoPontos;
import br.com.fiap.model.Voucher;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ConversaoPontosDAO {

    private static final String COLUNAS =
            "id_conversao, pontos_utilizados, valor_credito, data_conversao, status, Usuario_id_usuario";

    // Debita os pontos, grava a conversão e o voucher na mesma transação.
    // Retorna false (sem alterar nada) se o saldo for insuficiente.
    public boolean converter(ConversaoPontos conversao, Voucher voucher) throws SQLException {
        String sqlDebito = "UPDATE Usuario SET pontos_usuario = pontos_usuario - ? "
                + "WHERE id_usuario = ? AND pontos_usuario >= ?";
        String sqlConversao = "INSERT INTO ConversaoPontos "
                + "(pontos_utilizados, valor_credito, data_conversao, status, Usuario_id_usuario) "
                + "VALUES (?, ?, ?, ?, ?)";
        String sqlVoucher = "INSERT INTO Voucher "
                + "(codigo_voucher, valor_credito_voucher, data_emissao, data_validade, "
                + "status_voucher, nome_operador_transporte, ConversaoPontos_id_conversao) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexao = ConexaoBancoDados.getConnection()) {
            conexao.setAutoCommit(false);

            try (PreparedStatement debito = conexao.prepareStatement(sqlDebito);
                 PreparedStatement insConversao = conexao.prepareStatement(sqlConversao, new String[]{"id_conversao"});
                 PreparedStatement insVoucher = conexao.prepareStatement(sqlVoucher, new String[]{"id_voucher"})) {

                debito.setInt(1, conversao.getPontosUtilizados());
                debito.setInt(2, conversao.getIdUsuario());
                debito.setInt(3, conversao.getPontosUtilizados());
                if (debito.executeUpdate() == 0) {
                    conexao.rollback();
                    return false;
                }

                insConversao.setInt(1, conversao.getPontosUtilizados());
                insConversao.setBigDecimal(2, conversao.getValorCredito());
                insConversao.setTimestamp(3, Timestamp.valueOf(conversao.getDataConversao()));
                insConversao.setString(4, conversao.getStatus());
                insConversao.setInt(5, conversao.getIdUsuario());
                insConversao.executeUpdate();

                try (ResultSet chaves = insConversao.getGeneratedKeys()) {
                    if (chaves.next()) {
                        conversao.setId(chaves.getInt(1));
                    }
                }
                voucher.setIdConversao(conversao.getId());

                insVoucher.setString(1, voucher.getCodigo());
                insVoucher.setBigDecimal(2, voucher.getValorCredito());
                insVoucher.setTimestamp(3, Timestamp.valueOf(voucher.getDataEmissao()));
                insVoucher.setTimestamp(4, Timestamp.valueOf(voucher.getDataValidade()));
                insVoucher.setString(5, voucher.getStatus());
                insVoucher.setString(6, voucher.getOperador());
                insVoucher.setInt(7, voucher.getIdConversao());
                insVoucher.executeUpdate();

                try (ResultSet chaves = insVoucher.getGeneratedKeys()) {
                    if (chaves.next()) {
                        voucher.setId(chaves.getInt(1));
                    }
                }

                conexao.commit();
                return true;
            } catch (SQLException e) {
                conexao.rollback();
                throw e;
            }
        }
    }

    public ConversaoPontos buscarPorId(int id) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM ConversaoPontos WHERE id_conversao = ?";

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? montarConversao(rs) : null;
            }
        }
    }

    public List<ConversaoPontos> listarTodas() throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM ConversaoPontos ORDER BY id_conversao";
        List<ConversaoPontos> lista = new ArrayList<>();

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(montarConversao(rs));
            }
        }
        return lista;
    }

    public List<ConversaoPontos> listarPorUsuario(int idUsuario) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM ConversaoPontos "
                + "WHERE Usuario_id_usuario = ? ORDER BY data_conversao DESC, id_conversao DESC";

        List<ConversaoPontos> lista = new ArrayList<>();

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(montarConversao(rs));
                }
            }
        }
        return lista;
    }

    public boolean atualizar(ConversaoPontos c) throws SQLException {
        String sql = "UPDATE ConversaoPontos SET pontos_utilizados = ?, valor_credito = ?, status = ? "
                + "WHERE id_conversao = ?";

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, c.getPontosUtilizados());
            stmt.setBigDecimal(2, c.getValorCredito());
            stmt.setString(3, c.getStatus());
            stmt.setInt(4, c.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    // Remove o voucher da conversão e depois a própria conversão, na mesma transação
    public boolean deletar(int id) throws SQLException {
        String sqlVoucher = "DELETE FROM Voucher WHERE ConversaoPontos_id_conversao = ?";
        String sqlConversao = "DELETE FROM ConversaoPontos WHERE id_conversao = ?";

        try (Connection conexao = ConexaoBancoDados.getConnection()) {
            conexao.setAutoCommit(false);

            try (PreparedStatement delVoucher = conexao.prepareStatement(sqlVoucher);
                 PreparedStatement delConversao = conexao.prepareStatement(sqlConversao)) {

                delVoucher.setInt(1, id);
                delVoucher.executeUpdate();

                delConversao.setInt(1, id);
                boolean removida = delConversao.executeUpdate() > 0;

                conexao.commit();
                return removida;
            } catch (SQLException e) {
                conexao.rollback();
                throw e;
            }
        }
    }

    private ConversaoPontos montarConversao(ResultSet rs) throws SQLException {
        return new ConversaoPontos(
                rs.getInt("id_conversao"),
                rs.getInt("pontos_utilizados"),
                rs.getBigDecimal("valor_credito"),
                rs.getTimestamp("data_conversao").toLocalDateTime(),
                rs.getString("status"),
                rs.getInt("Usuario_id_usuario"));
    }
}