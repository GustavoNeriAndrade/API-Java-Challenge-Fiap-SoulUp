package br.com.fiap.dao;

import br.com.fiap.conexao.ConexaoBancoDados;
import br.com.fiap.model.Usuario;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UsuarioDAO {

    private static final String COLUNAS =
            "id_usuario, nome_usuario, email_usuario, senha_usuario, pontos_usuario, data_cadastro";

    // Create
    public void inserir(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO Usuario "
                + "(nome_usuario, email_usuario, senha_usuario, pontos_usuario, data_cadastro) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql, new String[]{"id_usuario"})) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setInt(4, usuario.getPontos());
            stmt.setTimestamp(5, Timestamp.valueOf(usuario.getDataCadastro()));
            stmt.executeUpdate();

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    usuario.setId(chaves.getInt(1));
                }
            }
        }
    }

    // Read
    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM Usuario WHERE id_usuario = ?";

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? montarUsuario(rs) : null;
            }
        }
    }

    public Usuario buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM Usuario WHERE email_usuario = ?";

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? montarUsuario(rs) : null;
            }
        }
    }

    public List<Usuario> listarTodos() throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM Usuario ORDER BY id_usuario";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                usuarios.add(montarUsuario(rs));
            }
        }
        return usuarios;
    }

    // Update (todos os campos, inclusive pontos)
    public boolean atualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE Usuario SET nome_usuario = ?, email_usuario = ?, "
                + "senha_usuario = ?, pontos_usuario = ? WHERE id_usuario = ?";

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setInt(4, usuario.getPontos());
            stmt.setInt(5, usuario.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    // Update do perfil: só nome e senha. Não toca nos pontos, para não sobrescrever
    // pontos ganhos ou gastos por outra requisição no meio do caminho.
    public boolean atualizarPerfil(Usuario usuario) throws SQLException {
        String sql = "UPDATE Usuario SET nome_usuario = ?, senha_usuario = ? WHERE id_usuario = ?";

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getSenha());
            stmt.setInt(3, usuario.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    // Delete
    public boolean deletar(int id) throws SQLException {
        String sql = "DELETE FROM Usuario WHERE id_usuario = ?";

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // Exclui a conta e tudo que depende dela, na mesma transação
    public boolean deletarComDependencias(int idUsuario) throws SQLException {
        String[] comandos = {
                "DELETE FROM Voucher WHERE ConversaoPontos_id_conversao IN "
                        + "(SELECT id_conversao FROM ConversaoPontos WHERE Usuario_id_usuario = ?)",
                "DELETE FROM ConversaoPontos WHERE Usuario_id_usuario = ?",
                "DELETE FROM ImpactoAmbiental WHERE Usuario_id_usuario = ?",
                "DELETE FROM Usuario_missao WHERE Usuario_id_usuario = ?",
                "DELETE FROM Usuario WHERE id_usuario = ?"
        };

        try (Connection conexao = ConexaoBancoDados.getConnection()) {
            conexao.setAutoCommit(false);

            try {
                int linhasUsuario = 0;
                for (int i = 0; i < comandos.length; i++) {
                    try (PreparedStatement stmt = conexao.prepareStatement(comandos[i])) {
                        stmt.setInt(1, idUsuario);
                        int linhas = stmt.executeUpdate();
                        if (i == comandos.length - 1) {
                            linhasUsuario = linhas;
                        }
                    }
                }
                conexao.commit();
                return linhasUsuario > 0;
            } catch (SQLException e) {
                conexao.rollback();
                throw e;
            }
        }
    }

    // Auxiliar
    private Usuario montarUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id_usuario"),
                rs.getString("nome_usuario"),
                rs.getString("email_usuario"),
                rs.getString("senha_usuario"),
                rs.getInt("pontos_usuario"),
                rs.getTimestamp("data_cadastro").toLocalDateTime()
        );
    }
}