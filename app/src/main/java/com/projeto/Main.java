package com.projeto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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

            JFrame frame = new JFrame("SCA - Sistema de Cadastro Asdown");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setPreferredSize(new Dimension(800, 600));

            JTabbedPane menuPrincipal = new JTabbedPane();

            String[] colunas = {"ID", "Nome", "Data de Nascimento"};
            DefaultTableModel modelo = new DefaultTableModel(colunas, 0);

            menuPrincipal.addTab("Cadastrar", criarPainelCadastro(dao));
            menuPrincipal.addTab("Listar", criarPainelListagem(dao, modelo));

            menuPrincipal.addChangeListener(e -> {

                if (menuPrincipal.getSelectedIndex() == 1) {

                    preencherTabela(dao, modelo);
                    System.out.println("Lista atualizada!");

                }

            });

            frame.add(menuPrincipal);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

        });

    }

    private static JPanel criarPainelCadastro(AlunoDAO dao) {

        JPanel painel = new JPanel(new FlowLayout());

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

        final JFormattedTextField campoDataFinal = txtData;

        btnSalvar.addActionListener(e -> {

            try {

                String nome = txtNome.getText();
                String dataNasc = campoDataFinal.getText().replace("_", "").trim();

                if(nome.isEmpty()) {
                    JOptionPane.showMessageDialog(painel, "Preencha o nome!");
                    return;
                }

                if(dataNasc.length() < 10) {
                    JOptionPane.showMessageDialog(painel, "Preencha a data de nascimento completa!");
                    return;
                }

                if(!isDataValida(dataNasc)) {
                    JOptionPane.showMessageDialog(painel, "Data inserida inválida!");
                    return;
                }

                try {

                    Aluno aluno = new Aluno(nome, dataNasc);
                    dao.inserir(aluno);

                    txtNome.setText("");
                    campoDataFinal.setValue(null);

                    JOptionPane.showMessageDialog(painel, "Aluno salvo com sucesso!");

                } catch (Exception ex) {

                    JOptionPane.showMessageDialog(painel, "Erro ao salvar:" + ex.getMessage());

                }

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(painel, "Erro: " + ex.getMessage());

            }

        });

        painel.add(new JLabel("Nome: ")); painel.add(txtNome);
        painel.add(new JLabel("Nascimento: ")); painel.add(txtData);
        painel.add(btnSalvar);

        return painel;

    }

    private static JPanel criarPainelListagem(AlunoDAO dao, DefaultTableModel modelo) {

        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTable tabela = new JTable(modelo);
        tabela.setFillsViewportHeight(true);

        tabela.setDefaultEditor(Object.class, null);

        painel.add(new JLabel("Alunos Cadastrados:"), BorderLayout.NORTH);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        return painel;

    }

    private static void preencherTabela(AlunoDAO dao, DefaultTableModel modelo) {

        modelo.setRowCount(0);

        List<Aluno> alunos = dao.listar();

        for (Aluno a : alunos) {

            Object[] linha = { a.getId(), a.getNome(), a.getDataNascimento() };
            modelo.addRow(linha);

        }

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