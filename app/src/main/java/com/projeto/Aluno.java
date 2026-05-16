package com.projeto;

public class Aluno {

    private int id, idResponsavel1, idResponsavel2;
    private String nome, CPF, dataNascimento, RG, estCivil;

    public Aluno() {

    }

    public Aluno(String nome, String CPF, String dataNascimento, String rg, String estCivil, int idResp1, int idResp2) {

        this.nome = nome;
        this.CPF = CPF;
        this.dataNascimento = dataNascimento;
        this.RG = rg;
        this.estCivil = estCivil;
        this.idResponsavel1 = idResp1;
        this.idResponsavel2 = idResp2;

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

    public int getIdResponsavel1() { return idResponsavel1; }

    public void setIdResponsavel1(int idResp1) {

        this.idResponsavel1 = idResp1;

    }

    public int getIdResponsavel2() { return idResponsavel2; }

    public void setIdResponsavel2(int idResp2) {

        this.idResponsavel2 = idResp2;

    }

    public String getEstCivil() { return estCivil; }

    public void setEstCivil(String estCivil) {

        this.estCivil = estCivil;

    }

}