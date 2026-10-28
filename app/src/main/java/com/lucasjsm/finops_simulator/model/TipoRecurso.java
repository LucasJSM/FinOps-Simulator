package com.lucasjsm.finops_simulator.model;

import java.util.Locale;

public class TipoRecurso {

    private long id;
    private String nome;
    private String provedor;       // AWS, GCP, Azure, OCI
    private String unidade;        // hora, GB, milhao_reqs
    private double custoUnitario;
    private String descricao;

    // Construtor padrão
    public TipoRecurso() {
        this.nome = "Novo Serviço";
        this.provedor = "AWS";
        this.unidade = "hora";
        this.custoUnitario = 0.0;
        this.descricao = "";
    }

    // Construtor sem ID
    public TipoRecurso(String nome, String provedor, String unidade, double custoUnitario, String descricao) {
        this.nome = nome;
        this.provedor = provedor;
        this.unidade = unidade;
        this.custoUnitario = custoUnitario;
        this.descricao = descricao;
    }

    // Construtor completo com ID
    public TipoRecurso(long id, String nome, String provedor, String unidade, double custoUnitario, String descricao) {
        this.id = id;
        this.nome = nome;
        this.provedor = provedor;
        this.unidade = unidade;
        this.custoUnitario = custoUnitario;
        this.descricao = descricao;
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

    public String getProvedor() {
        return provedor;
    }

    public void setProvedor(String provedor) {
        this.provedor = provedor;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public double getCustoUnitario() {
        return custoUnitario;
    }

    public void setCustoUnitario(double custoUnitario) {
        this.custoUnitario = custoUnitario;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return getNome() + " (" + getProvedor() + ") - R$ " + String.format(Locale.getDefault(), "%.3f", getCustoUnitario()) + "/" + getUnidade();
    }
}
