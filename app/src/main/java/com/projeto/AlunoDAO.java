package com.projeto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlunoDAO {

    public void inserir(Aluno aluno) throws SQLException{

        String sql = "INSERT INTO aluno(nome, cpf, data_nascimento, id_responsavel1, id_responsavel2, rg, estado_civil, " +
                "celular, email, endereco, cidade, estado, cep, escola, escola_ano, escola_fone, tem_irmaos, irmaos, " +
                "data_admissao, data_desligamento, diagnostico_down, parente_down, grau_parentesco, recebe_beneficio, " +
                "iniciou_terapia, recebeu_atendimento, instituicoes, terapias_atividades) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, aluno.getNome());
            stmt.setString(2, aluno.getCPF());
            stmt.setString(3, aluno.getDataNascimento());
            stmt.setInt(4, aluno.getIdResponsavel1());

            if (aluno.getIdResponsavel2() > 0) {

                stmt.setInt(5, aluno.getIdResponsavel2());

            } else {

                stmt.setNull(5, Types.INTEGER);

            }

            stmt.setString(6, aluno.getRG());
            stmt.setString(7, aluno.getEstCivil());
            stmt.setString(8, aluno.getCelular());
            stmt.setString(9, aluno.getEmail());
            stmt.setString(10, aluno.getEndereco());
            stmt.setString(11, aluno.getCidade());
            stmt.setString(12, aluno.getEstado());
            stmt.setString(13, aluno.getCep());
            stmt.setString(14, aluno.getEscola());
            stmt.setString(15, aluno.getEscAno());
            stmt.setString(16, aluno.getEscFone());
            stmt.setString(17, aluno.getTemIrmaos());
            stmt.setString(18, aluno.getIrmaos());
            stmt.setString(19, aluno.getAdmissao());
            stmt.setString(20, aluno.getDesligamento());
            stmt.setString(21, aluno.getDiagnostico());
            stmt.setString(22, aluno.getParente());
            stmt.setString(23, aluno.getParentesco());
            stmt.setString(24, aluno.getBeneficio());
            stmt.setString(25, aluno.getTerapia());
            stmt.setString(26, aluno.getAtendimento());
            stmt.setString(27, aluno.getInstituicoes());
            stmt.setString(28, aluno.getAtividades());

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
               a.setCPF(rs.getString("cpf"));
               a.setDataNascimento(rs.getString("data_nascimento"));
               a.setIdResponsavel1(rs.getInt("id_responsavel1"));
               a.setIdResponsavel2(rs.getInt("id_responsavel2"));
               a.setRG(rs.getString("rg"));
               a.setEstCivil(rs.getString("estado_civil"));
               a.setCelular(rs.getString("celular"));
               a.setEmail(rs.getString("email"));
               a.setEndereco(rs.getString("endereco"));
               a.setCidade(rs.getString("cidade"));
               a.setEstado(rs.getString("estado"));
               a.setCep(rs.getString("cep"));
               a.setEscola(rs.getString("escola"));
               a.setEscAno(rs.getString("escola_ano"));
               a.setEscFone(rs.getString("escola_fone"));
               a.setTemIrmaos(rs.getString("tem_irmaos"));
               a.setIrmaos(rs.getString("irmaos"));
               a.setAdmissao(rs.getString("data_admissao"));
               a.setDesligamento(rs.getString("data_desligamento"));
               a.setDiagnostico(rs.getString("diagnostico_down"));
               a.setParente(rs.getString("parente_down"));
               a.setParentesco(rs.getString("grau_parentesco"));
               a.setBeneficio(rs.getString("recebe_beneficio"));
               a.setTerapia(rs.getString("iniciou_terapia"));
               a.setAtendimento(rs.getString("recebeu_atendimento"));
               a.setInstituicoes(rs.getString("instituicoes"));
               a.setAtividades(rs.getString("terapias_atividades"));
               lista.add(a);

           }

        } catch (SQLException e) {

            System.err.println("Erro ao listar alunos: " + e.getMessage());

        }

        return lista;

    }

    public void atualizar (Aluno aluno) throws SQLException {

        String sql = "UPDATE aluno SET nome = ?, cpf = ?, data_nascimento = ?, id_responsavel1 = ?, id_responsavel2 = ?, " +
                "rg = ?, estado_civil = ?, celular = ?, email = ?, endereco = ?, cidade = ?, estado = ?, cep = ?, " +
                "escola = ?, escola_ano = ?, escola_fone = ?, tem_irmaos = ?, irmaos = ?, data_admissao = ?, " +
                "data_desligamento = ?, diagnostico_down = ?, parente_down = ?, grau_parentesco = ?, recebe_beneficio = ?, " +
                "iniciou_terapia = ?, recebeu_atendimento = ?, instituicoes = ?, terapias_atividades = ? WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, aluno.getNome());
            stmt.setString(2, aluno.getCPF());
            stmt.setString(3, aluno.getDataNascimento());
            stmt.setInt(4, aluno.getIdResponsavel1());
            stmt.setInt(5, aluno.getIdResponsavel2());
            stmt.setString(6, aluno.getRG());
            stmt.setString(7, aluno.getEstCivil());
            stmt.setString(8, aluno.getCelular());
            stmt.setString(9, aluno.getEmail());
            stmt.setString(10, aluno.getEndereco());
            stmt.setString(11, aluno.getCidade());
            stmt.setString(12, aluno.getEstado());
            stmt.setString(13, aluno.getCep());
            stmt.setString(14, aluno.getEscola());
            stmt.setString(15, aluno.getEscAno());
            stmt.setString(16, aluno.getEscFone());
            stmt.setString(17, aluno.getTemIrmaos());
            stmt.setString(18, aluno.getIrmaos());
            stmt.setString(19, aluno.getAdmissao());
            stmt.setString(20, aluno.getDesligamento());
            stmt.setString(21, aluno.getDiagnostico());
            stmt.setString(22, aluno.getParente());
            stmt.setString(23, aluno.getParentesco());
            stmt.setString(24, aluno.getBeneficio());
            stmt.setString(25, aluno.getTerapia());
            stmt.setString(26, aluno.getAtendimento());
            stmt.setString(27, aluno.getInstituicoes());
            stmt.setString(28, aluno.getAtividades());
            stmt.setInt(29, aluno.getId());
            stmt.executeUpdate();
            System.out.println("Aluno atualizado com sucesso.");

        }

    }

    public void deletar(int id) throws SQLException {

        String sql = "DELETE FROM aluno WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Aluno deletado com sucesso.");

        }

    }

    public Aluno buscarPorId(int id) {

        String sql = "SELECT * FROM aluno WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                Aluno a = new Aluno(rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("data_nascimento"),
                        rs.getInt("id_responsavel1"),
                        rs.getInt("id_responsavel2"),
                        rs.getString("rg"),
                        rs.getString("estado_civil"),
                        rs.getString("celular"),
                        rs.getString("email"),
                        rs.getString("endereco"),
                        rs.getString("cidade"),
                        rs.getString("estado"),
                        rs.getString("cep"),
                        rs.getString("escola"),
                        rs.getString("escola_ano"),
                        rs.getString("escola_fone"),
                        rs.getString("tem_irmaos"),
                        rs.getString("irmaos"),
                        rs.getString("data_admissao"),
                        rs.getString("data_desligamento"),
                        rs.getString("diagnostico_down"),
                        rs.getString("parente_down"),
                        rs.getString("grau_parentesco"),
                        rs.getString("recebe_beneficio"),
                        rs.getString("iniciou_terapia"),
                        rs.getString("recebeu_atendimento"),
                        rs.getString("instituicoes"),
                        rs.getString("terapias_atividades")
                );
                a.setId(rs.getInt("id"));
                return a;

            }

        } catch (SQLException e) {

            System.err.println("Erro na busca por id: " + e.getMessage());

        }

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

        } catch (SQLException e) {

            System.err.println("Erro na busca de alunos por responsável: " + e.getMessage());

        }

        return nomes;

    }

}