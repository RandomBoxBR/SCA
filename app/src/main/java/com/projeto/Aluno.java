package com.projeto;

public class Aluno {

    private int id, idResponsavel1, idResponsavel2;
    private String nome, CPF, dataNascimento, RG, estCivil, celular, email, endereco, cidade, estado, cep, escola, escAno,
            escFone, temIrmaos, irmaos, admissao, desligamento, diagnostico, parente, parentesco, beneficio, terapia,
            atendimento, instituicoes, atividades;

    public Aluno() {

    }

    public Aluno(String nome, String CPF, String dataNascimento, int idResp1, int idResp2, String rg, String estCivil,
                 String celular, String email, String endereco, String cidade, String estado, String cep, String escola,
                 String escAno, String escFone, String temIrmaos, String irmaos, String admissao, String desligamento,
                 String diagnostico, String parente, String parentesco, String beneficio, String terapia, String atendimento,
                 String instituicoes, String atividades) {

        this.nome = nome; this.CPF = CPF; this.dataNascimento = dataNascimento; this.idResponsavel1 = idResp1;
        this.idResponsavel2 = idResp2; this.RG = rg; this.estCivil = estCivil; this.celular = celular; this.email = email;
        this.endereco = endereco; this.cidade = cidade; this.estado = estado; this.cep = cep; this.escola = escola;
        this.escAno = escAno; this.escFone = escFone; this.temIrmaos = temIrmaos; this.irmaos = irmaos;
        this.admissao = admissao; this.desligamento = desligamento; this.diagnostico = diagnostico; this.parente = parente;
        this.parentesco = parentesco; this.beneficio = beneficio; this.terapia = terapia; this.atendimento = atendimento;
        this.instituicoes = instituicoes; this.atividades = atividades;

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

    public String getEscola() { return escola; }

    public void setEscola(String escola) {

        this.escola = escola;

    }

    public String getEscAno() { return escAno; }

    public void setEscAno(String escAno) {

        this.escAno = escAno;

    }

    public String getEscFone() { return escFone; }

    public void setEscFone(String escFone) {

        this.escFone = escFone;

    }

    public String getTemIrmaos() { return temIrmaos; }

    public void setTemIrmaos(String temIrmaos) {

        this.temIrmaos = temIrmaos;

    }

    public String getIrmaos() { return irmaos; }

    public void setIrmaos(String irmaos) {

        this.irmaos = irmaos;

    }

    public String getAdmissao() { return admissao; }

    public void setAdmissao(String admissao) {

        this.admissao = admissao;

    }

    public String getDesligamento() { return desligamento; }

    public void setDesligamento(String desligamento) {

        this.desligamento = desligamento;

    }

    public String getDiagnostico() { return diagnostico; }

    public void setDiagnostico(String diagnostico) {

        this.diagnostico = diagnostico;

    }

    public String getParente() { return parente; }

    public void setParente(String parente) {

        this.parente = parente;

    }

    public String getParentesco() { return parentesco; }

    public void setParentesco(String parentesco) {

        this.parentesco = parentesco;

    }

    public String getBeneficio() { return beneficio; }

    public void setBeneficio(String beneficio) {

        this.beneficio = beneficio;

    }

    public String getTerapia() { return terapia; }

    public void setTerapia(String terapia) {

        this.terapia = terapia;

    }

    public String getInstituicoes() { return instituicoes; }

    public void setInstituicoes(String instituicoes) {

        this.instituicoes = instituicoes;

    }

    public String getAtendimento() { return atendimento; }

    public void setAtendimento(String atendimento) {

        this.atendimento = atendimento;

    }

    public String getAtividades() { return atividades; }

    public void setAtividades(String atividades) {

        this.atividades = atividades;

    }

}