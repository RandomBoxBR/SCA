package com.projeto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    // URL de conexão com o arquivo de banco (sera criado na raiz do projeto)
    private static final String URL = "jdbc:sqlite:./alunos.db";

    // Obtém e retorna a conexão com o SQLite
    public static Connection conectar() throws SQLException {

        return DriverManager.getConnection(URL);

    }

}