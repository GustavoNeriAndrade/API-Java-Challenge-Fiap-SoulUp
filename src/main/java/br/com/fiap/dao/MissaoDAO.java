package br.com.fiap.dao;

import br.com.fiap.conexao.ConexaoBancoDados;
import br.com.fiap.model.Missao;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class MissaoDAO {

    private static final String COLUNAS =
            "id_missao, titulo_missao, descricao_missao, pontos_recompensa, "
                    + "categoria_missao, ativa, dicas_missao";

    // Create
    public void inserir(Missao missao) throws SQLException {
        String sql = "INSERT INTO Missao (titulo_missao, descricao_missao, pontos_recompensa, "
                + "categoria_missao, ativa, dicas_missao) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql, new String[]{"id_missao"})) {

            stmt.setString(1, missao.getTitulo());
            stmt.setString(2, missao.getDescricao());
            stmt.setInt(3, missao.getPontosRecompensa());
            stmt.setString(4, missao.getCategoria());
            stmt.setString(5, missao.isAtiva() ? "S" : "N");
            stmt.setString(6, dicasParaTexto(missao.getDicas()));
            stmt.executeUpdate();

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    missao.setId(chaves.getInt(1));
                }
            }
        }
    }

    // Read
    public Missao buscarPorId(int id) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM Missao WHERE id_missao = ?";

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? montarMissao(rs) : null;
            }
        }
    }

    public List<Missao> listarTodas() throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM Missao ORDER BY id_missao";
        List<Missao> missoes = new ArrayList<>();

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                missoes.add(montarMissao(rs));
            }
        }
        return missoes;
    }

    // Update
    public boolean atualizar(Missao missao) throws SQLException {
        String sql = "UPDATE Missao SET titulo_missao = ?, descricao_missao = ?, "
                + "pontos_recompensa = ?, categoria_missao = ?, ativa = ?, dicas_missao = ? "
                + "WHERE id_missao = ?";

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, missao.getTitulo());
            stmt.setString(2, missao.getDescricao());
            stmt.setInt(3, missao.getPontosRecompensa());
            stmt.setString(4, missao.getCategoria());
            stmt.setString(5, missao.isAtiva() ? "S" : "N");
            stmt.setString(6, dicasParaTexto(missao.getDicas()));
            stmt.setInt(7, missao.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    // Delete
    public boolean deletar(int id) throws SQLException {
        String sql = "DELETE FROM Missao WHERE id_missao = ?";

        try (Connection conexao = ConexaoBancoDados.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // Auxiliares
    private Missao montarMissao(ResultSet rs) throws SQLException {
        return new Missao(
                rs.getInt("id_missao"),
                rs.getString("titulo_missao"),
                rs.getString("descricao_missao"),
                rs.getInt("pontos_recompensa"),
                rs.getString("categoria_missao"),
                "S".equals(rs.getString("ativa")),
                textoParaDicas(rs.getString("dicas_missao"))
        );
    }

    private String dicasParaTexto(List<String> dicas) {
        return (dicas == null || dicas.isEmpty()) ? null : String.join("|", dicas);
    }

    private List<String> textoParaDicas(String texto) {
        if (texto == null || texto.isBlank()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(texto.split("\\|")));
    }
}