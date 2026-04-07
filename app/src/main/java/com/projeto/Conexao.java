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

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {

            stmt.execute("PRAGMA foreign_keys = ON;");

            // Executa a criação das tabelas
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS responsavel (
                    id    INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome  TEXT   NOT NULL,
                    cpf   TEXT   UNIQUE NOT NULL,
                    data_nascimento TEXT
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS aluno (
                    id    INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome  TEXT   NOT NULL,
                    data_nascimento TEXT,
                    id_responsavel INTEGER,
                    FOREIGN KEY (id_responsavel) REFERENCES responsavel(id)
                );
            """);

            System.out.println("Banco de dados inicializado com sucesso.");

        } catch (SQLException e) {

            System.err.println("Erro ao inicializar banco: " + e.getMessage());

        }

    }

}