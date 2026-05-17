package com.projeto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
    private static JTabbedPane menuAbas;
    private static CardLayout cardLayout = new CardLayout();
    private static JPanel painelContainer = new JPanel(cardLayout);

    public static void main(String[] args) {

        Conexao.inicializarBanco();

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("SCA - Sistema de Cadastro Asdown");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.setPreferredSize(new Dimension(900, 700));
            frame.setMinimumSize(new Dimension(900, 700));

            JPanel telaLogin = criarPainelLogin(frame);
            JPanel telaSistema = interfaceSistema();

            painelContainer.add(telaLogin, "LOGIN");
            painelContainer.add(telaSistema, "SISTEMA");

            frame.add(painelContainer);

            cardLayout.show(painelContainer, "LOGIN");

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
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

        JtextFieldSomenteLetras txtNome = new JtextFieldSomenteLetras(20, 100);
        JFormattedTextField txtCPF = null;
        JFormattedTextField txtData = null;
        JtextFieldSomenteNumeros txtRG = new JtextFieldSomenteNumeros(12, 12);
        JtextFieldSomenteLetras txtCivil = new JtextFieldSomenteLetras(10, 40);
        JFormattedTextField txtCel = null;
        JtextFieldLimitado txtEmail = new JtextFieldLimitado(20, 50);

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

        try {

            MaskFormatter mascara = new MaskFormatter("(##) 9####-####");
            mascara.setPlaceholderCharacter('_');
            txtCel = new JFormattedTextField(mascara);
            txtCel.setColumns(15);

        } catch (Exception e) { e.printStackTrace(); }

        final JFormattedTextField txtCPFFinal = txtCPF;
        final JFormattedTextField txtDataFinal = txtData;
        final JFormattedTextField txtCelFinal = txtCel;

        comboCadResp1 = new JComboBox<>();
        comboCadResp1.setBackground(Color.WHITE);
        comboCadResp2 = new JComboBox<>();
        comboCadResp2.setBackground(Color.WHITE);
        comboCadResp2.setName("Opcional");

        atualizarCombosResponsaveis(respDao, comboCadResp1, comboCadResp2);

        JButton btnSalvar = new JButton("Salvar");

        btnSalvar.addActionListener(e -> {

            String nome = txtNome.getText();
            String CPF = txtCPFFinal.getText().replace("_", "").trim();
            String dataNasc = txtDataFinal.getText().replace("_", "").trim();
            String RG = txtRG.getText();
            String civil = txtCivil.getText();
            String celular = txtCelFinal.getText().replace("_", "").trim();
            String email = txtEmail.getText();

            if(nome.isEmpty()) {

                JOptionPane.showMessageDialog(painel, "Preencha o nome!");
                return;

            }

            if(CPF.length() < 14) {

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

            if (celular.length() == 5) {

                celular = null;

            } else if (celular.length() < 15) {

                JOptionPane.showMessageDialog(painel, "Preencha o número de celular completo!");
                return;

            }

            try {

                Aluno aluno = new Aluno(nome, CPF, dataNasc, id1, id2, RG, civil, celular, email);
                alunoDao.inserir(aluno);

                limparCamposAluno(txtNome, txtCPFFinal, txtDataFinal, comboCadResp1, comboCadResp2, txtRG, txtCivil,
                        txtCelFinal, txtEmail, null);

                JOptionPane.showMessageDialog(painel, "Aluno salvo com sucesso!");

            } catch (SQLException ex) {

                if (ex.getMessage().toLowerCase().contains("UNIQUE constraint failed: aluno.cpf")) {

                    JOptionPane.showMessageDialog(painel,
                            "Já existe alguém cadastrado com este CPF!",
                            "CPF Duplicado",
                            JOptionPane.ERROR_MESSAGE);

                } else if (ex.getMessage().toLowerCase().contains("UNIQUE constraint failed: aluno.celular")) {

                    JOptionPane.showMessageDialog(painel,
                            "Já existe alguém cadastrado com este número de celular!",
                            "Celular Duplicado",
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
        painel.add(new JLabel("RG: ")); painel.add(txtRG);
        painel.add(new JLabel("Estado Civil: ")); painel.add(txtCivil);
        painel.add(new JLabel("Número de Cel.: ")); painel.add(txtCelFinal);
        painel.add(new JLabel("E-mail: ")); painel.add(txtEmail);
        painel.add(btnSalvar);

        return painel;

    }

    private static JPanel criarFormularioResp(ResponsavelDAO respDao) {

        JPanel painel = new JPanel(new FlowLayout());

        JtextFieldSomenteLetras txtNome = new JtextFieldSomenteLetras(20, 100);
        JFormattedTextField txtCPF = null;
        JFormattedTextField txtData = null;
        JtextFieldSomenteNumeros txtRG = new JtextFieldSomenteNumeros(12, 12);
        JtextFieldSomenteLetras txtCivil = new JtextFieldSomenteLetras(10, 40);
        JFormattedTextField txtCel = null;
        JtextFieldLimitado txtEmail = new JtextFieldLimitado(20, 50);
        JtextFieldSomenteLetras txtProf = new JtextFieldSomenteLetras(20, 40);
        JtextFieldLimitado txtTrab = new JtextFieldLimitado(20, 50);
        JtextFieldLimitado txtEnder = new JtextFieldLimitado(25, 40);
        JtextFieldSomenteLetras txtCid = new JtextFieldSomenteLetras(15, 40);
        String[] ufs = {"GO", "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "MA", "MT", "MS", "MG", "PA", "PB",
                "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO"};
        JComboBox<String> comboEstado = new JComboBox<>(ufs);
        comboEstado.setBackground(Color.WHITE);
        JFormattedTextField txtCep = null;

        try {

            MaskFormatter mascara = new MaskFormatter("###.###.###-##");
            mascara.setPlaceholderCharacter('_');
            txtCPF = new JFormattedTextField(mascara);
            txtCPF.setColumns(14);

        } catch (Exception e) { e.printStackTrace(); }

        try {

            MaskFormatter mascara = new MaskFormatter("##/##/####");
            mascara.setPlaceholderCharacter('_');
            txtData = new JFormattedTextField(mascara);
            txtData.setColumns(10);

        } catch (Exception e) { e.printStackTrace(); }

        try {

            MaskFormatter mascara = new MaskFormatter("(##) 9####-####");
            mascara.setPlaceholderCharacter('_');
            txtCel = new JFormattedTextField(mascara);
            txtCel.setColumns(15);

        } catch (Exception e) { e.printStackTrace(); }

        try {

            MaskFormatter mascara = new MaskFormatter("#####-###");
            mascara.setPlaceholderCharacter('_');
            txtCep = new JFormattedTextField(mascara);
            txtCep.setColumns(9);

        } catch (Exception e) { e.printStackTrace(); }

        final JFormattedTextField txtCPFFinal = txtCPF;
        final JFormattedTextField txtDataFinal = txtData;
        final JFormattedTextField txtCelFinal = txtCel;
        final JFormattedTextField txtCepFinal = txtCep;

        JButton btnSalvar = new JButton("Salvar");

        btnSalvar.addActionListener(e -> {

            String nome = txtNome.getText();
            String CPF = txtCPFFinal.getText().replace("_", "").trim();
            String dataNasc = txtDataFinal.getText().replace("_", "").trim();
            String RG = txtRG.getText();
            String civil = txtCivil.getText();
            String celular = txtCelFinal.getText().replace("_", "").trim();
            String email = txtEmail.getText();
            String prof = txtProf.getText();
            String trab = txtTrab.getText();
            String endereco = txtEnder.getText();
            String cidade = txtCid.getText();
            String estado = (String) comboEstado.getSelectedItem();
            String cep = txtCepFinal.getText().replace("_", "").trim();

            if(nome.isEmpty()) {

                JOptionPane.showMessageDialog(painel, "Preencha o nome!");
                return;

            }

            if(CPF.length() < 14) {

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

            if(celular.length() < 15) {

                JOptionPane.showMessageDialog(painel, "Preencha o número de celular completo!");
                return;

            }

            if(endereco.isEmpty()) {

                JOptionPane.showMessageDialog(painel, "Preencha o endereço!");
                return;

            }

            if(cidade.isEmpty()) {

                JOptionPane.showMessageDialog(painel, "Preencha a cidade!");
                return;

            }

            if(cep.length() < 9) {

                JOptionPane.showMessageDialog(painel, "Preencha o CEP completo!");
                return;

            }

            try {

                Responsavel resp = new Responsavel(nome, CPF, dataNasc, RG, civil, celular, email, prof, trab, endereco, cidade, estado, cep);
                respDao.inserir(resp);

                limparCamposResponsavel(txtNome, txtCPFFinal, txtDataFinal, txtRG, txtCivil, txtCelFinal, txtEmail,
                        txtProf, txtTrab, txtEnder, txtCid, comboEstado, txtCepFinal, null);

                JOptionPane.showMessageDialog(painel, "Responsável salvo com sucesso!");

            } catch (SQLException ex) {

                if (ex.getMessage().toLowerCase().contains("UNIQUE constraint failed: responsavel.cpf")) {

                    JOptionPane.showMessageDialog(painel,
                            "Já existe alguém cadastrado com este CPF!",
                            "CPF Duplicado",
                            JOptionPane.ERROR_MESSAGE);

                } else if (ex.getMessage().toLowerCase().contains("UNIQUE constraint failed: responsavel.celular")) {

                    JOptionPane.showMessageDialog(painel,
                            "Já existe alguém cadastrado com este número de celular!",
                            "Celular Duplicado",
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
        painel.add(new JLabel("RG: ")); painel.add(txtRG);
        painel.add(new JLabel("Estado Civil: ")); painel.add(txtCivil);
        painel.add(new JLabel("Número de Cel.: ")); painel.add(txtCelFinal);
        painel.add(new JLabel("E-mail: ")); painel.add(txtEmail);
        painel.add(new JLabel("Profissão: ")); painel.add(txtProf);
        painel.add(new JLabel("Local de Trabalho: ")); painel.add(txtTrab);
        painel.add(new JLabel("Endereço: ")); painel.add(txtEnder);
        painel.add(new JLabel("Cidade: ")); painel.add(txtCid);
        painel.add(new JLabel("Estado: ")); painel.add(comboEstado);
        painel.add(new JLabel("CEP: ")); painel.add(txtCepFinal);
        painel.add(btnSalvar);

        return painel;

    }

    private static JPanel criarPainelListagem(DefaultTableModel modeloAl, DefaultTableModel modeloResp, ResponsavelDAO respDao, AlunoDAO alunoDao) {

        JPanel painelPrincipal = new JPanel(new BorderLayout());

        JPanel painelSeletor = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] tipos = {"Alunos", "Responsáveis"};
        JComboBox<String> comboTipo = new JComboBox<>(tipos);
        comboTipo.setBackground(Color.WHITE);
        painelSeletor.add(new JLabel("Qual lista deseja ver?"));
        painelSeletor.add(comboTipo);

        JPanel containerCards = new JPanel(new CardLayout());

        JPanel listAluno = criarAlListagem(modeloAl, respDao, alunoDao);
        JPanel listResp = criarRespListagem(modeloResp, respDao, alunoDao);

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

    private static JPanel criarAlListagem(DefaultTableModel modeloAl, ResponsavelDAO respDao, AlunoDAO alunoDao) {

        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTable tabela = new JTable(modeloAl);
        tabela.setFillsViewportHeight(true);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.getTableHeader().setResizingAllowed(false);
        tabela.setDefaultEditor(Object.class, null);

        tabela.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseClicked(java.awt.event.MouseEvent e) {

                if (e.getClickCount() == 2) {

                    int linha = tabela.getSelectedRow();
                    if (linha != -1) {

                        int id = Integer.parseInt(tabela.getValueAt(linha, 0).toString());

                        abrirFichaAluno(id, respDao, alunoDao);

                    }

                }

            }

        });

        painel.add(new JLabel("Alunos Cadastrados:"), BorderLayout.NORTH);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        return painel;

    }

    private static JPanel criarRespListagem(DefaultTableModel modeloResp, ResponsavelDAO respDao, AlunoDAO alunoDao) {

        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTable tabela = new JTable(modeloResp);
        tabela.setFillsViewportHeight(true);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.getTableHeader().setResizingAllowed(false);
        tabela.setDefaultEditor(Object.class, null);

        tabela.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {

                    int linha = tabela.getSelectedRow();
                    if (linha != -1) {

                        int id = Integer.parseInt(tabela.getValueAt(linha, 0).toString());

                        abrirFichaResponsavel(id, respDao, alunoDao);

                    }

                }

            }

        });

        painel.add(new JLabel("Responsáveis Cadastrados:"), BorderLayout.NORTH);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        return painel;

    }

    private static JPanel criarPainelEditar(AlunoDAO alunoDao, ResponsavelDAO respDao, DefaultTableModel modeloAlReduzido,
                                            DefaultTableModel modeloRespReduzido) {

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
        JtextFieldSomenteLetras txtNome = new JtextFieldSomenteLetras(20, 100);

        JFormattedTextField txtCPF = null;
        JFormattedTextField txtData = null;
        JtextFieldSomenteNumeros txtRG = new JtextFieldSomenteNumeros(12, 12);
        JtextFieldSomenteLetras txtCivil = new JtextFieldSomenteLetras(10, 40);
        JtextFieldLimitado txtEmail = new JtextFieldLimitado(20, 50);
        JFormattedTextField txtCel = null;

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

        try {

            MaskFormatter mascara = new MaskFormatter("(##) 9####-####");
            mascara.setPlaceholderCharacter('_');
            txtCel = new JFormattedTextField(mascara);
            txtCel.setColumns(15);

        } catch (Exception e) { e.printStackTrace(); }

        final JFormattedTextField txtCPFFinal = txtCPF;
        final JFormattedTextField txtDataFinal = txtData;
        final JFormattedTextField txtCelFinal = txtCel;

        JButton btnEditar = new JButton("Salvar Alterações");
        JButton btnExcluir = new JButton("Excluir Aluno");
        btnExcluir.setBackground(new Color(255, 150, 150));

        painelEditor.add(new JLabel("ID:")); painelEditor.add(txtId);
        painelEditor.add(new JLabel("Nome:")); painelEditor.add(txtNome);
        painelEditor.add(new JLabel("CPF:")); painelEditor.add(txtCPFFinal);
        painelEditor.add(new JLabel("Data de Nascimento:")); painelEditor.add(txtDataFinal);
        painelEditor.add(new JLabel("Resp. 1")); painelEditor.add(comboEditResp1);
        painelEditor.add(new JLabel("Resp. 2")); painelEditor.add(comboEditResp2);
        painelEditor.add(new JLabel("RG: ")); painelEditor.add(txtRG);
        painelEditor.add(new JLabel("Estado Civil: ")); painelEditor.add(txtCivil);
        painelEditor.add(new JLabel("Número de Cel.: ")); painelEditor.add(txtCelFinal);
        painelEditor.add(new JLabel("E-Mail: ")); painelEditor.add(txtEmail);
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
                    txtRG.setText(String.valueOf(a.getRG()));
                    txtCivil.setText(String.valueOf(a.getEstCivil()));
                    txtCelFinal.setText(a.getCelular());
                    txtEmail.setText(a.getEmail());

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
            String novoRG = txtRG.getText().trim();
            String novoCivil = txtCivil.getText().trim();
            String novoCelular = txtCelFinal.getText().replace("_", "").trim();
            String novoEmail = txtEmail.getText().trim();

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

            if (novoCelular.length() == 5) {

                novoCelular = null;

            } else if(novoCelular.length() < 15) {

                JOptionPane.showMessageDialog(painelPrincipal, "Número de celular inválido!");
                return;

            }

            Aluno alunoEditado = new Aluno(novoNome, novoCPF, novaData, novoId1, novoId2, novoRG, novoCivil,
                    novoCelular, novoEmail);
            alunoEditado.setId(Integer.parseInt(idTexto));

            try {

                alunoDao.atualizar(alunoEditado);

                preencherTabAlunoReduzida(alunoDao, modeloReduzido);

                JOptionPane.showMessageDialog(painelPrincipal, "Dados atualizados com sucesso!");

            } catch (SQLException ex) {

                if (ex.getMessage().toLowerCase().contains("UNIQUE constraint failed: aluno.cpf")) {

                    JOptionPane.showMessageDialog(painelPrincipal,
                            "Já existe alguém cadastrado com este CPF!",
                            "CPF Duplicado",
                            JOptionPane.ERROR_MESSAGE);

                } else if (ex.getMessage().toLowerCase().contains("UNIQUE constraint failed: aluno.celular")) {

                    JOptionPane.showMessageDialog(painelPrincipal,
                            "Já existe alguém cadastrado com este número de celular!",
                            "Celular Duplicado",
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
                    limparCamposAluno(txtNome, txtCPFFinal, txtDataFinal, comboEditResp1, comboEditResp2, txtRG, txtCivil,
                            txtCelFinal, txtEmail, txtId);
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
        JtextFieldSomenteLetras txtNome = new JtextFieldSomenteLetras(20, 100);
        JFormattedTextField txtCPF = null;
        JFormattedTextField txtData = null;
        JtextFieldSomenteNumeros txtRG = new JtextFieldSomenteNumeros(12, 12);
        JtextFieldSomenteLetras txtCivil = new JtextFieldSomenteLetras(10, 40);
        JtextFieldLimitado txtEmail = new JtextFieldLimitado(20, 50);
        JFormattedTextField txtCel = null;
        JtextFieldSomenteLetras txtProf = new JtextFieldSomenteLetras(20, 40);
        JtextFieldLimitado txtTrab = new JtextFieldLimitado(20, 50);
        JtextFieldLimitado txtEnder = new JtextFieldLimitado(25, 40);
        JtextFieldSomenteLetras txtCid = new JtextFieldSomenteLetras(15, 40);
        String[] ufs = {"GO", "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "MA", "MT", "MS", "MG", "PA", "PB",
                "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO"};
        JComboBox<String> comboEstado = new JComboBox<>(ufs);
        comboEstado.setBackground(Color.WHITE);
        JFormattedTextField txtCep = null;

        try {

            MaskFormatter mascara = new MaskFormatter("###.###.###-##");
            mascara.setPlaceholderCharacter('_');
            txtCPF = new JFormattedTextField(mascara);
            txtCPF.setColumns(14);

        } catch (Exception e) { e.printStackTrace(); }

        try {

            MaskFormatter m = new MaskFormatter("##/##/####");
            m.setPlaceholderCharacter('_');
            txtData = new JFormattedTextField(m);
            txtData.setColumns(10);

        } catch (Exception e)  { e.printStackTrace(); }

        try {

            MaskFormatter mascara = new MaskFormatter("(##) 9####-####");
            mascara.setPlaceholderCharacter('_');
            txtCel = new JFormattedTextField(mascara);
            txtCel.setColumns(15);

        } catch (Exception e) { e.printStackTrace(); }

        try {

            MaskFormatter mascara = new MaskFormatter("#####-###");
            mascara.setPlaceholderCharacter('_');
            txtCep = new JFormattedTextField(mascara);
            txtCep.setColumns(9);

        } catch (Exception e) { e.printStackTrace(); }

        final JFormattedTextField txtCPFFinal = txtCPF;
        final JFormattedTextField txtDataFinal = txtData;
        final JFormattedTextField txtCelFinal = txtCel;
        final JFormattedTextField txtCepFinal = txtCep;

        JButton btnEditar = new JButton("Salvar Alterações");
        JButton btnExcluir = new JButton("Excluir Responsável");
        btnExcluir.setBackground(new Color(255, 150, 150));

        painelEditor.add(new JLabel("ID: ")); painelEditor.add(txtId);
        painelEditor.add(new JLabel("Nome: ")); painelEditor.add(txtNome);
        painelEditor.add(new JLabel("CPF: ")); painelEditor.add(txtCPFFinal);
        painelEditor.add(new JLabel("Data de Nascimento: ")); painelEditor.add(txtDataFinal);
        painelEditor.add(new JLabel("RG: ")); painelEditor.add(txtRG);
        painelEditor.add(new JLabel("Estado Civil: ")); painelEditor.add(txtCivil);
        painelEditor.add(new JLabel("Número de Cel.: ")); painelEditor.add(txtCelFinal);
        painelEditor.add(new JLabel("E-Mail: ")); painelEditor.add(txtEmail);
        painelEditor.add(new JLabel("Profissão: ")); painelEditor.add(txtProf);
        painelEditor.add(new JLabel("Local de Trabalho: ")); painelEditor.add(txtTrab);
        painelEditor.add(new JLabel("Endereço: ")); painelEditor.add(txtEnder);
        painelEditor.add(new JLabel("Cidade: ")); painelEditor.add(txtCid);
        painelEditor.add(new JLabel("Estado: ")); painelEditor.add(comboEstado);
        painelEditor.add(new JLabel("CEP: ")); painelEditor.add(txtCepFinal);
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
                    txtRG.setText(String.valueOf(r.getRG()));
                    txtCivil.setText(String.valueOf(r.getEstCivil()));
                    txtCelFinal.setText(r.getCelular());
                    txtEmail.setText(r.getEmail());
                    txtProf.setText(r.getProfissao());
                    txtTrab.setText(r.getLocTrabalho());
                    txtEnder.setText(r.getEndereco());
                    txtCid.setText(r.getCidade());
                    comboEstado.setSelectedItem(r.getEstado());
                    txtCepFinal.setText(r.getCep());

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
            String novoRG = txtRG.getText().trim();
            String novoCivil = txtCivil.getText().trim();
            String novoCelular = txtCelFinal.getText().replace("_", "").trim();
            String novoEmail = txtEmail.getText().trim();
            String novaProf = txtProf.getText().trim();
            String novoTrab = txtTrab.getText().trim();
            String novoEnder = txtEnder.getText().trim();
            String novaCid = txtCid.getText().trim();
            String novoEstado = (String) comboEstado.getSelectedItem();
            String novoCep = txtCepFinal.getText().trim();

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

            if(novoCelular.length() < 15) {

                JOptionPane.showMessageDialog(painelPrincipal, "Número de celular inválido!");
                return;

            }

            if(novoEnder.isEmpty()) {

                JOptionPane.showMessageDialog(painelPrincipal, "Endereço não pode estar vazio!");
                return;

            }

            if(novaCid.isEmpty()) {

                JOptionPane.showMessageDialog(painelPrincipal, "Cidade não pode estar vazia!");
                return;

            }

            if(novoCep.length() < 9) {

                JOptionPane.showMessageDialog(painelEditor, "Preencha o CEP completo!");
                return;

            }

            Responsavel respEditado = new Responsavel(novoNome, novoCPF, novaData, novoRG, novoCivil, novoCelular,
                    novoEmail, novaProf, novoTrab, novoEnder, novaCid, novoEstado, novoCep);
            respEditado.setId(Integer.parseInt(idTexto));

            try {

                respDao.atualizar(respEditado);

                preencherTabRespReduzida(respDao, modeloReduzido);

                JOptionPane.showMessageDialog(painelPrincipal, "Dados atualizados com sucesso!");

            } catch (SQLException ex) {

                if (ex.getMessage().toLowerCase().contains("UNIQUE constraint failed: responsavel.cpf")) {

                    JOptionPane.showMessageDialog(painelPrincipal,
                            "Já existe alguém cadastrado com este CPF!",
                            "CPF Duplicado",
                            JOptionPane.ERROR_MESSAGE);

                } else if (ex.getMessage().toLowerCase().contains("UNIQUE constraint failed: responsavel.celular")) {

                    JOptionPane.showMessageDialog(painelPrincipal,
                            "Já existe alguém cadastrado com este número de celular!",
                            "Celular Duplicado",
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
                    limparCamposResponsavel(txtNome, txtCPFFinal, txtDataFinal, txtRG, txtCivil, txtCelFinal, txtEmail,
                            txtProf, txtTrab, txtEnder, txtCid, comboEstado, txtCepFinal, txtId);
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

    private static JPanel criarPainelLogin(JFrame frame) {

        UserDAO userDao = new UserDAO();
        JPanel telaLogin = new JPanel(new GridBagLayout());
        JTextField txtUser = new JTextField(15);
        JPasswordField txtPass = new JPasswordField(15);
        JButton btnLogin = new JButton("Login");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        JLabel lblTitulo = new JLabel("Tela de Login", JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        telaLogin.add(lblTitulo, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 1;
        telaLogin.add(new JLabel("Usuário:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        telaLogin.add(txtUser, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        telaLogin.add(new JLabel("Senha:"), gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        telaLogin.add(txtPass, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        telaLogin.add(btnLogin, gbc);

        btnLogin.addActionListener(e -> {

            if (userDao.validarLogin(txtUser.getText(), new String(txtPass.getPassword()))) {

                destrancarSistema();

            } else {

                JOptionPane.showMessageDialog(frame, "Usuário ou senha inválidos.");

            }

        });

        return telaLogin;

    }

    private static JPanel criarPainelUsuarios() {

        UserDAO userDao = new UserDAO();
        JPanel telaUsers = new JPanel(new BorderLayout(10, 10));
        telaUsers.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] colunas = {"ID", "Usuário"};
        DefaultTableModel modeloUser = new DefaultTableModel(colunas, 0);

        JTable tabela = new JTable(modeloUser);
        tabela.setFillsViewportHeight(true);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.getTableHeader().setResizingAllowed(false);
        tabela.setDefaultEditor(Object.class, null);
        preencherTabUsers(userDao, modeloUser);

        JPanel painelLateral = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField txtNovoUser = new JTextField(10);
        JPasswordField txtNovaPass = new JPasswordField(10);
        JButton btnSalvar = new JButton("Criar Novo Usuário");
        JButton btnEditar = new JButton("Alterar Usuário Selecionado");
        JButton btnExcluir = new JButton("Excluir Selecionado");

        JLabel lblIdOculto = new JLabel("");

        gbc.gridx = 0; gbc.gridy = 0; painelLateral.add(new JLabel("Usuário:"), gbc);
        gbc.gridy = 1; painelLateral.add(txtNovoUser, gbc);
        gbc.gridy = 2; painelLateral.add(new JLabel("Senha:"), gbc);
        gbc.gridy = 3; painelLateral.add(txtNovaPass, gbc);
        gbc.gridy = 4; painelLateral.add(btnSalvar, gbc);
        gbc.gridy = 5; painelLateral.add(btnEditar, gbc);
        gbc.gridy = 6; painelLateral.add(new JSeparator(), gbc);
        gbc.gridy = 7; painelLateral.add(btnExcluir, gbc);

        telaUsers.add(new JLabel("Gerenciar Contas de Usuário", JLabel.CENTER), BorderLayout.NORTH);
        telaUsers.add(new JScrollPane(tabela), BorderLayout.CENTER);
        telaUsers.add(painelLateral, BorderLayout.EAST);

        tabela.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {

                int linha = tabela.getSelectedRow();

                if (linha != -1) {

                    lblIdOculto.setText(modeloUser.getValueAt(linha, 0).toString());
                    txtNovoUser.setText(modeloUser.getValueAt(linha, 1).toString());
                    txtNovaPass.setText("");

                }

            }

        });

        btnSalvar.addActionListener(e -> {

            String user = txtNovoUser.getText().trim();
            String pass = new String(txtNovaPass.getPassword()).trim();

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha Usuário e Senha!");
                return;
            }

            User u = new User();
            u.setUsername(user);
            u.setPassword(pass);

            if(userDao.inserir(u)) {

                JOptionPane.showMessageDialog(null, "Usuário criado!");
                preencherTabUsers(userDao, modeloUser);
                txtNovoUser.setText(""); txtNovaPass.setText("");
                lblIdOculto.setText("");

            } else {

                JOptionPane.showMessageDialog(null, "Usuário Já Utilizado!");

            }

        });

        btnEditar.addActionListener(e -> {

            if (lblIdOculto.getText().isEmpty()) {

                JOptionPane.showMessageDialog(null, "Selecione um usuário na tabela primeiro.");
                return;

            }

            int id = Integer.parseInt(lblIdOculto.getText());

            if (id == 1) {

                JOptionPane.showMessageDialog(null, "O Administrador padrão não pode ser alterado.");

                return;

            }

            if (txtNovoUser.getText().trim().isEmpty() || new String(txtNovaPass.getPassword()).trim().isEmpty()) {

                JOptionPane.showMessageDialog(null, "Preencha Usuário e Senha!");
                return;

            }

            User u = new User();
            u.setId(id);
            u.setUsername(txtNovoUser.getText());
            u.setPassword(new String(txtNovaPass.getPassword()).trim());

            if (userDao.atualizar(u)) {

                JOptionPane.showMessageDialog(null, "Usuário atualizado!");
                preencherTabUsers(userDao, modeloUser);
                txtNovoUser.setText(""); txtNovaPass.setText(""); lblIdOculto.setText("");
                tabela.clearSelection();

            }

        });

        btnExcluir.addActionListener(e -> {

            int linha = tabela.getSelectedRow();

            if (linha != -1) {

                int id = (int) modeloUser.getValueAt(linha, 0);

                if (id == 1) {

                    JOptionPane.showMessageDialog(null, "O Administrador padrão não pode ser excluído.");

                    return;

                }

                if (userDao.deletar(id)) {

                    preencherTabUsers(userDao, modeloUser);
                    txtNovoUser.setText(""); txtNovaPass.setText(""); lblIdOculto.setText("");

                }

            }

        });

        return telaUsers;

    }

    private static JPanel interfaceSistema() {

        JPanel painelSistema = new JPanel(new BorderLayout());

        AlunoDAO alunoDao = new AlunoDAO();
        ResponsavelDAO respDao = new ResponsavelDAO();

        menuAbas = new JTabbedPane();

        String[] colunasAl = {"Nº", "Nome", "CPF", "Data de Nascimento", "1º Responsável", "2º Responsável"};
        DefaultTableModel modeloAl = new DefaultTableModel(colunasAl, 0);
        String[] colunasResp = {"Nº", "Nome", "CPF", "Celular", "Alunos Vinculados"};
        DefaultTableModel modeloResp = new DefaultTableModel(colunasResp, 0);
        String[] colunasReduzidas = {"ID", "Nome"};
        DefaultTableModel modeloAlReduzido = new DefaultTableModel(colunasReduzidas, 0);
        DefaultTableModel modeloRespReduzido = new DefaultTableModel(colunasReduzidas, 0);

        menuAbas.addTab("Cadastrar", criarPainelCadastro(alunoDao, respDao));
        menuAbas.addTab("Listar", criarPainelListagem(modeloAl, modeloResp, respDao, alunoDao));
        menuAbas.addTab("Editar/Excluir", criarPainelEditar(alunoDao, respDao, modeloAlReduzido, modeloRespReduzido));
        menuAbas.addTab("Relatório", criarPainelRelatorio(alunoDao, respDao));
        menuAbas.addTab("Usuários", criarPainelUsuarios());

        menuAbas.addChangeListener(e -> {
            int aba = menuAbas.getSelectedIndex();
            if (aba == -1) return;
            String titulo = menuAbas.getTitleAt(aba);

            if (titulo.equals("Cadastrar")) {

                if (comboCadResp1 != null && comboCadResp2 != null) {

                    atualizarCombosResponsaveis(respDao, comboCadResp1, comboCadResp2);
                    System.out.println("Combos de responsáveis atualizados!");

                }

            } else if (titulo.equals("Listar")) {

                preencherTabAluno(alunoDao,respDao, modeloAl);
                preencherTabResp(respDao, alunoDao, modeloResp);

                System.out.println("Tabela atualizada!");

            } else if (titulo.equals("Editar/Excluir")) {

                if (comboEditResp1 != null && comboEditResp2 != null) {

                    atualizarCombosResponsaveis(respDao, comboEditResp1, comboEditResp2);
                    System.out.println("Combos de responsáveis atualizados!");

                }

                preencherTabAlunoReduzida(alunoDao, modeloAlReduzido);
                preencherTabRespReduzida(respDao, modeloRespReduzido);

                System.out.println("Tabela atualizada!");

            }

        });

        painelSistema.add(menuAbas, BorderLayout.CENTER);
        return painelSistema;

    }

    private static void destrancarSistema() {

        cardLayout.show(painelContainer, "SISTEMA");

        JOptionPane.showMessageDialog(null, "Acesso concedido!");

    }

    private static void preencherTabUsers(UserDAO dao, DefaultTableModel modelo) {

        modelo.setRowCount(0);

        for (User u : dao.listar()) {

            modelo.addRow(new Object[]{u.getId(), u.getUsername()});

        }

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
                    r.getCelular(),
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

    private static void limparCamposResponsavel(JtextFieldSomenteLetras txtNome, JFormattedTextField txtCPF,
                                                JFormattedTextField txtData, JtextFieldSomenteNumeros txtRG,
                                                JtextFieldSomenteLetras txtCivil, JFormattedTextField txtCel,
                                                JtextFieldLimitado txtEmail, JtextFieldSomenteLetras txtProf,
                                                JtextFieldLimitado txtTrab, JtextFieldLimitado txtEnder,
                                                JtextFieldSomenteLetras txtCid, JComboBox cb1, JFormattedTextField txtCep,
                                                JTextField txtId) {

        txtNome.setText("");
        txtCPF.setValue(null);
        txtData.setValue(null);
        txtRG.setText("");
        txtCivil.setText("");
        txtCel.setValue(null);
        txtEmail.setText("");
        txtProf.setText("");
        txtTrab.setText("");
        txtEnder.setText("");
        txtCid.setText("");
        if (cb1.getItemCount() > 0) cb1.setSelectedIndex(0);
        txtCep.setValue(null);
        if(txtId != null) txtId.setText("");

    }


    private static void limparCamposAluno(JtextFieldSomenteLetras txtNome, JFormattedTextField txtCPF,
                                          JFormattedTextField txtData, JComboBox cb1, JComboBox cb2,
                                          JtextFieldSomenteNumeros txtRG, JtextFieldSomenteLetras txtCivil,
                                          JFormattedTextField txtCel, JtextFieldLimitado txtEmail, JTextField txtId) {

        txtNome.setText("");
        txtCPF.setValue(null);
        txtData.setValue(null);
        if (cb1.getItemCount() > 0) cb1.setSelectedIndex(0);
        if (cb2.getItemCount() > 0) cb2.setSelectedIndex(0);
        txtRG.setText("");
        txtCivil.setText("");
        txtCel.setValue(null);
        txtEmail.setText("");

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
            pw.println("Nome;CPF;Data Nascimento;Responsável 1;Responsável 2;RG;Estado Civil;Celular;E-Mail");

            for (Aluno a : alunoDao.listar()) {

                Responsavel r1 = respDao.buscarPorId(a.getIdResponsavel1());
                String nomeR1 = (r1 != null) ? r1.getNome() : "Não encontrado";

                String nomeR2 = "Nenhum";
                if (a.getIdResponsavel2() > 0) {

                    Responsavel r2 = respDao.buscarPorId(a.getIdResponsavel2());
                    if (r2 != null) nomeR2 = r2.getNome();

                }

                pw.printf("%s;%s;%s;%s;%s;%s;%s;%s;%s\n", a.getNome(), a.getCPF(), a.getDataNascimento(), nomeR1, nomeR2,
                        a.getRG(), a.getEstCivil(), a.getCelular(), a.getEmail());

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

            pw.println("Nome;CPF;Data Nascimento;RG;Estado Civil;Número de Celular;E-Mail;Profissão;Local de Trabalho;" +
                    "Endereço;Cidade;Estado;CEP;Alunos Vinculados");

            for (Responsavel r : respDao.listar()) {

                List<String> alunos = alunoDao.buscarNomesAlunosPorResponsavel(r.getId());
                String nomesAlunos = String.join(", ", alunos);
                if (nomesAlunos.isEmpty()) nomesAlunos = "Nenhum";

                pw.printf("%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s\n", r.getNome(), r.getCPF(), r.getDataNascimento(), r.getRG(),
                        r.getEstCivil(), r.getCelular(), r.getEmail(), r.getProfissao(), r.getLocTrabalho(), r.getEndereco(),
                        r.getCidade(), r.getEstado(), r.getCep(), nomesAlunos);

            }

        }

    }

    public static final class JtextFieldSomenteNumeros extends JTextField {

        private int maximoCaracteres = -1;


        public JtextFieldSomenteNumeros(int columns, int maximo) {

            super(columns);
            this.maximoCaracteres = maximo;
            configurarEvento();

        }

        private void configurarEvento() {

            addKeyListener(new KeyAdapter() {

                @Override
                public void keyTyped(KeyEvent evt) {

                    char c = evt.getKeyChar();

                    if (!Character.isDigit(c) && c != '.' && c != '-') {

                        evt.consume();
                        return;

                    }

                    if (maximoCaracteres != -1 && getText().length() >= maximoCaracteres) {

                        evt.consume();

                    }

                }

            });

        }

        public int getMaximoCaracteres() { return maximoCaracteres; }

        public void setMaximoCaracteres(int maximoCaracteres) {

            this.maximoCaracteres = maximoCaracteres;

        }

    }

    public static final class JtextFieldSomenteLetras extends JTextField {

        private int maximoCaracteres = -1;

        public JtextFieldSomenteLetras(int columns, int maximo) {

            super(columns);
            this.maximoCaracteres = maximo;
            configurarEvento();

        }

        private void configurarEvento() {

            addKeyListener(new KeyAdapter() {

                @Override
                public void keyTyped(KeyEvent evt) {

                    char c = evt.getKeyChar();

                    if (!Character.isLetter(c) && !Character.isSpaceChar(c)) {

                        evt.consume();
                        return;

                    }

                    if (maximoCaracteres != -1 && getText().length() >= maximoCaracteres) {

                        evt.consume();

                    }

                }

            });

        }

        public int getMaximoCaracteres() { return maximoCaracteres; }

        public void setMaximoCaracteres(int maximo) {

            this.maximoCaracteres = maximo;

        }

    }

    public static final class JtextFieldLimitado extends JTextField {

        private int maximo;

        public JtextFieldLimitado(int columns, int maximo) {

            super(columns);
            this.maximo = maximo;
            addKeyListener(new java.awt.event.KeyAdapter() {

                @Override
                public void keyTyped(java.awt.event.KeyEvent evt) {

                    if (getText().length() >= maximo) evt.consume();

                }

            });

        }

    }

    private static void abrirFichaResponsavel(int id, ResponsavelDAO respDao, AlunoDAO alunoDao) {

        Responsavel r = respDao.buscarPorId(id);
        if (r == null) return;

        List<String> listaAlunos = alunoDao.buscarNomesAlunosPorResponsavel(id);
        String nomesAlunos = String.join(", ", listaAlunos);
        if (nomesAlunos.isEmpty()) nomesAlunos = "Nenhum aluno vinculado";

        JDialog ficha = new JDialog((Frame) null, "Ficha do Responsável: " + r.getNome(), true);
        ficha.setSize(550, 650);
        ficha.setLocationRelativeTo(null);
        ficha.setResizable(false);

        JPanel painelFicha = new JPanel(new GridBagLayout());
        painelFicha.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        painelFicha.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lbTitulo = new JLabel("INFORMAÇÕES DO RESPONSÁVEL", SwingConstants.CENTER);
        lbTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 40, 0);
        painelFicha.add(lbTitulo, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 5, 8, 5);
        int linha = 1;

        adicionarCampoFicha(painelFicha, "Nome:", r.getNome(), gbc, linha++);
        adicionarCampoFicha(painelFicha, "CPF:", r.getCPF(), gbc, linha++);
        adicionarCampoFicha(painelFicha, "RG:", r.getRG(), gbc, linha++);
        adicionarCampoFicha(painelFicha, "Data de Nascimento:", r.getDataNascimento(), gbc, linha++);
        adicionarCampoFicha(painelFicha, "Estado Civil:", r.getEstCivil(), gbc, linha++);
        adicionarCampoFicha(painelFicha, "Celular:", r.getCelular(), gbc, linha++);
        adicionarCampoFicha(painelFicha, "E-mail:", r.getEmail(), gbc, linha++);
        adicionarCampoFicha(painelFicha, "Profissão:", r.getProfissao(), gbc, linha++);
        adicionarCampoFicha(painelFicha, "Local de Trabalho:", r.getLocTrabalho(), gbc, linha++);
        adicionarCampoFicha(painelFicha, "Endereço:", r.getEndereco(), gbc, linha++);
        adicionarCampoFicha(painelFicha, "Cidade/Estado:", r.getCidade() + " - " + r.getEstado(), gbc, linha++);
        adicionarCampoFicha(painelFicha, "CEP:", r.getCep(), gbc, linha++);
        adicionarCampoFicha(painelFicha, "Alunos Vinculados:", nomesAlunos, gbc, linha++);

        JScrollPane scroll = new JScrollPane(painelFicha);
        scroll.setBorder(null);

        ficha.add(scroll);
        ficha.setVisible(true);

    }

    private static void abrirFichaAluno(int id, ResponsavelDAO respDao, AlunoDAO alunoDao) {

        Aluno a = alunoDao.buscarPorId(id);
        if (a == null) return;

        Responsavel r1 = respDao.buscarPorId(a.getIdResponsavel1());
        String nomeResp1 = (r1 != null) ? r1.getNome() : "Não encontrado";

        String nomeResp2 = "Nenhum";
        if (a.getIdResponsavel2() > 0) {

            Responsavel r2 = respDao.buscarPorId(a.getIdResponsavel2());
            if (r2 != null) nomeResp2 = r2.getNome();

        }

        JDialog ficha = new JDialog((Frame) null, "Ficha do Aluno: " + a.getNome(), true);
        ficha.setSize(550, 650);
        ficha.setLocationRelativeTo(null);
        ficha.setResizable(false);

        JPanel painelFicha = new JPanel(new GridBagLayout());
        painelFicha.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        painelFicha.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lbTitulo = new JLabel("INFORMAÇÕES DO ALUNO", SwingConstants.CENTER);
        lbTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 40, 0);
        painelFicha.add(lbTitulo, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 5, 8, 5);
        int linha = 1;

        adicionarCampoFicha(painelFicha, "Nome:", a.getNome(), gbc, linha++);
        adicionarCampoFicha(painelFicha, "CPF:", a.getCPF(), gbc, linha++);
        adicionarCampoFicha(painelFicha, "RG:", a.getRG(), gbc, linha++);
        adicionarCampoFicha(painelFicha, "Data de Nascimento:", a.getDataNascimento(), gbc, linha++);
        adicionarCampoFicha(painelFicha, "1º Responsável:", nomeResp1, gbc, linha++);
        adicionarCampoFicha(painelFicha, "2º Responsável:", nomeResp2, gbc, linha++);
        adicionarCampoFicha(painelFicha, "Estado Civil:", a.getEstCivil(), gbc, linha++);
        adicionarCampoFicha(painelFicha, "Celular:", a.getCelular(), gbc, linha++);
        adicionarCampoFicha(painelFicha, "E-mail:", a.getEmail(), gbc, linha++);

        JScrollPane scroll = new JScrollPane(painelFicha);
        scroll.setBorder(null);

        ficha.add(scroll);
        ficha.setVisible(true);

    }

    private static void adicionarCampoFicha(JPanel p, String label, String valor, GridBagConstraints gbc, int linha) {

        gbc.gridy = linha;

        gbc.gridx = 0; gbc.weightx = 0.3;
        p.add(new JLabel("<html><b>" + label + "</b></html>"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;

        JTextField campo = new JTextField(valor);
        campo.setEditable(false);
        campo.setFont(new Font("Arial", Font.PLAIN, 13));

        p.add(campo, gbc);

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