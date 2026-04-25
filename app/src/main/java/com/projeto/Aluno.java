package com.projeto;

public class Aluno {

    private int id;
    private String nome;
    private String CPF;
    private String dataNascimento; // DD/MM/AAAA para a tela e YYYY/MM/DD para o banco
    private int idResponsavel1;
    private int idResponsavel2;

    public Aluno() {

    }

    public Aluno(String nome, String CPF, String dataNascimento, int idResp1, int idResp2) {

        this.nome = nome;
        this.CPF = CPF;
        this.dataNascimento = dataNascimento;
        this.idResponsavel1 = idResp1;
        this.idResponsavel2 = idResp2;

    }

    //Getters e Setters
    public int getId() {

        return id;

    }
    public void setId(int id) {

        this.id = id;

    }

    public String getNome() {

        return nome;

    }
    public void setNome(String nome) {

        this.nome = nome;

    }

    public String getCPF() {

        return CPF;

    }
    public void setCPF(String CPF) {

        this.CPF = CPF;

    }

    public String getDataNascimento() {

        return dataNascimento;

    }
    public void setDataNascimento(String dataNascimento) {

        this.dataNascimento = dataNascimento;

    }

    public int getIdResponsavel1() {

        return idResponsavel1;

    }
    public void setIdResponsavel1(int idResp1) {

        this.idResponsavel1 = idResp1;

    }

    public int getIdResponsavel2() {

        return idResponsavel2;

    }
    public void setIdResponsavel2(int idResp2) {

        this.idResponsavel2 = idResp2;

    }

}