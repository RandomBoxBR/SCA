package com.projeto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;

public class Main {

    private static JComboBox<ResponsavelComboItem> comboCadResp1, comboCadResp2;
    private static JComboBox<ResponsavelComboItem> comboEditResp1, comboEditResp2;

    public static void main(String[] args) {

        Conexao.inicializarBanco();

        SwingUtilities.invokeLater(() -> {

            AlunoDAO alunoDao = new AlunoDAO();
            ResponsavelDAO respDao = new ResponsavelDAO();

            JFrame frame = new JFrame("SCA - Sistema de Cadastro Asdown");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setPreferredSize(new Dimension(800, 600));

            JTabbedPane menuAbas = new JTabbedPane();

            String[] colunasAl = {"Nº", "Nome", "CPF", "Data de Nascimento", "1º Responsável", "2º Responsável"};
            DefaultTableModel modeloAl = new DefaultTableModel(colunasAl, 0);
            String[] colunasResp = {"Nº", "Nome", "CPF", "Data de Nascimento", "Alunos Vinculados"};
            DefaultTableModel modeloResp = new DefaultTableModel(colunasResp, 0);
            String[] colunasReduzidas = {"ID", "Nome"};
            DefaultTableModel modeloAlReduzido = new DefaultTableModel(colunasReduzidas, 0);
            DefaultTableModel modeloRespReduzido = new DefaultTableModel(colunasReduzidas, 0);


            menuAbas.addTab("Cadastrar", criarPainelCadastro(alunoDao, respDao));
            menuAbas.addTab("Listar", criarPainelListagem(modeloAl, modeloResp));
            menuAbas.addTab("Editar/Excluir", criarPainelEditar(alunoDao, respDao, modeloAlReduzido, modeloRespReduzido));
            menuAbas.addTab("Relatório", criarPainelRelatorio(alunoDao, respDao));

            menuAbas.addChangeListener(e -> {
                int aba = menuAbas.getSelectedIndex();

                if (aba == 0) {

                    if (comboCadResp1 != null && comboCadResp2 != null) {

                        atualizarCombosResponsaveis(respDao, comboCadResp1, comboCadResp2);
                        System.out.println("Combos de responsáveis atualizados!");

                    }

                } else if (aba == 1) {

                    preencherTabAluno(alunoDao,respDao, modeloAl);
                    preencherTabResp(respDao, alunoDao, modeloResp);

                    System.out.println("Tabela atualizada!");

                } else if (aba == 2) {

                    if (comboEditResp1 != null && comboEditResp2 != null) {

                        atualizarCombosResponsaveis(respDao, comboEditResp1, comboEditResp2);
                        System.out.println("Combos de responsáveis atualizados!");

                    }

                    preencherTabAlunoReduzida(alunoDao, modeloAlReduzido);
                    preencherTabRespReduzida(respDao, modeloRespReduzido);

                    System.out.println("Tabela atualizada!");

                }

            });

            frame.add(menuAbas);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

        });

    }

    private static JPanel criarPainelCadastro(AlunoDAO alunoDao, ResponsavelDAO respDao) {

        JPanel painelPrincipal = new JPanel(new BorderLayout());

        JPanel painelSeletor = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] tipos = {"Aluno", "Responsável"};
        JComboBox<String> comboTipo = new JComboBox<>(tipos);
        comboTipo.setBackground(Color.WHITE);
        painelSeletor.add(new JLabel("O que deseja cadastrar?"));
        painelSeletor.add(comboTipo);

        JPanel containerCards = new JPanel(new CardLayout());

        JPanel formAluno = criarFormularioAl(alunoDao, respDao);
        JPanel formResponsavel = criarFormularioResp(respDao);

        containerCards.add(formAluno, "Aluno");
        containerCards.add(formResponsavel, "Responsável");

        comboTipo.addActionListener(e -> {

            CardLayout cl = (CardLayout) (containerCards.getLayout());
            cl.show(containerCards, (String) comboTipo.getSelectedItem());

            String selecao = (String) comboTipo.getSelectedItem();
            cl.show(containerCards, selecao);

            if (selecao.equals("Aluno")) {

                atualizarCombosResponsaveis(respDao, comboCadResp1, comboCadResp2);
                System.out.println("Combos de responsáveis atualizados!");

            }

            containerCards.revalidate();
            containerCards.repaint();

        });

        painelPrincipal.add(painelSeletor, BorderLayout.NORTH);
        painelPrincipal.add(containerCards, BorderLayout.CENTER);

        return painelPrincipal;

    }

    private static JPanel criarFormularioAl(AlunoDAO alunoDao, ResponsavelDAO respDao) {

        JPanel painel = new JPanel(new FlowLayout());

        JTextField txtNome = new JTextField(20);
        JFormattedTextField txtCPF = null;
        JFormattedTextField txtData = null;

        try {

            MaskFormatter mascara = new MaskFormatter("###.###.###-##");
            mascara.setPlaceholderCharacter('_');
            txtCPF = new JFormattedTextField(mascara);
            txtCPF.setColumns(9);

        } catch (Exception e) { e.printStackTrace(); }

        try {

            MaskFormatter mascara = new MaskFormatter("##/##/####");
            mascara.setPlaceholderCharacter('_');
            txtData = new JFormattedTextField(mascara);
            txtData.setColumns(6);

        } catch (Exception e) { e.printStackTrace(); }

        final JFormattedTextField txtCPFFinal = txtCPF;
        final JFormattedTextField txtDataFinal = txtData;

        comboCadResp1 = new JComboBox<>();
        comboCadResp1.setBackground(Color.WHITE);
        comboCadResp2 = new JComboBox<>();
        comboCadResp2.setBackground(Color.WHITE);
        comboCadResp2.setName("Opcional");

        atualizarCombosResponsaveis(respDao, comboCadResp1, comboCadResp2);

        JButton btnSalvar = new JButton("Salvar");

        btnSalvar.addActionListener(e -> {

            String nome = txtNome.getText();
            String cpf = txtCPFFinal.getText().replace("_", "").trim();
            String dataNasc = txtDataFinal.getText().replace("_", "").trim();

            if(nome.isEmpty()) {

                JOptionPane.showMessageDialog(painel, "Preencha o nome!");
                return;

            }

            if(cpf.length() < 14) {

                JOptionPane.showMessageDialog(painel, "Preencha o CPF completo!");
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

            ResponsavelComboItem resp1 = (ResponsavelComboItem) comboCadResp1.getSelectedItem();
            ResponsavelComboItem resp2 = (ResponsavelComboItem) comboCadResp2.getSelectedItem();

            if (resp1 == null) {

                JOptionPane.showMessageDialog(painel, "Todo aluno deve ter o primeiro responsável!");
                return;

            }

            int id1 = resp1.getId();
            int id2 = (resp2 != null) ? resp2.getId() : 0;

            if (id1 != 0 && id1 == id2) {

                JOptionPane.showMessageDialog(null, "O 1º e o 2º responsável não podem ser a mesma pessoa!");
                return;

            }

            try {

                Aluno aluno = new Aluno(nome, cpf, dataNasc, id1, id2);
                alunoDao.inserir(aluno);

                limparCamposAluno(txtNome, txtCPFFinal, txtDataFinal, comboCadResp1, comboCadResp2, null);

                JOptionPane.showMessageDialog(painel, "Aluno salvo com sucesso!");

            } catch (SQLException ex) {

                if (ex.getMessage().contains("UNIQUE constraint failed: aluno.cpf")) {

                    JOptionPane.showMessageDialog(painel,
                            "Já existe alguém cadastrado com este CPF!",
                            "CPF Duplicado",
                            JOptionPane.ERROR_MESSAGE);

                } else {

                    JOptionPane.showMessageDialog(painel, "Erro no banco de dados: " + ex.getMessage());

                }

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(painel, "Erro inesperado: " + ex.getMessage());

            }

        });

        painel.add(new JLabel("Nome: ")); painel.add(txtNome);
        painel.add(new JLabel("CPF: ")); painel.add(txtCPFFinal);
        painel.add(new JLabel("Nascimento: ")); painel.add(txtDataFinal);
        painel.add(new JLabel("Resp. 1:")); painel.add(comboCadResp1);
        painel.add(new JLabel("Resp. 2:")); painel.add(comboCadResp2);
        painel.add(btnSalvar);

        return painel;

    }

    private static JPanel criarFormularioResp(ResponsavelDAO respDao) {

        JPanel painel = new JPanel(new FlowLayout());

        JTextField txtNome = new JTextField(20);
        JFormattedTextField txtCPF = null;
        JFormattedTextField txtData = null;

        try {

            MaskFormatter mascara = new MaskFormatter("###.###.###-##");
            mascara.setPlaceholderCharacter('_');
            txtCPF = new JFormattedTextField(mascara);
            txtCPF.setColumns(9);

        } catch (Exception e) { e.printStackTrace(); }

        try {

            MaskFormatter mascara = new MaskFormatter("##/##/####");
            mascara.setPlaceholderCharacter('_');
            txtData = new JFormattedTextField(mascara);
            txtData.setColumns(6);

        } catch (Exception e) { e.printStackTrace(); }

        final JFormattedTextField txtCPFFinal = txtCPF;
        final JFormattedTextField txtDataFinal = txtData;

        JButton btnSalvar = new JButton("Salvar");

        btnSalvar.addActionListener(e -> {

            String nome = txtNome.getText();
            String cpf = txtCPFFinal.getText().replace("_", "").trim();
            String dataNasc = txtDataFinal.getText().replace("_", "").trim();

            if(nome.isEmpty()) {

                JOptionPane.showMessageDialog(painel, "Preencha o nome!");
                return;

            }

            if(cpf.length() < 14) {

                JOptionPane.showMessageDialog(painel, "Preencha o CPF completo!");
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

                Responsavel resp = new Responsavel(nome, cpf, dataNasc);
                respDao.inserir(resp);

                limparCamposResponsavel(txtNome, txtCPFFinal, txtDataFinal, null);

                JOptionPane.showMessageDialog(painel, "Responsável salvo com sucesso!");

            } catch (SQLException ex) {

                if (ex.getMessage().contains("UNIQUE constraint failed: responsavel.cpf")) {

                    JOptionPane.showMessageDialog(painel,
                            "Já existe alguém cadastrado com este CPF!",
                            "CPF Duplicado",
                            JOptionPane.ERROR_MESSAGE);

                } else {

                    JOptionPane.showMessageDialog(painel, "Erro no banco de dados: " + ex.getMessage());

                }

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(painel, "Erro inesperado: " + ex.getMessage());

            }

        });

        painel.add(new JLabel("Nome: ")); painel.add(txtNome);
        painel.add(new JLabel("CPF: ")); painel.add(txtCPFFinal);
        painel.add(new JLabel("Nascimento: ")); painel.add(txtDataFinal);
        painel.add(btnSalvar);

        return painel;

    }

    private static JPanel criarPainelListagem(DefaultTableModel modeloAl, DefaultTableModel modeloResp) {

        JPanel painelPrincipal = new JPanel(new BorderLayout());

        JPanel painelSeletor = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] tipos = {"Alunos", "Responsáveis"};
        JComboBox<String> comboTipo = new JComboBox<>(tipos);
        comboTipo.setBackground(Color.WHITE);
        painelSeletor.add(new JLabel("Qual lista deseja ver?"));
        painelSeletor.add(comboTipo);

        JPanel containerCards = new JPanel(new CardLayout());

        JPanel listAluno = criarAlListagem(modeloAl);
        JPanel listResp = criarRespListagem(modeloResp);

        containerCards.add(listAluno, "Alunos");
        containerCards.add(listResp, "Responsáveis");

        comboTipo.addActionListener(e -> {

            CardLayout cl = (CardLayout) (containerCards.getLayout());
            cl.show(containerCards, (String) comboTipo.getSelectedItem());

            String selecao = (String) comboTipo.getSelectedItem();
            cl.show(containerCards, selecao);

            containerCards.revalidate();
            containerCards.repaint();

        });

        painelPrincipal.add(painelSeletor, BorderLayout.NORTH);
        painelPrincipal.add(containerCards, BorderLayout.CENTER);

        return painelPrincipal;

    }

    private static JPanel criarAlListagem(DefaultTableModel modeloAl) {

        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTable tabela = new JTable(modeloAl);
        tabela.setFillsViewportHeight(true);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.getTableHeader().setResizingAllowed(false);
        tabela.setDefaultEditor(Object.class, null);

        painel.add(new JLabel("Alunos Cadastrados:"), BorderLayout.NORTH);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        return painel;

    }

    private static JPanel criarRespListagem(DefaultTableModel modeloResp) {

        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTable tabela = new JTable(modeloResp);
        tabela.setFillsViewportHeight(true);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.getTableHeader().setResizingAllowed(false);
        tabela.setDefaultEditor(Object.class, null);

        painel.add(new JLabel("Responsáveis Cadastrados:"), BorderLayout.NORTH);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        return painel;

    }

    private static JPanel criarPainelEditar(AlunoDAO alunoDao, ResponsavelDAO respDao, DefaultTableModel modeloAlReduzido, DefaultTableModel modeloRespReduzido) {

        JPanel painelPrincipal = new JPanel(new BorderLayout());

        JPanel painelSeletor = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] tipos = {"Alunos", "Responsáveis"};
        JComboBox<String> comboTipo = new JComboBox<>(tipos);
        comboTipo.setBackground(Color.WHITE);
        painelSeletor.add(new JLabel("O que você deseja editar?"));
        painelSeletor.add(comboTipo);

        JPanel containerCards = new JPanel(new CardLayout());

        JPanel listAluno = criarAlEditar(alunoDao, respDao, modeloAlReduzido);
        JPanel listResp = criarRespEditar(respDao, modeloRespReduzido);

        containerCards.add(listAluno, "Alunos");
        containerCards.add(listResp, "Responsáveis");

        comboTipo.addActionListener(e -> {

            CardLayout cl = (CardLayout) (containerCards.getLayout());
            cl.show(containerCards, (String) comboTipo.getSelectedItem());

            String selecao = (String) comboTipo.getSelectedItem();
            cl.show(containerCards, selecao);

            if (selecao.equals("Alunos")) {

                preencherTabAlunoReduzida(alunoDao, modeloAlReduzido);
                atualizarCombosResponsaveis(respDao, comboEditResp1, comboEditResp2);
                System.out.println("Combos de responsáveis atualizados!");


            }else {

                preencherTabRespReduzida(respDao, modeloRespReduzido);

            }

            containerCards.revalidate();
            containerCards.repaint();

        });

        painelPrincipal.add(painelSeletor, BorderLayout.NORTH);
        painelPrincipal.add(containerCards, BorderLayout.CENTER);

        return painelPrincipal;

    }

    private static JPanel criarAlEditar(AlunoDAO alunoDao, ResponsavelDAO respDao, DefaultTableModel modeloReduzido) {

        JPanel painelPrincipal = new JPanel(new GridLayout(1, 2, 10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel painelEditor = new JPanel(new FlowLayout(FlowLayout.LEFT));

        comboEditResp1 = new JComboBox<>();
        comboEditResp1.setBackground(Color.WHITE);
        comboEditResp2 = new JComboBox<>();
        comboEditResp2.setBackground(Color.WHITE);
        comboEditResp2.setName("Opcional");

        atualizarCombosResponsaveis(respDao, comboEditResp1, comboEditResp2);

        JTextField txtId = new JTextField(2);
        txtId.setEditable(false);
        JTextField txtNome = new JTextField(20);

        JFormattedTextField txtCPF = null;
        JFormattedTextField txtData = null;

        try {

            MaskFormatter mascara = new MaskFormatter("###.###.###-##");
            mascara.setPlaceholderCharacter('_');
            txtCPF = new JFormattedTextField(mascara);
            txtCPF.setColumns(9);

        } catch (Exception e) { e.printStackTrace(); }

        try {

            MaskFormatter m = new MaskFormatter("##/##/####");
            m.setPlaceholderCharacter('_');
            txtData = new JFormattedTextField(m);
            txtData.setColumns(6);

        } catch (Exception e)  { e.printStackTrace(); }

        final JFormattedTextField txtCPFFinal = txtCPF;
        final JFormattedTextField txtDataFinal = txtData;

        JButton btnEditar = new JButton("Salvar Alterações");
        JButton btnExcluir = new JButton("Excluir Aluno");
        btnExcluir.setBackground(new Color(255, 150, 150));

        painelEditor.add(new JLabel("ID:")); painelEditor.add(txtId);
        painelEditor.add(new JLabel("Nome:")); painelEditor.add(txtNome);
        painelEditor.add(new JLabel("CPF:")); painelEditor.add(txtCPFFinal);
        painelEditor.add(new JLabel("Data de Nascimento:")); painelEditor.add(txtDataFinal);
        painelEditor.add(new JLabel("Resp. 1")); painelEditor.add(comboEditResp1);
        painelEditor.add(new JLabel("Resp. 2")); painelEditor.add(comboEditResp2);
        painelEditor.add(btnEditar);
        painelEditor.add(btnExcluir);

        JTable tabela  = new JTable(modeloReduzido);
        tabela.setFillsViewportHeight(true);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.getTableHeader().setResizingAllowed(false);
        tabela.setDefaultEditor(Object.class, null);

        tabela.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting() && tabela.getSelectedRow() != -1) {

                int linha = tabela.getSelectedRow();

                int id = Integer.parseInt(tabela.getValueAt(linha, 0).toString());

                Aluno a = alunoDao.buscarPorId(id);
                if (a != null) {

                    txtId.setText(String.valueOf(a.getId()));
                    txtNome.setText(a.getNome());
                    txtCPFFinal.setText(a.getCPF());
                    txtDataFinal.setText(a.getDataNascimento());
                    selecionarNoCombo(comboEditResp1, a.getIdResponsavel1());
                    selecionarNoCombo(comboEditResp2, a.getIdResponsavel2());

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
            String novoCPF = txtCPFFinal.getText().replace("_", "").trim();
            String novaData = txtDataFinal.getText().replace("_", "").trim();

            if (novoNome.isEmpty()) {

                JOptionPane.showMessageDialog(painelPrincipal, "O nome não pode estar vazio!");
                return;

            }

            if (novoCPF.length() < 14) {

                JOptionPane.showMessageDialog(painelPrincipal, "Cpf inválido!");
                return;

            }

            if (novaData.length() < 10 || !isDataValida(novaData)) {

                JOptionPane.showMessageDialog(painelPrincipal, "Data inválida!");
                return;

            }

            int novoId1 = ((ResponsavelComboItem) comboEditResp1.getSelectedItem()).getId();
            int novoId2 = ((ResponsavelComboItem) comboEditResp2.getSelectedItem()).getId();

            if (novoId1 != 0 && novoId1 == novoId2) {

                JOptionPane.showMessageDialog(null, "O 1º e o 2º responsável não podem ser a mesma pessoa!");
                return;

            }

            Aluno alunoEditado = new Aluno(novoNome, novoCPF, novaData, novoId1, novoId2);
            alunoEditado.setId(Integer.parseInt(idTexto));

            try {

                alunoDao.atualizar(alunoEditado);

                preencherTabAlunoReduzida(alunoDao, modeloReduzido);

                JOptionPane.showMessageDialog(painelPrincipal, "Dados atualizados com sucesso!");

            } catch (SQLException ex) {

                if (ex.getMessage().contains("UNIQUE constraint failed: aluno.cpf")) {

                    JOptionPane.showMessageDialog(painelPrincipal,
                            "Já existe alguém cadastrado com este CPF!",
                            "CPF Duplicado",
                            JOptionPane.ERROR_MESSAGE);

                } else {

                    JOptionPane.showMessageDialog(painelPrincipal, "Erro no banco de dados: " + ex.getMessage());

                }

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(painelPrincipal, "Erro inesperado: " + ex.getMessage());

            }

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

                try {

                    alunoDao.deletar(Integer.parseInt(txtId.getText()));
                    preencherTabAlunoReduzida(alunoDao, modeloReduzido);
                    limparCamposAluno(txtNome, txtCPFFinal, txtDataFinal, comboEditResp1, comboEditResp2, txtId);
                    JOptionPane.showMessageDialog(painelPrincipal, "Aluno deletado com sucesso!");

                } catch (SQLException ex) {

                    JOptionPane.showMessageDialog(null, "Falha ao excluir no banco: " + ex.getMessage());

                } catch (Exception ex) {

                    JOptionPane.showMessageDialog(painelPrincipal, "Erro inesperado: " + ex.getMessage());

                }

            }

        });

        painelPrincipal.add(painelEditor);
        painelPrincipal.add(new JScrollPane(tabela));

        preencherTabAlunoReduzida(alunoDao, modeloReduzido);

        return painelPrincipal;

    }

    private static JPanel criarRespEditar(ResponsavelDAO respDao, DefaultTableModel modeloReduzido) {

        JPanel painelPrincipal = new JPanel(new GridLayout(1, 2, 10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel painelEditor = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtId = new JTextField(2);
        txtId.setEditable(false);
        JTextField txtNome = new JTextField(20);

        JFormattedTextField txtCPF = null;
        JFormattedTextField txtData = null;

        try {

            MaskFormatter mascara = new MaskFormatter("###.###.###-##");
            mascara.setPlaceholderCharacter('_');
            txtCPF = new JFormattedTextField(mascara);
            txtCPF.setColumns(9);

        } catch (Exception e) { e.printStackTrace(); }

        try {

            MaskFormatter m = new MaskFormatter("##/##/####");
            m.setPlaceholderCharacter('_');
            txtData = new JFormattedTextField(m);
            txtData.setColumns(6);

        } catch (Exception e)  { e.printStackTrace(); }

        final JFormattedTextField txtCPFFinal = txtCPF;
        final JFormattedTextField txtDataFinal = txtData;

        JButton btnEditar = new JButton("Salvar Alterações");
        JButton btnExcluir = new JButton("Excluir Responsável");
        btnExcluir.setBackground(new Color(255, 150, 150));

        painelEditor.add(new JLabel("ID:")); painelEditor.add(txtId);
        painelEditor.add(new JLabel("Nome:")); painelEditor.add(txtNome);
        painelEditor.add(new JLabel("CPF:")); painelEditor.add(txtCPFFinal);
        painelEditor.add(new JLabel("Data de Nascimento:")); painelEditor.add(txtDataFinal);
        painelEditor.add(btnEditar);
        painelEditor.add(btnExcluir);

        JTable tabela  = new JTable(modeloReduzido);
        tabela.setFillsViewportHeight(true);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.getTableHeader().setResizingAllowed(false);
        tabela.setDefaultEditor(Object.class, null);

        tabela.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting() && tabela.getSelectedRow() != -1) {

                int linha = tabela.getSelectedRow();

                int id = Integer.parseInt(tabela.getValueAt(linha, 0).toString());

                Responsavel r = respDao.buscarPorId(id);
                if (r != null) {

                    txtId.setText(String.valueOf(r.getId()));
                    txtNome.setText(r.getNome());
                    txtCPFFinal.setText(r.getCPF());
                    txtDataFinal.setText(r.getDataNascimento());

                }

            }

        });

        btnEditar.addActionListener(e -> {

            String idTexto = txtId.getText();

            if (idTexto.isEmpty()) {

                JOptionPane.showMessageDialog(painelPrincipal, "Selecione um responsável na tabela para editar.");
                return;

            }

            String novoNome = txtNome.getText().trim();
            String novoCPF = txtCPFFinal.getText().replace("_", "").trim();
            String novaData = txtDataFinal.getText().replace("_", "").trim();

            if (novoNome.isEmpty()) {

                JOptionPane.showMessageDialog(painelPrincipal, "O nome não pode estar vazio!");
                return;

            }

            if (novoCPF.length() < 14) {

                JOptionPane.showMessageDialog(painelPrincipal, "Cpf inválido!");
                return;

            }

            if (novaData.length() < 10 || !isDataValida(novaData)) {

                JOptionPane.showMessageDialog(painelPrincipal, "Data inválida!");
                return;

            }

            Responsavel respEditado = new Responsavel(novoNome, novoCPF, novaData);
            respEditado.setId(Integer.parseInt(idTexto));

            try {

                respDao.atualizar(respEditado);

                preencherTabRespReduzida(respDao, modeloReduzido);

                JOptionPane.showMessageDialog(painelPrincipal, "Dados atualizados com sucesso!");

            } catch (SQLException ex) {

                if (ex.getMessage().contains("UNIQUE constraint failed: responsavel.cpf")) {

                    JOptionPane.showMessageDialog(painelPrincipal,
                            "Já existe alguém cadastrado com este CPF!",
                            "CPF Duplicado",
                            JOptionPane.ERROR_MESSAGE);

                } else {

                    JOptionPane.showMessageDialog(painelPrincipal, "Erro no banco de dados: " + ex.getMessage());

                }

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(painelPrincipal, "Erro inesperado: " + ex.getMessage());

            }

        });

        btnExcluir.addActionListener(e -> {

            if (txtId.getText().isEmpty()) {

                JOptionPane.showMessageDialog(null, "Selecione um responsável na tabela para excluir.");
                return;

            }

            int id = Integer.parseInt((txtId.getText()));

            if (respDao.temAlunosVinculados(id)) {

                JOptionPane.showMessageDialog(null,
                        "Não é possível excluir! Este responsável está vinculado a um ou mais alunos.",
                        "Bloqueado", JOptionPane.ERROR_MESSAGE);
                return;

            }

            Object[] opcoes = {"Confirmar", "Cancelar"};

            int escolha = JOptionPane.showOptionDialog(

                    painelPrincipal,
                    "Tem certeza que deseja excluir o responsavel " + txtNome.getText() + "?",
                    "Atenção!",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    opcoes,
                    opcoes[1]

            );

            if (escolha == 0) {

                try {

                    respDao.deletar(Integer.parseInt(txtId.getText()));
                    preencherTabRespReduzida(respDao, modeloReduzido);
                    limparCamposResponsavel(txtNome, txtCPFFinal, txtDataFinal, txtId);
                    JOptionPane.showMessageDialog(painelPrincipal, "Responsável deletado com sucesso!");

                } catch (SQLException ex) {

                    JOptionPane.showMessageDialog(null, "Falha ao excluir no banco: " + ex.getMessage());

                } catch (Exception ex) {

                    JOptionPane.showMessageDialog(painelPrincipal, "Erro inesperado: " + ex.getMessage());

                }

            }

        });

        painelPrincipal.add(painelEditor);
        painelPrincipal.add(new JScrollPane(tabela));

        preencherTabRespReduzida(respDao, modeloReduzido);

        return painelPrincipal;

    }

    private static JPanel criarPainelRelatorio(AlunoDAO alunoDao, ResponsavelDAO respDao) {

        JPanel painel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("Central de Exportação (Excel/CSV)", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridy = 0;
        painel.add(titulo, gbc);

        JButton btnAlunos = new JButton("Exportar Lista de Alunos");
        gbc.gridy = 1;
        painel.add(btnAlunos, gbc);

        JButton btnResps = new JButton("Exportar Lista de Responsáveis");
        gbc.gridy = 2;
        painel.add(btnResps, gbc);

        JButton btnAmbos = new JButton("Exportar Ambos");
        btnAmbos.setBackground(new Color(200, 230, 200));
        gbc.gridy = 3;
        painel.add(btnAmbos, gbc);

        btnAlunos.addActionListener(e -> acaoExportar(alunoDao, respDao, "Alunos"));
        btnResps.addActionListener(e -> acaoExportar(alunoDao, respDao, "Responsaveis"));
        btnAmbos.addActionListener(e -> acaoExportar(alunoDao, respDao, "ambos"));

        return painel;

    }

    private static void preencherTabAluno(AlunoDAO dao, ResponsavelDAO respDao, DefaultTableModel modelo) {

        modelo.setRowCount(0);
        int contadorVisual = 1;

        for (Aluno a : dao.listar()) {

            Responsavel r1 = respDao.buscarPorId(a.getIdResponsavel1());
            String nomeResp1 = (r1 != null) ? r1.getNome() : "Não encontrado";

            String nomeResp2 = "Nenhum";
            if (a.getIdResponsavel2() > 0) {

                Responsavel r2 = respDao.buscarPorId(a.getIdResponsavel2());
                if (r2 != null) nomeResp2 = r2.getNome();

            }

            Object[] linha = {
                    contadorVisual++,
                    a.getNome(),
                    a.getCPF(),
                    a.getDataNascimento(),
                    nomeResp1,
                    nomeResp2
            };
            modelo.addRow(linha);

        }

    }

    private static void preencherTabAlunoReduzida(AlunoDAO dao, DefaultTableModel modelo) {

        modelo.setRowCount(0);

        for (Aluno a : dao.listar()) {

            Object[] linha = {
                    a.getId(),
                    a.getNome(),
            };
            modelo.addRow(linha);

        }

    }

    private static void preencherTabResp(ResponsavelDAO dao, AlunoDAO alunoDao, DefaultTableModel modelo) {

        modelo.setRowCount(0);
        int contadorVisual = 1;

        for (Responsavel r : dao.listar()) {

            List<String> alunos = alunoDao.buscarNomesAlunosPorResponsavel(r.getId());
            String nomesAlunos = String.join(", ", alunos);
            if(nomesAlunos.isEmpty()) nomesAlunos = "Nenhum";

            Object[] linha = {

                    contadorVisual++,
                    r.getNome(),
                    r.getCPF(),
                    r.getDataNascimento(),
                    nomesAlunos

            };
            modelo.addRow(linha);

        }

    }

    private static void preencherTabRespReduzida(ResponsavelDAO dao, DefaultTableModel modelo) {

        modelo.setRowCount(0);

        for (Responsavel r : dao.listar()) {

            Object[] linha = {

                    r.getId(),
                    r.getNome(),

            };
            modelo.addRow(linha);

        }

    }

    private static void limparCamposResponsavel(JTextField txtNome, JFormattedTextField txtCPF, JFormattedTextField txtData, JTextField txtId) {

        txtNome.setText("");
        txtCPF.setValue(null);
        txtData.setValue(null);
        if(txtId != null) txtId.setText("");

    }


    private static void limparCamposAluno(JTextField txtNome, JFormattedTextField txtCPF, JFormattedTextField txtData, JComboBox cb1, JComboBox cb2, JTextField txtId) {

        txtNome.setText("");
        txtCPF.setValue(null);
        txtData.setValue(null);

        if (cb1.getItemCount() > 0) cb1.setSelectedIndex(0);
        if (cb2.getItemCount() > 0) cb2.setSelectedIndex(0);

        if (txtId != null) txtId.setText("");

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

    private static void atualizarCombosResponsaveis(ResponsavelDAO dao, JComboBox... combos) {

        List<Responsavel> lista = dao.listar();
        for (JComboBox combo : combos) {

            combo.removeAllItems();

            if (combo.getName() != null && combo.getName().equals("Opcional")) {

                combo.addItem(new ResponsavelComboItem(0, "Nenhum"));

            }

            for (Responsavel r : lista) {

                combo.addItem(new ResponsavelComboItem(r.getId(), r.getNome()));

            }

        }

    }

    public static void selecionarNoCombo(JComboBox<ResponsavelComboItem> combo, int idBuscado) {

        for (int i = 0; i < combo.getItemCount(); i++) {

            if (combo.getItemAt(i).getId() == idBuscado) {

                combo.setSelectedIndex(i);

                return;

            }

        }

        combo.setSelectedIndex(0);

    }

    public static void acaoExportar(AlunoDAO alunoDao, ResponsavelDAO respDao, String tipo) {

        JFileChooser chooser = new JFileChooser();

        if (tipo.equals("ambos")) {

            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setDialogTitle("Selecione a pasta para salvar os relatórios");

        } else {

            chooser.setDialogTitle("Salvar Relatório");
            chooser.setSelectedFile(new java.io.File(tipo + "_Cadastrados.csv"));

        }

        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

            try {

                if (tipo.equals("Alunos") || tipo.equals("ambos")) {

                    String path = tipo.equals("ambos") ?
                            chooser.getSelectedFile().getPath() + "/Alunos_Cadastrados.csv" :
                            chooser.getSelectedFile().getPath();
                    gerarCsvAlunos(alunoDao, respDao, path);

                }
                if (tipo.equals("Responsaveis") || tipo.equals("ambos")) {

                    String path = tipo.equals("ambos") ?
                            chooser.getSelectedFile().getPath() + "/Responsaveis_Cadastrados.csv" :
                            chooser.getSelectedFile().getPath();
                    gerarCsvResponsaveis(respDao, alunoDao, path);

                }

                JOptionPane.showMessageDialog(null, "Exportação concluída com sucesso!");

                if (java.awt.Desktop.isDesktopSupported()) {

                    java.io.File pastaParaAbrir;

                    if (tipo.equals("ambos")) {

                        pastaParaAbrir = chooser.getSelectedFile();

                    } else {

                        pastaParaAbrir = chooser.getSelectedFile().getParentFile();

                    }

                    if (pastaParaAbrir != null && pastaParaAbrir.exists()) {

                        java.awt.Desktop.getDesktop().open(pastaParaAbrir);

                    }

                }

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(null, "Erro ao exportar: " + ex.getMessage());

            }

        }

    }

    private static void gerarCsvAlunos(AlunoDAO alunoDao, ResponsavelDAO respDao, String path) throws java.io.IOException {

        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.File(path), "ISO-8859-1")) {

            String dataHora = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

            pw.println("sep=;");
            pw.println("************************************************************");
            pw.println("                SISTEMA DE CADASTRO AS DOWN                 ");
            pw.println("                    RELATÓRIO DE ALUNOS                     ");
            pw.println("                Gerado em: " + dataHora);
            pw.println("************************************************************");
            pw.println();
            pw.println("Nome;CPF;Data Nascimento;Responsável 1; Responsável 2");

            for (Aluno a : alunoDao.listar()) {

                Responsavel r1 = respDao.buscarPorId(a.getIdResponsavel1());
                String nomeR1 = (r1 != null) ? r1.getNome() : "Não encontrado";

                String nomeR2 = "Nenhum";
                if (a.getIdResponsavel2() > 0) {

                    Responsavel r2 = respDao.buscarPorId(a.getIdResponsavel2());
                    if (r2 != null) nomeR2 = r2.getNome();

                }

                pw.printf("%s;%s;%s;%s;%s;\n", a.getNome(), a.getCPF(), a.getDataNascimento(), nomeR1, nomeR2);

            }

        }

    }

    private static void gerarCsvResponsaveis(ResponsavelDAO respDao, AlunoDAO alunoDao, String path) throws java.io.IOException {

        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.File(path), "ISO-8859-1")) {

            String dataHora = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

            pw.println("sep=;");
            pw.println("************************************************************");
            pw.println("                SISTEMA DE CADASTRO AS DOWN                 ");
            pw.println("                 RELATÓRIO DE RESPONSÁVEIS                  ");
            pw.println("                Gerado em: " + dataHora);
            pw.println("************************************************************");
            pw.println();

            pw.println("Nome;CPF;Data Nascimento;Alunos Vinculados");

            for (Responsavel r : respDao.listar()) {

                List<String> alunos = alunoDao.buscarNomesAlunosPorResponsavel(r.getId());
                String nomesAlunos = String.join(", ", alunos);
                if (nomesAlunos.isEmpty()) nomesAlunos = "Nenhum";

                pw.printf("%s;%s;%s;%s;\n", r.getNome(), r.getCPF(), r.getDataNascimento(), nomesAlunos);

            }

        }

    }

    public static class ResponsavelComboItem {

        private int id;
        private String nome;

        public ResponsavelComboItem(int id, String nome) {

            this.id = id;
            this.nome = nome;

        }

        public int getId() { return id; }

        public String toString() {

            return nome;

        }

    }

}