package com.lucasjsm.finops_simulator.model;

/**
 * Model: RecursoAlocado
 * Recurso efetivamente alocado a um projeto.
 * custo_estimado = quantidade × custo_unitario × periodo
 */
public class RecursoAlocado {

    private long id;
    private long projetoId;
    private long tipoRecursoId;
    private double quantidade;
    private double periodo;
    private double custoEstimado;
    private String observacao;

    // Campos auxiliares para exibição (não persistidos diretamente)
    private String nomeTipo;
    private String provedor;
    private String unidade;
    private double custoUnitario;

    public RecursoAlocado() {
    }

    public RecursoAlocado(long projetoId, long tipoRecursoId, double quantidade, double periodo, String observacao) {
        this.projetoId = projetoId;
        this.tipoRecursoId = tipoRecursoId;
        this.quantidade = quantidade;
        this.periodo = periodo;
        this.observacao = observacao;
    }

    /**
     * Calcula o custo estimado com base na fórmula acadêmica:
     * custo_estimado = quantidade × custo_unitario × periodo
     */
    public void calcularCusto(double custoUnitario) {
        this.custoUnitario = custoUnitario;
        this.custoEstimado = this.quantidade * custoUnitario * this.periodo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProjetoId() {
        return projetoId;
    }

    public void setProjetoId(long projetoId) {
        this.projetoId = projetoId;
    }

    public long getTipoRecursoId() {
        return tipoRecursoId;
    }

    public void setTipoRecursoId(long tipoRecursoId) {
        this.tipoRecursoId = tipoRecursoId;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public double getPeriodo() {
        return periodo;
    }

    public void setPeriodo(double periodo) {
        this.periodo = periodo;
    }

    public double getCustoEstimado() {
        return custoEstimado;
    }

    public void setCustoEstimado(double custoEstimado) {
        this.custoEstimado = custoEstimado;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getNomeTipo() {
        return nomeTipo;
    }

    public void setNomeTipo(String nomeTipo) {
        this.nomeTipo = nomeTipo;
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

    @Override
    public String toString() {
        return (nomeTipo != null ? nomeTipo : "Recurso") + " - US$ " + String.format("%.2f", custoEstimado);
    }
}
