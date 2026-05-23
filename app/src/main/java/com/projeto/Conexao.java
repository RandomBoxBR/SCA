package com.projeto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Conexao {

    // URL de conexão com o arquivo de banco (sera criado na raiz do projeto)
    private static final String URL = "jdbc:sqlite:./SCADB.db";

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
                    data_nascimento TEXT NOT NULL,
                    rg    TEXT,
                    estado_civil   TEXT,
                    celular   TEXT   NOT NULL,
                    email   TEXT,
                    profissao   TEXT,
                    local_trabalho   TEXT,
                    endereco   TEXT   NOT NULL,
                    cidade   TEXT   NOT NULL,
                    estado   TEXT   NOT NULL,
                    cep   TEXT   NOT NULL
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS aluno (
                    id    INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome  TEXT   NOT NULL,
                    cpf   TEXT   UNIQUE NOT NULL,
                    data_nascimento TEXT   NOT NULL,
                    id_responsavel1 INTEGER NOT NULL,
                    id_responsavel2 INTEGER,
                    rg    TEXT,
                    estado_civil   TEXT,
                    celular   TEXT,
                    email   TEXT,
                    endereco   TEXT   NOT NULL,
                    cidade   TEXT   NOT NULL,
                    estado   TEXT   NOT NULL,
                    cep   TEXT   NOT NULL,
                    escola   TEXT,
                    escola_ano   TEXT,
                    escola_fone   TEXT,
                    tem_irmaos   TEXT   NOT NULL,
                    irmaos   TEXT,
                    data_admissao   TEXT   NOT NULL,
                    data_desligamento   TEXT,
                    diagnostico_down   TEXT   NOT NULL,
                    parente_down   TEXT   NOT NULL,
                    grau_parentesco   TEXT,
                    FOREIGN KEY (id_responsavel1) REFERENCES responsavel(id),
                    FOREIGN KEY (id_responsavel2) REFERENCES responsavel(id)
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id    INTEGER PRIMARY KEY AUTOINCREMENT,
                    username   TEXT   UNIQUE NOT NULL,
                    password   TEXT   NOT NULL
                );
            """);

            stmt.execute("""
                INSERT OR IGNORE INTO users (id, username, password) VALUES (1, 'Admin', 'Admin1234');
            """);

            System.out.println("Banco de dados inicializado com sucesso.");

        } catch (SQLException e) {

            System.err.println("Erro ao inicializar banco: " + e.getMessage());

        }

    }

}