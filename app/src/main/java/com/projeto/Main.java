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

        Conexao.inicializarBanco();

        SwingUtilities.invokeLater(() -> {

            AlunoDAO dao = new AlunoDAO();

            JFrame frame = new JFrame("SCA - Sistema de Cadastro Asdown");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setPreferredSize(new Dimension(800, 600));

            JTabbedPane menuPrincipal = new JTabbedPane();

            String[] colunas = {"ID", "Nome", "Data de Nascimento"};
            DefaultTableModel modelo = new DefaultTableModel(colunas, 0);
            String[] colunasReduzidas = {"ID", "Nome"};
            DefaultTableModel modeloReduzido = new DefaultTableModel(colunasReduzidas, 0);


            menuPrincipal.addTab("Cadastrar", criarPainelCadastro(dao));
            menuPrincipal.addTab("Listar", criarPainelListagem(dao, modelo));
            menuPrincipal.addTab("Gerenciar", criarPainelGerenciamento(dao, modeloReduzido));

            menuPrincipal.addChangeListener(e -> {
                int aba = menuPrincipal.getSelectedIndex();
                if (aba == 1) {

                    preencherTabela(dao, modelo);

                    System.out.println("Lista atualizada!");

                } else if (aba == 2) {

                    preencherTabela(dao, modeloReduzido);

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
            txtData = new JFormattedTextField(mascara);
            txtData.setColumns(10);

        } catch (Exception e) { e.printStackTrace(); }

        final JFormattedTextField campoDataFinal = txtData;

        JButton btnSalvar = new JButton("Salvar");

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
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.getTableHeader().setResizingAllowed(false);
        tabela.setDefaultEditor(Object.class, null);

        painel.add(new JLabel("Alunos Cadastrados:"), BorderLayout.NORTH);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        return painel;

    }

    private static JPanel criarPainelGerenciamento(AlunoDAO dao, DefaultTableModel modelo) {

        JPanel painelPrincipal = new JPanel(new GridLayout(1, 2, 10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel painelEsquerdo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtId = new JTextField(5);
        txtId.setEditable(false);
        JTextField txtNome = new JTextField(20);

        JFormattedTextField txtData = null;
        try {

            MaskFormatter m = new MaskFormatter("##/##/####");
            m.setPlaceholderCharacter('_');
            txtData = new JFormattedTextField(m);
            txtData.setColumns(10);

        } catch (Exception e)  { e.printStackTrace(); }

        final JFormattedTextField campoDataFinal = txtData;

        JButton btnEditar = new JButton("Salvar Alterações");
        JButton btnExcluir = new JButton("Excluir Aluno");
        btnExcluir.setBackground(new Color(255, 150, 150));

        painelEsquerdo.add(new JLabel("ID:")); painelEsquerdo.add(txtId);
        painelEsquerdo.add(new JLabel("Nome:")); painelEsquerdo.add(txtNome);
        painelEsquerdo.add(new JLabel("Data de Nascimento:")); painelEsquerdo.add(txtData);
        painelEsquerdo.add(btnEditar);
        painelEsquerdo.add(btnExcluir);

        JTable tabela  = new JTable(modelo);
        tabela.setFillsViewportHeight(true);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.getTableHeader().setResizingAllowed(false);
        tabela.setDefaultEditor(Object.class, null);

        tabela.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting() && tabela.getSelectedRow() != -1) {

                int linha = tabela.getSelectedRow();

                int id = Integer.parseInt(tabela.getValueAt(linha, 0).toString());

                Aluno a = dao.buscarPorId(id);
                if (a != null) {

                    txtId.setText(String.valueOf(a.getId()));
                    txtNome.setText(a.getNome());

                    if(a.getDataNascimento() != null) {

                        campoDataFinal.setText(a.getDataNascimento());

                    }

                }

            }

        });

        btnEditar.addActionListener(e -> {

            String idTexto = txtId.getText();

            if (idTexto.isEmpty()) {

                JOptionPane.showMessageDialog(painelPrincipal, "Selecione um aluno na tabela para editar.");
                return;

            }

            String novoNome = txtNome.getText().trim();
            String novaData = ((JFormattedTextField) painelEsquerdo.getComponent(5)).getText().replace("_", "").trim();

            if (novoNome.isEmpty()) {

                JOptionPane.showMessageDialog(painelPrincipal, "O nome não pode estar vazio!");
                return;

            }

            if (novaData.length() < 10 || !isDataValida(novaData)) {

                JOptionPane.showMessageDialog(painelPrincipal, "Data inválida!");
                return;

            }

            Aluno alunoEditado = new Aluno(novoNome, novaData);
            alunoEditado.setId(Integer.parseInt(idTexto));

            dao.atualizar(alunoEditado);

            preencherTabela(dao, modelo);

            JOptionPane.showMessageDialog(painelPrincipal, "Dados atualizados com sucesso!");

        });

        btnExcluir.addActionListener(e -> {

            if (txtId.getText().isEmpty()) {

                JOptionPane.showMessageDialog(null, "Selecione um aluno na tabela para excluir.");
                return;

            }

            Object[] opcoes = {"Confirmar", "Cancelar"};

            int escolha = JOptionPane.showOptionDialog(

                    painelPrincipal,
                    "Tem certeza que deseja excluir o aluno " + txtNome.getText() + "?",
                    "Atenção!",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    opcoes,
                    opcoes[1]

            );

            if (escolha == 0) {

                dao.deletar(Integer.parseInt(txtId.getText()));
                preencherTabela(dao, modelo);
                txtId.setText(""); txtNome.setText(""); campoDataFinal.setValue(null);
                JOptionPane.showMessageDialog(painelPrincipal, "Aluno deletado com sucesso!");

            }

        });

        painelPrincipal.add(painelEsquerdo);
        painelPrincipal.add(new JScrollPane(tabela));

        preencherTabela(dao, modelo);

        return painelPrincipal;

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