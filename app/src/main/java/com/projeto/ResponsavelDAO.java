package com.projeto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResponsavelDAO {

    public void inserir(Responsavel responsavel) throws SQLException {

        String sql = "INSERT INTO responsavel(nome, cpf, data_nascimento) VALUES (?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, responsavel.getNome());
            stmt.setString(2, responsavel.getCPF());
            stmt.setString(3, responsavel.getDataNascimento());
            stmt.executeUpdate();
            System.out.println("Responsavel inserido com sucesso.");

        }

    }

    public List<Responsavel> listar() {

        String sql = "SELECT * FROM responsavel";
        List<Responsavel> lista = new ArrayList<>();

        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                Responsavel r = new Responsavel();
                r.setId(rs.getInt("id"));
                r.setNome(rs.getString("nome"));
                r.setCPF(rs.getString("cpf"));
                r.setDataNascimento(rs.getString("data_nascimento"));
                lista.add(r);

            }

        } catch (SQLException e) {

            System.err.println("Erro ao listar responsáveis: " + e.getMessage());

        }

        return lista;

    }

    public void atualizar (Responsavel responsavel) throws SQLException {

        String sql = "UPDATE responsavel SET nome = ?, cpf = ?, data_nascimento = ? WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, responsavel.getNome());
            stmt.setString(2, responsavel.getCPF());
            stmt.setString(3, responsavel.getDataNascimento());
            stmt.setInt(4, responsavel.getId());
            stmt.executeUpdate();
            System.out.println("Responsavel atualizado com sucesso.");

        }

    }

    public void deletar(int id) throws SQLException {

        String sql = "DELETE FROM responsavel WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Responsavel deletado com sucesso.");

        }

    }

    public Responsavel buscarPorId(int id) {

        String sql = "SELECT * FROM responsavel WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                Responsavel r = new Responsavel(
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("data_nascimento")
                );
                r.setId(rs.getInt("id"));

                return r;

            }

        } catch (SQLException e) {

            System.err.println("Erro na busca por id: " + e.getMessage());

        }

        return null;

    }

    public boolean temAlunosVinculados(int idResponsavel) {

        String sql = "SELECT COUNT (*) FROM aluno WHERE id_responsavel1 = ? OR id_responsavel2 = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idResponsavel);
            stmt.setInt(2, idResponsavel);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                return rs.getInt(1) > 0;

            }

        } catch (SQLException e) {

            System.err.println("Erro ao verificar vínculos: " + e.getMessage());
            return true;

        }

        return false;

    }

}
