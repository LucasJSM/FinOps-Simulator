package com.lucasjsm.finops_simulator.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lucasjsm.finops_simulator.model.Projeto;
import com.lucasjsm.finops_simulator.model.RecursoAlocado;
import com.lucasjsm.finops_simulator.model.TipoRecurso;

import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseHelper – acesso a dados SQLite.
 * Três tabelas: projeto, tipo_recurso, recurso_alocado.
 * Relacionamentos 1:N com integridade referencial.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "finops_simulator.db";
    private static final int DATABASE_VERSION = 1;

    // Tabela Projeto
    public static final String TABLE_PROJETO = "projeto";
    public static final String COL_PROJ_ID = "id";
    public static final String COL_PROJ_NOME = "nome";
    public static final String COL_PROJ_DESCRICAO = "descricao";
    public static final String COL_PROJ_PROVEDOR = "provedor";
    public static final String COL_PROJ_ORCAMENTO = "orcamento_maximo";
    public static final String COL_PROJ_DATA = "data_criacao";

    // Tabela TipoRecurso
    public static final String TABLE_TIPO = "tipo_recurso";
    public static final String COL_TIPO_ID = "id";
    public static final String COL_TIPO_NOME = "nome";
    public static final String COL_TIPO_PROVEDOR = "provedor";
    public static final String COL_TIPO_UNIDADE = "unidade";
    public static final String COL_TIPO_CUSTO = "custo_unitario";
    public static final String COL_TIPO_DESCRICAO = "descricao";

    // Tabela RecursoAlocado
    public static final String TABLE_RECURSO = "recurso_alocado";
    public static final String COL_REC_ID = "id";
    public static final String COL_REC_PROJETO_ID = "projeto_id";
    public static final String COL_REC_TIPO_ID = "tipo_recurso_id";
    public static final String COL_REC_QUANTIDADE = "quantidade";
    public static final String COL_REC_PERIODO = "periodo";
    public static final String COL_REC_CUSTO = "custo_estimado";
    public static final String COL_REC_OBS = "observacao";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlProjeto = "CREATE TABLE " + TABLE_PROJETO + " (" +
                COL_PROJ_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PROJ_NOME + " TEXT NOT NULL, " +
                COL_PROJ_DESCRICAO + " TEXT, " +
                COL_PROJ_PROVEDOR + " TEXT NOT NULL, " +
                COL_PROJ_ORCAMENTO + " REAL, " +
                COL_PROJ_DATA + " TEXT)";
        db.execSQL(sqlProjeto);

        String sqlTipo = "CREATE TABLE " + TABLE_TIPO + " (" +
                COL_TIPO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TIPO_NOME + " TEXT NOT NULL, " +
                COL_TIPO_PROVEDOR + " TEXT NOT NULL, " +
                COL_TIPO_UNIDADE + " TEXT NOT NULL, " +
                COL_TIPO_CUSTO + " REAL NOT NULL, " +
                COL_TIPO_DESCRICAO + " TEXT)";
        db.execSQL(sqlTipo);

        String sqlRecurso = "CREATE TABLE " + TABLE_RECURSO + " (" +
                COL_REC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_REC_PROJETO_ID + " INTEGER NOT NULL, " +
                COL_REC_TIPO_ID + " INTEGER NOT NULL, " +
                COL_REC_QUANTIDADE + " REAL NOT NULL, " +
                COL_REC_PERIODO + " REAL NOT NULL, " +
                COL_REC_CUSTO + " REAL, " +
                COL_REC_OBS + " TEXT, " +
                "FOREIGN KEY(" + COL_REC_PROJETO_ID + ") REFERENCES " + TABLE_PROJETO + "(" + COL_PROJ_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY(" + COL_REC_TIPO_ID + ") REFERENCES " + TABLE_TIPO + "(" + COL_TIPO_ID + "))";
        db.execSQL(sqlRecurso);

        // Dados iniciais de exemplo (catálogo)
        inserirTiposIniciais(db);
    }

    private void inserirTiposIniciais(SQLiteDatabase db) {
        // AWS
        inserirTipoDireto(db, "EC2", "AWS", "hora", 0.10, "Instância de computação elástica");
        inserirTipoDireto(db, "S3", "AWS", "GB", 0.023, "Armazenamento de objetos");
        inserirTipoDireto(db, "Lambda", "AWS", "milhao_reqs", 0.20, "Computação serverless");
        inserirTipoDireto(db, "DynamoDB", "AWS", "GB", 0.25, "Banco de dados NoSQL");
        // GCP
        inserirTipoDireto(db, "BigQuery", "GCP", "TB", 5.00, "Data warehouse analítico");
        inserirTipoDireto(db, "Apache Spark", "GCP", "hora", 0.15, "Processamento de dados");
        inserirTipoDireto(db, "BigTable", "GCP", "GB", 0.17, "Banco NoSQL de alta performance");
        // Azure
        inserirTipoDireto(db, "Blob Storage", "Azure", "GB", 0.018, "Armazenamento de blobs");
        inserirTipoDireto(db, "Azure Monitor", "Azure", "GB", 2.30, "Monitoramento e logs");
        inserirTipoDireto(db, "AKS", "Azure", "hora", 0.12, "Kubernetes gerenciado");
        // OCI
        inserirTipoDireto(db, "Autonomous Database", "OCI", "hora", 0.50, "Banco de dados autônomo");
        inserirTipoDireto(db, "Bare Metal", "OCI", "hora", 1.20, "Servidor bare metal");
        inserirTipoDireto(db, "OKE", "OCI", "hora", 0.10, "Kubernetes Engine");
    }

    private void inserirTipoDireto(SQLiteDatabase db, String nome, String provedor, String unidade, double custo, String desc) {
        ContentValues cv = new ContentValues();
        cv.put(COL_TIPO_NOME, nome);
        cv.put(COL_TIPO_PROVEDOR, provedor);
        cv.put(COL_TIPO_UNIDADE, unidade);
        cv.put(COL_TIPO_CUSTO, custo);
        cv.put(COL_TIPO_DESCRICAO, desc);
        db.insert(TABLE_TIPO, null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECURSO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIPO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJETO);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // ==================== CRUD PROJETO ====================

    public long inserirProjeto(Projeto p) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_PROJ_NOME, p.getNome());
        cv.put(COL_PROJ_DESCRICAO, p.getDescricao());
        cv.put(COL_PROJ_PROVEDOR, p.getProvedor());
        cv.put(COL_PROJ_ORCAMENTO, p.getOrcamentoMaximo());
        cv.put(COL_PROJ_DATA, p.getDataCriacao());
        long id = db.insert(TABLE_PROJETO, null, cv);
        db.close();
        return id;
    }

    public List<Projeto> listarProjetos() {
        List<Projeto> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_PROJETO, null, null, null, null, null, COL_PROJ_NOME + " ASC");
        if (c.moveToFirst()) {
            do {
                Projeto p = cursorToProjeto(c);
                p.setCustoTotal(calcularCustoTotalProjeto(p.getId()));
                lista.add(p);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return lista;
    }

    public Projeto buscarProjetoPorId(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_PROJETO, null, COL_PROJ_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        Projeto p = null;
        if (c.moveToFirst()) {
            p = cursorToProjeto(c);
            p.setCustoTotal(calcularCustoTotalProjeto(id));
        }
        c.close();
        db.close();
        return p;
    }

    public int atualizarProjeto(Projeto p) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_PROJ_NOME, p.getNome());
        cv.put(COL_PROJ_DESCRICAO, p.getDescricao());
        // provedor NÃO é atualizado (RN01)
        cv.put(COL_PROJ_ORCAMENTO, p.getOrcamentoMaximo());
        int rows = db.update(TABLE_PROJETO, cv, COL_PROJ_ID + "=?", new String[]{String.valueOf(p.getId())});
        db.close();
        return rows;
    }

    public int excluirProjeto(long id) {
        SQLiteDatabase db = getWritableDatabase();
        // CASCADE remove recursos automaticamente
        int rows = db.delete(TABLE_PROJETO, COL_PROJ_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    private Projeto cursorToProjeto(Cursor c) {
        Projeto p = new Projeto();
        p.setId(c.getLong(c.getColumnIndexOrThrow(COL_PROJ_ID)));
        p.setNome(c.getString(c.getColumnIndexOrThrow(COL_PROJ_NOME)));
        p.setDescricao(c.getString(c.getColumnIndexOrThrow(COL_PROJ_DESCRICAO)));
        p.setProvedor(c.getString(c.getColumnIndexOrThrow(COL_PROJ_PROVEDOR)));
        p.setOrcamentoMaximo(c.getDouble(c.getColumnIndexOrThrow(COL_PROJ_ORCAMENTO)));
        p.setDataCriacao(c.getString(c.getColumnIndexOrThrow(COL_PROJ_DATA)));
        return p;
    }

    // ==================== CRUD TIPO RECURSO ====================

    public long inserirTipo(TipoRecurso t) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TIPO_NOME, t.getNome());
        cv.put(COL_TIPO_PROVEDOR, t.getProvedor());
        cv.put(COL_TIPO_UNIDADE, t.getUnidade());
        cv.put(COL_TIPO_CUSTO, t.getCustoUnitario());
        cv.put(COL_TIPO_DESCRICAO, t.getDescricao());
        long id = db.insert(TABLE_TIPO, null, cv);
        db.close();
        return id;
    }

    public List<TipoRecurso> listarTipos() {
        List<TipoRecurso> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_TIPO, null, null, null, null, null, COL_TIPO_PROVEDOR + " ASC, " + COL_TIPO_NOME + " ASC");
        if (c.moveToFirst()) {
            do {
                lista.add(cursorToTipo(c));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return lista;
    }

    /**
     * Lista apenas os tipos de recurso do provedor informado.
     * Essencial para filtrar o Spinner ao adicionar recurso a um projeto.
     */
    public List<TipoRecurso> listarTiposPorProvedor(String provedor) {
        List<TipoRecurso> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_TIPO, null, COL_TIPO_PROVEDOR + "=?", new String[]{provedor}, null, null, COL_TIPO_NOME + " ASC");
        if (c.moveToFirst()) {
            do {
                lista.add(cursorToTipo(c));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return lista;
    }

    public TipoRecurso buscarTipoPorId(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_TIPO, null, COL_TIPO_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        TipoRecurso t = null;
        if (c.moveToFirst()) {
            t = cursorToTipo(c);
        }
        c.close();
        db.close();
        return t;
    }

    public int atualizarTipo(TipoRecurso t) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TIPO_NOME, t.getNome());
        cv.put(COL_TIPO_PROVEDOR, t.getProvedor());
        cv.put(COL_TIPO_UNIDADE, t.getUnidade());
        cv.put(COL_TIPO_CUSTO, t.getCustoUnitario());
        cv.put(COL_TIPO_DESCRICAO, t.getDescricao());
        int rows = db.update(TABLE_TIPO, cv, COL_TIPO_ID + "=?", new String[]{String.valueOf(t.getId())});
        db.close();
        return rows;
    }

    /**
     * Exclui tipo somente se não estiver em uso (RN08).
     * Retorna -1 se houver dependências.
     */
    public int excluirTipo(long id) {
        if (tipoEmUso(id)) {
            return -1;
        }
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_TIPO, COL_TIPO_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    public boolean tipoEmUso(long tipoId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RECURSO + " WHERE " + COL_REC_TIPO_ID + "=?", new String[]{String.valueOf(tipoId)});
        boolean emUso = false;
        if (c.moveToFirst()) {
            emUso = c.getInt(0) > 0;
        }
        c.close();
        db.close();
        return emUso;
    }

    private TipoRecurso cursorToTipo(Cursor c) {
        TipoRecurso t = new TipoRecurso();
        t.setId(c.getLong(c.getColumnIndexOrThrow(COL_TIPO_ID)));
        t.setNome(c.getString(c.getColumnIndexOrThrow(COL_TIPO_NOME)));
        t.setProvedor(c.getString(c.getColumnIndexOrThrow(COL_TIPO_PROVEDOR)));
        t.setUnidade(c.getString(c.getColumnIndexOrThrow(COL_TIPO_UNIDADE)));
        t.setCustoUnitario(c.getDouble(c.getColumnIndexOrThrow(COL_TIPO_CUSTO)));
        t.setDescricao(c.getString(c.getColumnIndexOrThrow(COL_TIPO_DESCRICAO)));
        return t;
    }

    // ==================== CRUD RECURSO ALOCADO ====================

    public long inserirRecurso(RecursoAlocado r) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_REC_PROJETO_ID, r.getProjetoId());
        cv.put(COL_REC_TIPO_ID, r.getTipoRecursoId());
        cv.put(COL_REC_QUANTIDADE, r.getQuantidade());
        cv.put(COL_REC_PERIODO, r.getPeriodo());
        cv.put(COL_REC_CUSTO, r.getCustoEstimado());
        cv.put(COL_REC_OBS, r.getObservacao());
        long id = db.insert(TABLE_RECURSO, null, cv);
        db.close();
        return id;
    }

    public List<RecursoAlocado> listarRecursosPorProjeto(long projetoId) {
        List<RecursoAlocado> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT r.*, t.nome AS nome_tipo, t.provedor, t.unidade, t.custo_unitario " +
                "FROM " + TABLE_RECURSO + " r " +
                "INNER JOIN " + TABLE_TIPO + " t ON r." + COL_REC_TIPO_ID + " = t." + COL_TIPO_ID + " " +
                "WHERE r." + COL_REC_PROJETO_ID + "=? " +
                "ORDER BY t.nome ASC";
        Cursor c = db.rawQuery(sql, new String[]{String.valueOf(projetoId)});
        if (c.moveToFirst()) {
            do {
                lista.add(cursorToRecurso(c));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return lista;
    }

    public RecursoAlocado buscarRecursoPorId(long id) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT r.*, t.nome AS nome_tipo, t.provedor, t.unidade, t.custo_unitario " +
                "FROM " + TABLE_RECURSO + " r " +
                "INNER JOIN " + TABLE_TIPO + " t ON r." + COL_REC_TIPO_ID + " = t." + COL_TIPO_ID + " " +
                "WHERE r." + COL_REC_ID + "=?";
        Cursor c = db.rawQuery(sql, new String[]{String.valueOf(id)});
        RecursoAlocado r = null;
        if (c.moveToFirst()) {
            r = cursorToRecurso(c);
        }
        c.close();
        db.close();
        return r;
    }

    public int atualizarRecurso(RecursoAlocado r) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_REC_QUANTIDADE, r.getQuantidade());
        cv.put(COL_REC_PERIODO, r.getPeriodo());
        cv.put(COL_REC_CUSTO, r.getCustoEstimado());
        cv.put(COL_REC_OBS, r.getObservacao());
        // tipo_recurso_id e projeto_id não são alterados
        int rows = db.update(TABLE_RECURSO, cv, COL_REC_ID + "=?", new String[]{String.valueOf(r.getId())});
        db.close();
        return rows;
    }

    public int excluirRecurso(long id) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_RECURSO, COL_REC_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    public double calcularCustoTotalProjeto(long projetoId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(" + COL_REC_CUSTO + ") FROM " + TABLE_RECURSO + " WHERE " + COL_REC_PROJETO_ID + "=?", new String[]{String.valueOf(projetoId)});
        double total = 0;
        if (c.moveToFirst()) {
            total = c.isNull(0) ? 0 : c.getDouble(0);
        }
        c.close();
        db.close();
        return total;
    }

    private RecursoAlocado cursorToRecurso(Cursor c) {
        RecursoAlocado r = new RecursoAlocado();
        r.setId(c.getLong(c.getColumnIndexOrThrow(COL_REC_ID)));
        r.setProjetoId(c.getLong(c.getColumnIndexOrThrow(COL_REC_PROJETO_ID)));
        r.setTipoRecursoId(c.getLong(c.getColumnIndexOrThrow(COL_REC_TIPO_ID)));
        r.setQuantidade(c.getDouble(c.getColumnIndexOrThrow(COL_REC_QUANTIDADE)));
        r.setPeriodo(c.getDouble(c.getColumnIndexOrThrow(COL_REC_PERIODO)));
        r.setCustoEstimado(c.getDouble(c.getColumnIndexOrThrow(COL_REC_CUSTO)));
        r.setObservacao(c.getString(c.getColumnIndexOrThrow(COL_REC_OBS)));
        // Campos do JOIN
        int idxNome = c.getColumnIndex("nome_tipo");
        if (idxNome >= 0) r.setNomeTipo(c.getString(idxNome));
        int idxProv = c.getColumnIndex("provedor");
        if (idxProv >= 0) r.setProvedor(c.getString(idxProv));
        int idxUnid = c.getColumnIndex("unidade");
        if (idxUnid >= 0) r.setUnidade(c.getString(idxUnid));
        int idxCusto = c.getColumnIndex("custo_unitario");
        if (idxCusto >= 0) r.setCustoUnitario(c.getDouble(idxCusto));
        return r;
    }
}
