package com.projeto;

public class Responsavel {

    private int id;
    private String nome;
    private String CPF;
    private String dataNascimento; // DD/MM/AAAA para a tela e YYYY/MM/DD para o banco
    private String RG;
    private String estCivil;
    private String celular;

    public Responsavel() {

    }

    public Responsavel(String nome, String CPF, String dataNascimento, String RG, String estCivil, String celular) {

        this.nome = nome;
        this.CPF = CPF;
        this.dataNascimento = dataNascimento;
        this.RG = RG;
        this.estCivil = estCivil;
        this.celular = celular;

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

    public String getEstCivil() { return estCivil; }

    public void setEstCivil(String estCivil) {

        this.estCivil = estCivil;

    }

    public String getCelular() { return celular; }

    public void setCelular(String celular) {

        this.celular = celular;

    }

}