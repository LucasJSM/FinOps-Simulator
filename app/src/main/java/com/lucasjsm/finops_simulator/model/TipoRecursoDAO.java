package com.lucasjsm.finops_simulator.model;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO da tabela TIPO_RECURSO (catálogo de serviços)
 * Métodos: open, close, buscar, getAll e getAllPorProvedor
 */
public class TipoRecursoDAO {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    private String[] colunas = {
            DatabaseHelper.COLUNA_TIPO_ID,
            DatabaseHelper.COLUNA_TIPO_NOME,
            DatabaseHelper.COLUNA_TIPO_PROVEDOR,
            DatabaseHelper.COLUNA_TIPO_UNIDADE,
            DatabaseHelper.COLUNA_TIPO_CUSTO,
            DatabaseHelper.COLUNA_TIPO_DESCRICAO
    };

    public TipoRecursoDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public TipoRecurso buscar(long id) {
        TipoRecurso tipo = null;
        Cursor cursor = database.query(DatabaseHelper.TABELA_TIPO, colunas,
                DatabaseHelper.COLUNA_TIPO_ID + " = " + id,
                null, null, null, null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            tipo = cursorParaTipo(cursor);
        }
        cursor.close();
        return tipo;
    }

    public List<TipoRecurso> getAll() {
        List<TipoRecurso> tipos = new ArrayList<TipoRecurso>();
        Cursor cursor = database.query(DatabaseHelper.TABELA_TIPO, colunas,
                null, null, null, null,
                DatabaseHelper.COLUNA_TIPO_PROVEDOR + ", " + DatabaseHelper.COLUNA_TIPO_NOME);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            tipos.add(cursorParaTipo(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return tipos;
    }

    /**
     * Lista somente os serviços do provedor informado pelo spinner
     */
    public List<TipoRecurso> getAllPorProvedor(String provedor) {
        List<TipoRecurso> tipos = new ArrayList<TipoRecurso>();
        Cursor cursor = database.query(DatabaseHelper.TABELA_TIPO, colunas,
                DatabaseHelper.COLUNA_TIPO_PROVEDOR + " = ?", new String[]{provedor},
                null, null, DatabaseHelper.COLUNA_TIPO_NOME);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            tipos.add(cursorParaTipo(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return tipos;
    }

    private TipoRecurso cursorParaTipo(Cursor cursor) {
        TipoRecurso tipo = new TipoRecurso();
        tipo.setId(cursor.getLong(0));
        tipo.setNome(cursor.getString(1));
        tipo.setProvedor(cursor.getString(2));
        tipo.setUnidade(cursor.getString(3));
        tipo.setCustoUnitario(cursor.getDouble(4));
        tipo.setDescricao(cursor.getString(5));
        return tipo;
    }
}
