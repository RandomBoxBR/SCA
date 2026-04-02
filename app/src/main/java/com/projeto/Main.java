package com.projeto;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement()) {

            String sqlCreate = """
                CREATE TABLE IF NOT EXISTS aluno (
                    id    INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome  TEXT   NOT NULL,
                    idade INTEGER
                );
            """;
            stmt.execute(sqlCreate);
            System.out.println("Tabela 'aluno' pronta.");

        } catch (SQLException e) {

            e.printStackTrace();

        }

        SwingUtilities.invokeLater(() -> {

            AlunoDAO dao = new AlunoDAO();

            JFrame frame = new JFrame("Cadastro de Alunos");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new FlowLayout());

            JTextField txtNome = new JTextField(15);
            JTextField txtIdade = new JTextField(3);
            JButton btnSalvar = new JButton("Salvar");
            JTextArea areaListagem = new JTextArea(10, 30);
            areaListagem.setEditable(false);

            btnSalvar.addActionListener(e -> {

                try {

                    String nome = txtNome.getText();
                    int idade = Integer.parseInt(txtIdade.getText());
                    Aluno aluno = new Aluno(nome, idade);
                    dao.inserir(aluno);
                    atualizarListagem(dao, areaListagem);

                } catch (Exception ex) {

                    JOptionPane.showMessageDialog(frame, "Erro: " + ex.getMessage());

                }

            });

            frame.add(new JLabel("Nome: ")); frame.add(txtNome);
            frame.add(new JLabel("Idade: ")); frame.add(txtIdade);
            frame.add(btnSalvar);
            frame.add(new JScrollPane(areaListagem));

            frame.pack();
            frame.setVisible(true);

            atualizarListagem(dao, areaListagem);

        });

    }

    private static void atualizarListagem(AlunoDAO dao, JTextArea area) {

        List<Aluno> alunos = dao.listar();
        StringBuilder sb = new StringBuilder();

        for (Aluno a : alunos) {

            sb.append(a.getId())
                    .append(": ").append(a.getNome())
                    .append(", ").append(a.getIdade())
                    .append(" anos.")
                    .append("\n");

        }

        area.setText(sb.toString());

    }

}
