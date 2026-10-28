package com.lucasjsm.finops_simulator.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO da tabela PROJETO
 * Métodos: open, close, inserir, alterar, apagar, buscar e getAll
 */
public class ProjetoDAO {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    private String[] colunas = {
            DatabaseHelper.COLUNA_PROJETO_ID,
            DatabaseHelper.COLUNA_PROJETO_NOME,
            DatabaseHelper.COLUNA_PROJETO_DESCRICAO,
            DatabaseHelper.COLUNA_PROJETO_PROVEDOR,
            DatabaseHelper.COLUNA_PROJETO_ORCAMENTO,
            DatabaseHelper.COLUNA_PROJETO_DATA
    };

    public ProjetoDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long inserir(String nome, String descricao, String provedor, double orcamento, String data) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUNA_PROJETO_NOME, nome);
        values.put(DatabaseHelper.COLUNA_PROJETO_DESCRICAO, descricao);
        values.put(DatabaseHelper.COLUNA_PROJETO_PROVEDOR, provedor);
        values.put(DatabaseHelper.COLUNA_PROJETO_ORCAMENTO, orcamento);
        values.put(DatabaseHelper.COLUNA_PROJETO_DATA, data);
        return database.insert(DatabaseHelper.TABELA_PROJETO, null, values);
    }

    /**
     * Altera nome, descrição e orçamento
     * O provedor não é alterado após a criação (regra de negócio)
     */
    public void alterar(long id, String nome, String descricao, double orcamento) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUNA_PROJETO_NOME, nome);
        values.put(DatabaseHelper.COLUNA_PROJETO_DESCRICAO, descricao);
        values.put(DatabaseHelper.COLUNA_PROJETO_ORCAMENTO, orcamento);
        database.update(DatabaseHelper.TABELA_PROJETO, values,
                DatabaseHelper.COLUNA_PROJETO_ID + " = " + id, null);
    }

    /**
     * Apaga os recursos alocados e o projeto
     * (relacionamento projeto -> recurso_alocado).
     */
    public void apagar(long id) {
        database.delete(DatabaseHelper.TABELA_RECURSO,
                DatabaseHelper.COLUNA_RECURSO_PROJETO_ID + " = " + id, null);
        database.delete(DatabaseHelper.TABELA_PROJETO,
                DatabaseHelper.COLUNA_PROJETO_ID + " = " + id, null);
    }

    public Projeto buscar(long id) {
        Projeto projeto = null;
        Cursor cursor = database.query(DatabaseHelper.TABELA_PROJETO, colunas,
                DatabaseHelper.COLUNA_PROJETO_ID + " = " + id,
                null, null, null, null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            projeto = cursorParaProjeto(cursor);
        }
        cursor.close();
        return projeto;
    }

    public List<Projeto> getAll() {
        List<Projeto> projetos = new ArrayList<Projeto>();
        Cursor cursor = database.query(DatabaseHelper.TABELA_PROJETO, colunas,
                null, null, null, null, DatabaseHelper.COLUNA_PROJETO_NOME);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            projetos.add(cursorParaProjeto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return projetos;
    }

    private Projeto cursorParaProjeto(Cursor cursor) {
        Projeto projeto = new Projeto();
        projeto.setId(cursor.getLong(0));
        projeto.setNome(cursor.getString(1));
        projeto.setDescricao(cursor.getString(2));
        projeto.setProvedor(cursor.getString(3));
        projeto.setOrcamentoMaximo(cursor.getDouble(4));
        projeto.setDataCriacao(cursor.getString(5));
        return projeto;
    }
}
