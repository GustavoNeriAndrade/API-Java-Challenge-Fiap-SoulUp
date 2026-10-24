package br.com.fiap.dao;

import br.com.fiap.conexao.ConexaoBancoDados;
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
public class VoucherDAO {

    private static final String COLUNAS =
            "id_voucher, codigo_voucher, valor_credito_voucher, data_emissao, data_validade, "
                    + "status_voucher, nome_operador_transporte, ConversaoPontos_id_conversao";

    // Próximo número da sequence usado no código do voucher (VCH-AAAA-NNNN)
    public int proximoSequencial() throws SQLException {
        String sql = "SELECT seq_codigo_voucher.NEXTVAL FROM dual";

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            rs.next();
            return rs.getInt(1);
        }
    }

    // Create
    public void inserir(Voucher v) throws SQLException {
        String sql = "INSERT INTO Voucher (codigo_voucher, valor_credito_voucher, data_emissao, "
                + "data_validade, status_voucher, nome_operador_transporte, ConversaoPontos_id_conversao) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql, new String[]{"id_voucher"})) {

            stmt.setString(1, v.getCodigo());
            stmt.setBigDecimal(2, v.getValorCredito());
            stmt.setTimestamp(3, Timestamp.valueOf(v.getDataEmissao()));
            stmt.setTimestamp(4, Timestamp.valueOf(v.getDataValidade()));
            stmt.setString(5, v.getStatus());
            stmt.setString(6, v.getOperador());
            stmt.setInt(7, v.getIdConversao());
            stmt.executeUpdate();

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    v.setId(chaves.getInt(1));
                }
            }
        }
    }

    // Read
    public Voucher buscarPorId(int id) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM Voucher WHERE id_voucher = ?";

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? montarVoucher(rs) : null;
            }
        }
    }

    public List<Voucher> listarTodos() throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM Voucher ORDER BY id_voucher";
        List<Voucher> lista = new ArrayList<>();

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(montarVoucher(rs));
            }
        }
        return lista;
    }

    // O voucher não tem FK direta para o usuário: o acesso é via ConversaoPontos
    public List<Voucher> listarPorUsuario(int idUsuario) throws SQLException {
        String sql = "SELECT v.id_voucher, v.codigo_voucher, v.valor_credito_voucher, "
                + "v.data_emissao, v.data_validade, v.status_voucher, "
                + "v.nome_operador_transporte, v.ConversaoPontos_id_conversao "
                + "FROM Voucher v "
                + "JOIN ConversaoPontos c ON c.id_conversao = v.ConversaoPontos_id_conversao "
                + "WHERE c.Usuario_id_usuario = ?";

        List<Voucher> lista = new ArrayList<>();

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(montarVoucher(rs));
                }
            }
        }
        return lista;
    }

    // Update
    public boolean atualizar(Voucher v) throws SQLException {
        String sql = "UPDATE Voucher SET valor_credito_voucher = ?, data_validade = ?, "
                + "status_voucher = ?, nome_operador_transporte = ? WHERE id_voucher = ?";

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setBigDecimal(1, v.getValorCredito());
            stmt.setTimestamp(2, Timestamp.valueOf(v.getDataValidade()));
            stmt.setString(3, v.getStatus());
            stmt.setString(4, v.getOperador());
            stmt.setInt(5, v.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    // Delete
    public boolean deletar(int id) throws SQLException {
        String sql = "DELETE FROM Voucher WHERE id_voucher = ?";

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Voucher montarVoucher(ResultSet rs) throws SQLException {
        return new Voucher(
                rs.getInt("id_voucher"),
                rs.getString("codigo_voucher"),
                rs.getBigDecimal("valor_credito_voucher"),
                rs.getTimestamp("data_emissao").toLocalDateTime(),
                rs.getTimestamp("data_validade").toLocalDateTime(),
                rs.getString("status_voucher"),
                rs.getString("nome_operador_transporte"),
                rs.getInt("ConversaoPontos_id_conversao"));
    }
}