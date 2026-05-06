package com.projeto;

public class Responsavel {

    private int id;
    private String nome;
    private String CPF;
    private String dataNascimento; // DD/MM/AAAA para a tela e YYYY/MM/DD para o banco
    private String RG;

    public Responsavel() {

    }

    public Responsavel(String nome, String CPF, String dataNascimento, String RG) {

        this.nome = nome;
        this.CPF = CPF;
        this.dataNascimento = dataNascimento;
        this.RG = RG;

    }

    public int getId() { return id; }

    public void setId(int id) {

        this.id = id;

    }

    public String getNome() { return nome; }

    public void setNome(String nome) {

        this.nome = nome;

    }

    public String getCPF() { return CPF; }

    public void setCPF(String CPF) {

        this.CPF = CPF;

    }

    public String getDataNascimento() { return dataNascimento; }

    public void setDataNascimento(String dataNascimento) {

        this.dataNascimento = dataNascimento;

    }

    public String getRG() { return RG; }

    public void setRG(String RG) {

        this.RG = RG;

    }

}