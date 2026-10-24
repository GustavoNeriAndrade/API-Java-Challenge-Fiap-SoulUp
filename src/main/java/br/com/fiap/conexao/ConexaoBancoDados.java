package br.com.fiap.conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBancoDados {

    private static final String SERVER = env("DB_SERVER", "oracle.fiap.com.br");
    private static final String PORT   = env("DB_PORT", "1521");
    private static final String SID    = env("DB_SID", "orcl");
    private static final String USER   = env("DB_USER", null);
    private static final String PASSWD = env("DB_PASSWORD", null);

    private static final String URL =
            "jdbc:oracle:thin:@" + SERVER + ":" + PORT + ":" + SID;

    private ConexaoBancoDados() {
    }

    private static String env(String nome, String padrao) {
        String valor = System.getenv(nome);
        if (valor == null || valor.isBlank()) {
            if (padrao == null) {
                throw new IllegalStateException("Variável de ambiente não definida: " + nome);
            }
            return padrao;
        }
        return valor;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWD);
    }

    public static void closeConnection(Connection conexao) {
        if (conexao != null) {
            try {
                conexao.close();
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao fechar a conexão com o banco de dados.", e);
            }
        }
    }
}