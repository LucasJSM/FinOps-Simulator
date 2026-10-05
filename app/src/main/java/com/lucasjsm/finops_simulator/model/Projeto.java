package com.lucasjsm.finops_simulator.model;

/**
 * Model: Projeto
 * Representa um projeto de simulação de custos em nuvem.
 * O provedor (AWS, GCP, Azure, OCI) é definido na criação e não pode ser alterado.
 */
public class Projeto {

    private long id;
    private String nome;
    private String descricao;
    private String provedor;          // AWS, GCP, Azure, OCI
    private double orcamentoMaximo;
    private String dataCriacao;
    private double custoTotal;        // calculado (não persistido)

    public Projeto() {
    }

    public Projeto(String nome, String descricao, String provedor, double orcamentoMaximo, String dataCriacao) {
        this.nome = nome;
        this.descricao = descricao;
        this.provedor = provedor;
        this.orcamentoMaximo = orcamentoMaximo;
        this.dataCriacao = dataCriacao;
    }

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
        return nome + " (" + provedor + ")";
    }
}
