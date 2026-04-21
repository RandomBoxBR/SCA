package com.projeto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlunoDAO {

    public void inserir(Aluno aluno) throws SQLException{

        String sql = "INSERT INTO aluno(nome, data_nascimento, id_responsavel1, id_responsavel2) VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, aluno.getNome());
            stmt.setString(2, aluno.getDataNascimento());
            stmt.setInt(3, aluno.getIdResponsavel1());

            if (aluno.getIdResponsavel2() > 0) {

                stmt.setInt(4, aluno.getIdResponsavel2());

            } else {

                stmt.setNull(4, Types.INTEGER);

            }


            stmt.executeUpdate();
            System.out.println("Aluno inserido com sucesso.");

        }

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
               a.setIdResponsavel1(rs.getInt("id_responsavel1"));
               a.setIdResponsavel2(rs.getInt("id_responsavel2"));
               lista.add(a);

           }

        } catch (SQLException e) { e.printStackTrace(); }

        return lista;

    }

    public void atualizar (Aluno aluno) {

        String sql = "UPDATE aluno SET nome = ?, data_nascimento = ?, id_responsavel1 = ?, id_responsavel2 = ? WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, aluno.getNome());
            stmt.setString(2, aluno.getDataNascimento());
            stmt.setInt(3, aluno.getIdResponsavel1());
            stmt.setInt(4, aluno.getIdResponsavel2());
            stmt.setInt(5, aluno.getId());
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

                Aluno a = new Aluno(rs.getString("nome"),
                        rs.getString("data_nascimento"),
                        rs.getInt("id_responsavel1"),
                        rs.getInt("id_responsavel2"));
                a.setId(rs.getInt("id"));
                return a;

            }

        } catch (SQLException e) { e.printStackTrace(); }

        return null;

    }

    public List<String> buscarNomesAlunosPorResponsavel(int idResp) {

        List<String> nomes = new ArrayList<>();
        String sql = "SELECT nome FROM aluno WHERE id_responsavel1 = ? OR id_responsavel2 = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idResp);
            stmt.setInt(2, idResp);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) { nomes.add(rs.getString("nome")); }

        } catch (SQLException e) { e.printStackTrace(); }

        return nomes;

    }

}