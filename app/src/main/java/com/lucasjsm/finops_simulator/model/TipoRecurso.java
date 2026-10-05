package com.lucasjsm.finops_simulator.model;

/**
 * Model: TipoRecurso
 * Catálogo de tipos de recursos de nuvem, organizados por provedor.
 * Exemplos: EC2 (AWS), S3 (AWS), BigQuery (GCP), Apache Spark (GCP), etc.
 */
public class TipoRecurso {

    private long id;
    private String nome;
    private String provedor;       // AWS, GCP, Azure, OCI
    private String unidade;        // hora, GB, instância, etc.
    private double custoUnitario;
    private String descricao;

    public TipoRecurso() {
    }

    public TipoRecurso(String nome, String provedor, String unidade, double custoUnitario, String descricao) {
        this.nome = nome;
        this.provedor = provedor;
        this.unidade = unidade;
        this.custoUnitario = custoUnitario;
        this.descricao = descricao;
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
        return nome + " (" + provedor + ") - US$ " + String.format("%.4f", custoUnitario) + "/" + unidade;
    }
}
