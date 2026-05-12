package com.projeto;

public class Responsavel {

    private int id;
    private String nome, CPF, dataNascimento, RG, estCivil, celular, email, profissao, locTrabalho, endereco, cidade, estado, cep;

    public Responsavel() {

    }

    public Responsavel(String nome, String CPF, String dataNascimento, String RG, String estCivil, String celular,
                       String email, String profissao, String locTrabalho, String endereco, String cidade, String estado, String cep) {

        this.nome = nome;
        this.CPF = CPF;
        this.dataNascimento = dataNascimento;
        this.RG = RG;
        this.estCivil = estCivil;
        this.celular = celular;
        this.email = email;
        this.profissao = profissao;
        this.locTrabalho = locTrabalho;
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

    public String getProfissao() { return profissao; }

    public void setProfissao(String profissao) {

        this.profissao = profissao;

    }

    public String getLocTrabalho() { return locTrabalho; }

    public void setLocTrabalho(String locTrabalho) {

        this.locTrabalho = locTrabalho;

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