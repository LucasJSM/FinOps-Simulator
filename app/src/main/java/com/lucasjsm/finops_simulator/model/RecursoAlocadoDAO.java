package com.lucasjsm.finops_simulator.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO da tabela RECURSO_ALOCADO
 * Tabela associativa que relaciona PROJETO e TIPO_RECURSO
 * Métodos: open, close, inserir, alterar, apagar, buscar, getAllPorProjeto e calcularCustoTotal
 */
public class RecursoAlocadoDAO {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    private String[] colunas = {
            DatabaseHelper.COLUNA_RECURSO_ID,
            DatabaseHelper.COLUNA_RECURSO_PROJETO_ID,
            DatabaseHelper.COLUNA_RECURSO_TIPO_ID,
            DatabaseHelper.COLUNA_RECURSO_QUANTIDADE,
            DatabaseHelper.COLUNA_RECURSO_PERIODO,
            DatabaseHelper.COLUNA_RECURSO_CUSTO,
            DatabaseHelper.COLUNA_RECURSO_OBSERVACAO
    };

    public RecursoAlocadoDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long inserir(long projetoId, long tipoId, double quantidade, double periodo,
                        double custo, String observacao) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUNA_RECURSO_PROJETO_ID, projetoId);
        values.put(DatabaseHelper.COLUNA_RECURSO_TIPO_ID, tipoId);
        values.put(DatabaseHelper.COLUNA_RECURSO_QUANTIDADE, quantidade);
        values.put(DatabaseHelper.COLUNA_RECURSO_PERIODO, periodo);
        values.put(DatabaseHelper.COLUNA_RECURSO_CUSTO, custo);
        values.put(DatabaseHelper.COLUNA_RECURSO_OBSERVACAO, observacao);
        return database.insert(DatabaseHelper.TABELA_RECURSO, null, values);
    }

    /**
     * Altera quantidade, período, custo e observação
     * O projeto e o tipo de recurso não são alterados
     */
    public void alterar(long id, double quantidade, double periodo, double custo, String observacao) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUNA_RECURSO_QUANTIDADE, quantidade);
        values.put(DatabaseHelper.COLUNA_RECURSO_PERIODO, periodo);
        values.put(DatabaseHelper.COLUNA_RECURSO_CUSTO, custo);
        values.put(DatabaseHelper.COLUNA_RECURSO_OBSERVACAO, observacao);
        database.update(DatabaseHelper.TABELA_RECURSO, values,
                DatabaseHelper.COLUNA_RECURSO_ID + " = " + id, null);
    }

    public void apagar(long id) {
        database.delete(DatabaseHelper.TABELA_RECURSO,
                DatabaseHelper.COLUNA_RECURSO_ID + " = " + id, null);
    }

    public RecursoAlocado buscar(long id) {
        RecursoAlocado recurso = null;
        Cursor cursor = database.query(DatabaseHelper.TABELA_RECURSO, colunas,
                DatabaseHelper.COLUNA_RECURSO_ID + " = " + id,
                null, null, null, null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            recurso = cursorParaRecurso(cursor);
        }
        cursor.close();
        return recurso;
    }

    // Lista os recursos alocados de um projeto (relacionamento projeto -> recurso_alocado)
    public List<RecursoAlocado> getAllPorProjeto(long projetoId) {
        List<RecursoAlocado> recursos = new ArrayList<RecursoAlocado>();
        Cursor cursor = database.query(DatabaseHelper.TABELA_RECURSO, colunas,
                DatabaseHelper.COLUNA_RECURSO_PROJETO_ID + " = " + projetoId,
                null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            recursos.add(cursorParaRecurso(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return recursos;
    }

    // Calcular o custo estimado de todos os recursos alocados ao projeto
    public double calcularCustoTotal(long projetoId) {
        double total = 0;
        List<RecursoAlocado> recursos = getAllPorProjeto(projetoId);
        for (RecursoAlocado r : recursos) {
            total += r.getCustoEstimado();
        }
        return total;
    }

    private RecursoAlocado cursorParaRecurso(Cursor cursor) {
        RecursoAlocado recurso = new RecursoAlocado();
        recurso.setId(cursor.getLong(0));
        recurso.setProjetoId(cursor.getLong(1));
        recurso.setTipoRecursoId(cursor.getLong(2));
        recurso.setQuantidade(cursor.getDouble(3));
        recurso.setPeriodo(cursor.getDouble(4));
        recurso.setCustoEstimado(cursor.getDouble(5));
        recurso.setObservacao(cursor.getString(6));
        return recurso;
    }
}
