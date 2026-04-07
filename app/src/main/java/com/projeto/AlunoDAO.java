package com.projeto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlunoDAO {

    public void inserir(Aluno aluno) {

        String sql = "INSERT INTO aluno(nome, data_nascimento) VALUES (?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, aluno.getNome());
            stmt.setString(2, aluno.getDataNascimento());
            stmt.executeUpdate();
            System.out.println("Aluno inserido com sucesso.");

        } catch (SQLException e) { e.printStackTrace(); }

    }

    public List<Aluno> listar() {

        String sql = "SELECT * FROM aluno";
        List<Aluno> lista = new ArrayList<>();

        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

           while (rs.next()) {

               Aluno a = new Aluno();
               a.setId(rs.getInt("id"));
               a.setNome(rs.getString("nome"));
               a.setDataNascimento(rs.getString("data_nascimento"));
               lista.add(a);

           }

        } catch (SQLException e) { e.printStackTrace(); }

        return lista;

    }

    public void atualizar (Aluno aluno) {

        String sql = "UPDATE aluno SET nome = ?, data_nascimento = ? WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, aluno.getNome());
            stmt.setString(2, aluno.getDataNascimento());
            stmt.setInt(3, aluno.getId());
            stmt.executeUpdate();
            System.out.println("Aluno atualizado com sucesso.");

        } catch (SQLException e) { e.printStackTrace(); }

    }

    public void deletar(int id) {

        String sql = "DELETE FROM aluno WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Aluno deletado com sucesso.");

        } catch (SQLException e) { e.printStackTrace(); }

    }

    public Aluno buscarPorId(int id) {

        String sql = "SELECT * FROM aluno WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                Aluno a = new Aluno(rs.getString("nome"), rs.getString("data_nascimento"));
                a.setId(rs.getInt("id"));
                return a;

            }

        } catch (SQLException e) { e.printStackTrace(); }

        return null;

    }

}