package com.lucasjsm.finops_simulator.model;

public class Projeto {

    private long id;
    private String nome;
    private String descricao;
    private String provedor;  // AWS, GCP, Azure, OCI
    private double orcamentoMaximo;
    private String dataCriacao;
    private double custoTotal;  // Calculado a partir dos recursos alocados

    // Construtor padrão
    public Projeto() {
        this.nome = "Novo Projeto";
        this.descricao = "";
        this.provedor = "AWS";
        this.orcamentoMaximo = 0.0;
        this.dataCriacao = "";
        this.custoTotal = 0.0;
    }

    // Construtor com parâmetros
    public Projeto(String nome, String descricao, String provedor, double orcamentoMaximo, String dataCriacao) {
        this.nome = nome;
        this.descricao = descricao;
        this.provedor = provedor;
        this.orcamentoMaximo = orcamentoMaximo;
        this.dataCriacao = dataCriacao;
        this.custoTotal = 0.0;
    }

    // Construtor completo com id
    public Projeto(long id, String nome, String descricao, String provedor, double orcamentoMaximo, String dataCriacao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.provedor = provedor;
        this.orcamentoMaximo = orcamentoMaximo;
        this.dataCriacao = dataCriacao;
        this.custoTotal = 0.0;
    }

    // Métodos Getters e Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getProvedor() {
        return provedor;
    }

    public void setProvedor(String provedor) {
        this.provedor = provedor;
    }

    public double getOrcamentoMaximo() {
        return orcamentoMaximo;
    }

    public void setOrcamentoMaximo(double orcamentoMaximo) {
        this.orcamentoMaximo = orcamentoMaximo;
    }

    public String getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(String dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public double getCustoTotal() {
        return custoTotal;
    }

    public void setCustoTotal(double custoTotal) {
        this.custoTotal = custoTotal;
    }

    @Override
    public String toString() {
        return getNome() + " (" + getProvedor() + ")";
    }
}
