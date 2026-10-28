package com.lucasjsm.finops_simulator.model;

import java.util.Locale;

public class RecursoAlocado {

    private long id;
    private long projetoId;
    private long tipoRecursoId;
    private double quantidade;
    private double periodo;
    private double custoEstimado;
    private String observacao;

    private String nomeTipo;
    private String provedor;
    private String unidade;
    private double custoUnitario;

    // Construtor padrão
    public RecursoAlocado() {
        this.quantidade = 1.0;
        this.periodo = 1.0;
        this.custoEstimado = 0.0;
        this.observacao = "";
        this.nomeTipo = "Recurso";
        this.unidade = "unidade";
        this.custoUnitario = 0.0;
    }

    // Construtor sem ID
    public RecursoAlocado(long projetoId, long tipoRecursoId, double quantidade, double periodo, String observacao) {
        this.projetoId = projetoId;
        this.tipoRecursoId = tipoRecursoId;
        this.quantidade = quantidade;
        this.periodo = periodo;
        this.observacao = observacao;
        this.custoEstimado = 0.0;
    }

    // Construtor com ID
    public RecursoAlocado(long id, long projetoId, long tipoRecursoId, double quantidade, double periodo, double custoEstimado, String observacao) {
        this.id = id;
        this.projetoId = projetoId;
        this.tipoRecursoId = tipoRecursoId;
        this.quantidade = quantidade;
        this.periodo = periodo;
        this.custoEstimado = custoEstimado;
        this.observacao = observacao;
    }

    /**
     * Regra de negócio da Camada Model para cálculo do custo estimado:
     * Para serviços cobrados por hora, multiplica por 730 horas/mês.
     */
    public double calcularCusto(double custoUnitario, String unidadeRecurso) {
        this.custoUnitario = custoUnitario;
        this.unidade = unidadeRecurso;

        double multiplicadorTempo = 1.0;
        if (unidadeRecurso != null && unidadeRecurso.trim().equalsIgnoreCase("hora")) {
            multiplicadorTempo = 730.0;
        }

        this.custoEstimado = this.quantidade * this.custoUnitario * multiplicadorTempo * this.periodo;
        return this.custoEstimado;
    }

    // Métodos Getters e Setters
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
        return (nomeTipo != null ? nomeTipo : "Recurso") + " - R$ " + String.format(Locale.getDefault(), "%.2f", custoEstimado);
    }
}
