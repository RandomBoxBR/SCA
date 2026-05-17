package com.projeto;

public class Aluno {

    private int id, idResponsavel1, idResponsavel2;
    private String nome, CPF, dataNascimento, RG, estCivil, celular, email, endereco, cidade, estado, cep;

    public Aluno() {

    }

    public Aluno(String nome, String CPF, String dataNascimento, int idResp1, int idResp2, String rg, String estCivil,
                 String celular, String email, String endereco, String cidade, String estado, String cep) {

        this.nome = nome;
        this.CPF = CPF;
        this.dataNascimento = dataNascimento;
        this.idResponsavel1 = idResp1;
        this.idResponsavel2 = idResp2;
        this.RG = rg;
        this.estCivil = estCivil;
        this.celular = celular;
        this.email = email;
        this.endereco = endereco;
        this.cidade = cidade;
        this.estado = estado;
        this.cep = cep;

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

    public int getIdResponsavel1() { return idResponsavel1; }

    public void setIdResponsavel1(int idResp1) {

        this.idResponsavel1 = idResp1;

    }

    public int getIdResponsavel2() { return idResponsavel2; }

    public void setIdResponsavel2(int idResp2) {

        this.idResponsavel2 = idResp2;

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

    public String getEmail() { return email; }

    public void setEmail(String email) {

        this.email = email;

    }

    public String getEndereco() { return endereco; }

    public void setEndereco(String endereco) {

        this.endereco = endereco;

    }

    public String getCidade() { return cidade; }

    public void setCidade(String cidade) {

        this.cidade = cidade;

    }

    public String getEstado() { return estado; }

    public void setEstado(String estado) {

        this.estado = estado;

    }

    public String getCep() { return cep; }

    public void setCep(String cep) {

        this.cep = cep;

    }

}