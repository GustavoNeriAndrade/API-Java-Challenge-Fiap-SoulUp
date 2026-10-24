package br.com.fiap.dao;

import br.com.fiap.conexao.ConexaoBancoDados;
import br.com.fiap.model.Contato;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ContatoDAO {

    private static final String COLUNAS =
            "id_contato, nome_contato, email_contato, mensagem_contato, data_envio";

    // Create
    public void inserir(Contato contato) throws SQLException {
        String sql = "INSERT INTO Contato (nome_contato, email_contato, mensagem_contato, data_envio) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql, new String[]{"id_contato"})) {

            stmt.setString(1, contato.getNome());
            stmt.setString(2, contato.getEmail());
            stmt.setString(3, contato.getMensagem());
            stmt.setTimestamp(4, Timestamp.valueOf(contato.getDataEnvio()));
            stmt.executeUpdate();

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    contato.setId(chaves.getInt(1));
                }
            }
        }
    }

    // Read
    public Contato buscarPorId(int id) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM Contato WHERE id_contato = ?";

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? montarContato(rs) : null;
            }
        }
    }

    public List<Contato> listarTodos() throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM Contato ORDER BY data_envio DESC, id_contato DESC";
        List<Contato> lista = new ArrayList<>();

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(montarContato(rs));
            }
        }
        return lista;
    }

    // Update
    public boolean atualizar(Contato contato) throws SQLException {
        String sql = "UPDATE Contato SET nome_contato = ?, email_contato = ?, mensagem_contato = ? "
                + "WHERE id_contato = ?";

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, contato.getNome());
            stmt.setString(2, contato.getEmail());
            stmt.setString(3, contato.getMensagem());
            stmt.setInt(4, contato.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    // Delete
    public boolean deletar(int id) throws SQLException {
        String sql = "DELETE FROM Contato WHERE id_contato = ?";

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Contato montarContato(ResultSet rs) throws SQLException {
        return new Contato(
                rs.getInt("id_contato"),
                rs.getString("nome_contato"),
                rs.getString("email_contato"),
                rs.getString("mensagem_contato"),
                rs.getTimestamp("data_envio").toLocalDateTime());
    }
}