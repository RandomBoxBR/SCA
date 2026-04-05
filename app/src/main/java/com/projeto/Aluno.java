package com.projeto;

public class Aluno {

    private int id;
    private String nome;
    private String dataNascimento; // DD/MM/AAAA para a tela e YYYY/MM/DD para o banco

    public Aluno() {

    }

    public Aluno(String nome, String dataNascimento) {

        this.nome = nome;
        this.dataNascimento = dataNascimento;

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

    public String getDataNascimento() {

        return dataNascimento;

    }
    public void setDataNascimento(String dataNascimento) {

        this.dataNascimento = dataNascimento;

    }

}