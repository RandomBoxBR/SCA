package com.projeto;

public class Aluno {

    private int id;
    private String nome;
    private int idade;

    public Aluno() {

    }

    public Aluno(String nome, int idade) {

        this.nome = nome;
        this.idade = idade;

    }

    //Getters e Setters
    public int getId() {

        return id;

    }
    public void setId(int id) {

        this.nome = nome;

    }

    public String getNome() {

        return nome;

    }
    public void setNome(String nome) {

        this.nome = nome;

    }

    public int getIdade() {

        return idade;

    }
    public void setIdade(int idade) {

        this.idade = idade;

    }

}
