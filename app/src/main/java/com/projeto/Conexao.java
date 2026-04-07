package com.projeto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Conexao {

    // URL de conexão com o arquivo de banco (sera criado na raiz do projeto)
    private static final String URL = "jdbc:sqlite:./SCA.db";

    // Obtém e retorna a conexão com o SQLite
    public static Connection conectar() throws SQLException {

        return DriverManager.getConnection(URL);

    }

    public static void inicializarBanco() {

            String sqlResponsavel = """
                CREATE TABLE IF NOT EXISTS responsavel (
                    id    INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome  TEXT   NOT NULL,
                    data_nascimento TEXT
                );
            """;

            String sqlAluno = """
                CREATE TABLE IF NOT EXISTS aluno (
                    id    INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome  TEXT   NOT NULL,
                    data_nascimento TEXT,
                    FOREIGN KEY (id_responsavel) REFERENCES responsavel(id)
                );
            """;

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {

            // Executa a criação das tabelas
            stmt.execute(sqlResponsavel);
            stmt.execute(sqlAluno);

            System.out.println("Tabela 'responsavel' verificada/pronta.");
            System.out.println("Tabela 'aluno' verificada/pronta.");
            System.out.println("Banco de dados inicializado com sucesso.");

        } catch (SQLException e) {

            System.err.println("Erro ao inicializar banco: " + e.getMessage());

        }

    }

}