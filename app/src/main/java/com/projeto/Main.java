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

    private static JComboBox<ResponsavelComboItem> comboResp1;
    private static JComboBox<ResponsavelComboItem> comboResp2;

    public static void main(String[] args) {

        Conexao.inicializarBanco();

        SwingUtilities.invokeLater(() -> {

            AlunoDAO alunoDao = new AlunoDAO();
            ResponsavelDAO respDao = new ResponsavelDAO();

            JFrame frame = new JFrame("SCA - Sistema de Cadastro Asdown");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setPreferredSize(new Dimension(800, 600));

            JTabbedPane menuAbas = new JTabbedPane();

            String[] colunasAl = {"ID", "Nome", "Data de Nascimento", "1º Responsável", "2º Responsável"};
            DefaultTableModel modeloAl = new DefaultTableModel(colunasAl, 0);
            String[] colunasResp = {"ID", "Nome", "CPF", "Data de Nascimento"};
            DefaultTableModel modeloResp = new DefaultTableModel(colunasResp, 0);
            String[] colunasReduzidas = {"ID", "Nome"};
            DefaultTableModel modeloReduzido = new DefaultTableModel(colunasReduzidas, 0);


            menuAbas.addTab("Cadastrar", criarPainelCadastro(alunoDao, respDao));
            menuAbas.addTab("Listar", criarPainelListagem(modeloAl, modeloResp));
            menuAbas.addTab("Editar/Excluir", criarPainelEditar(alunoDao, respDao, modeloReduzido));

            menuAbas.addChangeListener(e -> {
                int aba = menuAbas.getSelectedIndex();

                if (aba == 0) {

                    if (comboResp1 != null && comboResp2 != null) {

                        atualizarCombosResponsaveis(respDao, comboResp1, comboResp2);
                        System.out.println("Combos de responsáveis atualizados!");

                    }

                } else if (aba == 1) {

                    preencherTabAluno(alunoDao,respDao, modeloAl);
                    preencherTabResp(respDao, modeloResp);

                    System.out.println("Tabela atualizada!");

                } else if (aba == 2) {

                    preencherTabAlunoReduzida(alunoDao, modeloReduzido);
                    preencherTabRespReduzida(respDao, modeloResp);

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

                atualizarCombosResponsaveis(respDao, comboResp1, comboResp2);
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
        JFormattedTextField txtData = null;

        try {

            MaskFormatter mascara = new MaskFormatter("##/##/####");
            mascara.setPlaceholderCharacter('_');
            txtData = new JFormattedTextField(mascara);
            txtData.setColumns(6);

        } catch (Exception e) { e.printStackTrace(); }

        final JFormattedTextField txtDataFinal = txtData;

        comboResp1 = new JComboBox<>();
        comboResp1.setBackground(Color.WHITE);
        comboResp2 = new JComboBox<>();
        comboResp2.setBackground(Color.WHITE);
        comboResp2.setName("Opcional");

        atualizarCombosResponsaveis(respDao, comboResp1, comboResp2);

        JButton btnSalvar = new JButton("Salvar");

        btnSalvar.addActionListener(e -> {

            String nome = txtNome.getText();
            String dataNasc = txtDataFinal.getText().replace("_", "").trim();

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

            ResponsavelComboItem resp1 = (ResponsavelComboItem) comboResp1.getSelectedItem();
            ResponsavelComboItem resp2 = (ResponsavelComboItem) comboResp2.getSelectedItem();

            if (resp1 == null) {

                JOptionPane.showMessageDialog(painel, "Todo aluno deve ter o primeiro responsável!");
                return;

            }

            int id1 = resp1.getId();
            int id2 = (resp2 != null) ? resp2.getId() : 0;

                try {

                    Aluno aluno = new Aluno(nome, dataNasc, id1, id2);
                    alunoDao.inserir(aluno);

                    txtNome.setText("");
                    txtDataFinal.setValue(null);

                    JOptionPane.showMessageDialog(painel, "Aluno salvo com sucesso!");

                } catch (SQLException ex) {

                    JOptionPane.showMessageDialog(painel, "Erro no banco de dados:" + ex.getMessage());

                } catch (Exception ex) {

                JOptionPane.showMessageDialog(painel, "Erro inesperado: " + ex.getMessage());

                }

        });

        painel.add(new JLabel("Nome: ")); painel.add(txtNome);
        painel.add(new JLabel("Resp. 1:")); painel.add(comboResp1);
        painel.add(new JLabel("Resp. 2:")); painel.add(comboResp2);
        painel.add(new JLabel("Nascimento: ")); painel.add(txtData);
        painel.add(btnSalvar);

        return painel;

    }

    private static JPanel criarFormularioResp(ResponsavelDAO respDao) {

        JPanel painel = new JPanel(new FlowLayout());

        JTextField txtNome = new JTextField(20);
        JFormattedTextField txtCPF = null;
        JFormattedTextField txtData = null;

        try {

            MaskFormatter mascara = new MaskFormatter("##/##/####");
            mascara.setPlaceholderCharacter('_');
            txtData = new JFormattedTextField(mascara);
            txtData.setColumns(6);

        } catch (Exception e) { e.printStackTrace(); }

        try {

            MaskFormatter mascara = new MaskFormatter("###.###.###-##");
            mascara.setPlaceholderCharacter('_');
            txtCPF = new JFormattedTextField(mascara);
            txtCPF.setColumns(9);

        } catch (Exception e) { e.printStackTrace(); }

        final JFormattedTextField txtDataFinal = txtData;
        final JFormattedTextField txtCPFFinal = txtCPF;

        JButton btnSalvar = new JButton("Salvar");

        btnSalvar.addActionListener(e -> {

            String nome = txtNome.getText();
            String cpf = txtCPFFinal.getText().replace("_", "").trim();
            String dataNasc = txtDataFinal.getText().replace("_", "").trim();

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

            if(cpf.length() < 14) {
                JOptionPane.showMessageDialog(painel, "Preencha o CPF completo!");
                return;
            }

                try {

                    Responsavel resp = new Responsavel(nome, cpf, dataNasc);
                    respDao.inserir(resp);

                    txtNome.setText("");
                    txtDataFinal.setValue(null);
                    txtCPFFinal.setValue(null);

                    JOptionPane.showMessageDialog(painel, "Responsável salvo com sucesso!");

                } catch (SQLException ex) {

                    if (ex.getMessage().contains("UNIQUE constraint failed: responsavel.cpf")) {

                        JOptionPane.showMessageDialog(painel,
                                "Já existe um responsável cadastrado com este CPF!",
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
        painel.add(new JLabel("Nascimento: ")); painel.add(txtData);
        painel.add(new JLabel("CPF: ")); painel.add(txtCPF);
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

    private static JPanel criarPainelEditar(AlunoDAO alunoDao, ResponsavelDAO respDao, DefaultTableModel modeloReduzido) {

        JPanel painelPrincipal = new JPanel(new BorderLayout());

        JPanel painelSeletor = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] tipos = {"Alunos", "Responsáveis"};
        JComboBox<String> comboTipo = new JComboBox<>(tipos);
        comboTipo.setBackground(Color.WHITE);
        painelSeletor.add(new JLabel("O que você deseja editar?"));
        painelSeletor.add(comboTipo);

        JPanel containerCards = new JPanel(new CardLayout());

        JPanel listAluno = criarAlEditar(alunoDao, modeloReduzido);
        JPanel listResp = criarRespEditar(respDao, modeloReduzido);

        containerCards.add(listAluno, "Alunos");
        containerCards.add(listResp, "Responsáveis");

        comboTipo.addActionListener(e -> {

            CardLayout cl = (CardLayout) (containerCards.getLayout());
            cl.show(containerCards, (String) comboTipo.getSelectedItem());

            String selecao = (String) comboTipo.getSelectedItem();
            cl.show(containerCards, selecao);

            if (selecao.equals("Alunos")) {

                preencherTabAlunoReduzida(alunoDao, modeloReduzido);

            }else {

                preencherTabRespReduzida(respDao, modeloReduzido);

            }

            containerCards.revalidate();
            containerCards.repaint();

        });

        painelPrincipal.add(painelSeletor, BorderLayout.NORTH);
        painelPrincipal.add(containerCards, BorderLayout.CENTER);

        return painelPrincipal;

    }

    private static JPanel criarAlEditar(AlunoDAO alunoDao, DefaultTableModel modeloReduzido) {

        JPanel painelPrincipal = new JPanel(new GridLayout(1, 2, 10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel painelEditor = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtId = new JTextField(2);
        txtId.setEditable(false);
        JTextField txtNome = new JTextField(20);

        JFormattedTextField txtData = null;
        try {

            MaskFormatter m = new MaskFormatter("##/##/####");
            m.setPlaceholderCharacter('_');
            txtData = new JFormattedTextField(m);
            txtData.setColumns(6);

        } catch (Exception e)  { e.printStackTrace(); }

        final JFormattedTextField txtDataFinal = txtData;

        JButton btnEditar = new JButton("Salvar Alterações");
        JButton btnExcluir = new JButton("Excluir Aluno");
        btnExcluir.setBackground(new Color(255, 150, 150));

        painelEditor.add(new JLabel("ID:")); painelEditor.add(txtId);
        painelEditor.add(new JLabel("Nome:")); painelEditor.add(txtNome);
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

                Aluno a = alunoDao.buscarPorId(id);
                if (a != null) {

                    txtId.setText(String.valueOf(a.getId()));
                    txtNome.setText(a.getNome());
                    txtDataFinal.setText(a.getDataNascimento());

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
            String novaData = txtDataFinal.getText().replace("_", "").trim();

            if (novoNome.isEmpty()) {

                JOptionPane.showMessageDialog(painelPrincipal, "O nome não pode estar vazio!");
                return;

            }

            if (novaData.length() < 10 || !isDataValida(novaData)) {

                JOptionPane.showMessageDialog(painelPrincipal, "Data inválida!");
                return;

            }

            Aluno alunoEditado = new Aluno(novoNome, novaData, 1, 2);
            alunoEditado.setId(Integer.parseInt(idTexto));

            alunoDao.atualizar(alunoEditado);

            preencherTabAlunoReduzida(alunoDao, modeloReduzido);

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

                alunoDao.deletar(Integer.parseInt(txtId.getText()));
                preencherTabAlunoReduzida(alunoDao, modeloReduzido);
                txtId.setText(""); txtNome.setText(""); txtDataFinal.setValue(null);
                JOptionPane.showMessageDialog(painelPrincipal, "Aluno deletado com sucesso!");

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

        JFormattedTextField txtData = null;
        JFormattedTextField txtCpf = null;

        try {

            MaskFormatter m = new MaskFormatter("##/##/####");
            m.setPlaceholderCharacter('_');
            txtData = new JFormattedTextField(m);
            txtData.setColumns(6);

        } catch (Exception e)  { e.printStackTrace(); }

        try {

            MaskFormatter mascara = new MaskFormatter("###.###.###-##");
            mascara.setPlaceholderCharacter('_');
            txtCpf = new JFormattedTextField(mascara);
            txtCpf.setColumns(9);

        } catch (Exception e) { e.printStackTrace(); }

        final JFormattedTextField txtDataFinal = txtData;
        final JFormattedTextField txtCpfFinal = txtCpf;

        JButton btnEditar = new JButton("Salvar Alterações");
        JButton btnExcluir = new JButton("Excluir Responsável");
        btnExcluir.setBackground(new Color(255, 150, 150));

        painelEditor.add(new JLabel("ID:")); painelEditor.add(txtId);
        painelEditor.add(new JLabel("Nome:")); painelEditor.add(txtNome);
        painelEditor.add(new JLabel("CPF:")); painelEditor.add(txtCpfFinal);
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
                    txtCpfFinal.setText(r.getCpf());
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
            String novaData = txtDataFinal.getText().replace("_", "").trim();
            String novoCpf = txtCpfFinal.getText().replace("_", "").trim();

            if (novoNome.isEmpty()) {

                JOptionPane.showMessageDialog(painelPrincipal, "O nome não pode estar vazio!");
                return;

            }

            if (novaData.length() < 10 || !isDataValida(novaData)) {

                JOptionPane.showMessageDialog(painelPrincipal, "Data inválida!");
                return;

            }

            if (novoCpf.length() < 14) {

                JOptionPane.showMessageDialog(painelPrincipal, "Cpf inválido!");
                return;

            }

            Responsavel respEditado = new Responsavel(novoNome, novaData, novoCpf);
            respEditado.setId(Integer.parseInt(idTexto));

            respDao.atualizar(respEditado);

            preencherTabRespReduzida(respDao, modeloReduzido);

            JOptionPane.showMessageDialog(painelPrincipal, "Dados atualizados com sucesso!");

        });

        btnExcluir.addActionListener(e -> {

            if (txtId.getText().isEmpty()) {

                JOptionPane.showMessageDialog(null, "Selecione um responsável na tabela para excluir.");
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

                respDao.deletar(Integer.parseInt(txtId.getText()));
                preencherTabRespReduzida(respDao, modeloReduzido);
                txtId.setText(""); txtNome.setText(""); txtCpfFinal.setValue(null); txtDataFinal.setValue(null);
                JOptionPane.showMessageDialog(painelPrincipal, "Responsável deletado com sucesso!");

            }

        });

        painelPrincipal.add(painelEditor);
        painelPrincipal.add(new JScrollPane(tabela));

        preencherTabRespReduzida(respDao, modeloReduzido);

        return painelPrincipal;

    }

    private static void preencherTabAluno(AlunoDAO dao, ResponsavelDAO respDao, DefaultTableModel modelo) {

        modelo.setRowCount(0);

        for (Aluno a : dao.listar()) {

            Responsavel r1 = respDao.buscarPorId(a.getIdResponsavel1());
            String nomeResp1 = (r1 != null) ? r1.getNome() : "Não encontrado";

            String nomeResp2 = "Nenhum";
            if (a.getIdResponsavel2() > 0) {

                Responsavel r2 = respDao.buscarPorId(a.getIdResponsavel2());
                if (r2 != null) nomeResp2 = r2.getNome();

            }

            Object[] linha = {
                    a.getId(),
                    a.getNome(),
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

    private static void preencherTabResp(ResponsavelDAO dao, DefaultTableModel modelo) {

        modelo.setRowCount(0);

        for (Responsavel r : dao.listar()) {

            Object[] linha = {
                    r.getId(),
                    r.getNome(),
                    r.getCpf(),
                    r.getDataNascimento()

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