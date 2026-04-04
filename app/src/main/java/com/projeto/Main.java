package com.projeto;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;

public class Main {

    public static void main(String[] args) {

        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement()) {

            String sqlCreate = """
                CREATE TABLE IF NOT EXISTS aluno (
                    id    INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome  TEXT   NOT NULL,
                    data_nascimento TEXT
                );
            """;
            stmt.execute(sqlCreate);
            System.out.println("Tabela 'aluno' verificada/pronta.");

        } catch (SQLException e) {

            e.printStackTrace();

        }

        SwingUtilities.invokeLater(() -> {

            AlunoDAO dao = new AlunoDAO();

            JFrame frame = new JFrame("SCA - Cadastro de Alunos");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new FlowLayout());

            JTextField txtNome = new JTextField(15);

            JFormattedTextField txtData = null;
            try {

                MaskFormatter mascara = new MaskFormatter("##/##/####");
                mascara.setPlaceholderCharacter('_');
                mascara.setAllowsInvalid(false);
                mascara.setOverwriteMode(true);
                txtData = new JFormattedTextField(mascara);
                txtData.setColumns(8);

            } catch (Exception e) {

                e.printStackTrace();

            }

            JButton btnSalvar = new JButton("Salvar");
            JTextArea areaListagem = new JTextArea(10, 35);
            areaListagem.setEditable(false);

            final JFormattedTextField campoDataFinal = txtData;
            btnSalvar.addActionListener(e -> {

                try {

                    String nome = txtNome.getText();
                    String dataNasc = campoDataFinal.getText().replace("_", "").trim();

                    if(nome.isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "Preencha o nome!");
                        return;
                    }

                    if(dataNasc.length() < 10) {
                        JOptionPane.showMessageDialog(frame, "Preencha a data de nascimento completa!");
                        return;
                    }

                    if(!isDataValida(dataNasc)) {
                        JOptionPane.showMessageDialog(frame, "Data inserida inválida! Verifique o dia e o mês!");
                        return;
                    }

                    try {

                        Aluno aluno = new Aluno(nome, dataNasc);
                        dao.inserir(aluno);

                        txtNome.setText("");
                        campoDataFinal.setValue(null);

                        atualizarListagem(dao, areaListagem);
                        JOptionPane.showMessageDialog(frame, "Aluno salvo com sucesso!");

                    } catch (Exception ex) {

                        JOptionPane.showMessageDialog(frame, "Erro ao salvar:" + ex.getMessage());

                    }

                } catch (Exception ex) {

                    JOptionPane.showMessageDialog(frame, "Erro: " + ex.getMessage());

                }

            });

            frame.add(new JLabel("Nome: ")); frame.add(txtNome);
            frame.add(new JLabel("Nascimento: ")); frame.add(txtData);
            frame.add(btnSalvar);
            frame.add(new JScrollPane(areaListagem));

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            atualizarListagem(dao, areaListagem);

        });

    }

    private static void atualizarListagem(AlunoDAO dao, JTextArea area) {

        List<Aluno> alunos = dao.listar();
        StringBuilder sb = new StringBuilder();
        sb.append("ID | NOME | DATA NASC.\n");
        sb.append("---------------------------------\n");

        for (Aluno a : alunos) {

            sb.append(a.getId())
                    .append(" - ").append(a.getNome())
                    .append(", ").append(a.getDataNascimento())
                    .append("\n");

        }

        area.setText(sb.toString());

    }

    private static boolean isDataValida (String dataStr) {
        try {

            String dataLimpa = dataStr.trim();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu")
                .withResolverStyle(ResolverStyle.STRICT)
                    .withLocale(Locale.forLanguageTag("pt-BR"));

            LocalDate.parse(dataLimpa, formatter);

            if (LocalDate.parse(dataLimpa, formatter).isAfter(LocalDate.now())) {

                return false;

            }

            return true;

        } catch (DateTimeParseException e) {

            return false;

        }

    }

}
