package com.lucasjsm.finops_simulator.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Classe responsável pela criação e atualização do banco de dados
 * Tabelas:
 *  - projeto           (1) ----< (N) recurso_alocado
 *  - tipo_recurso      (1) ----< (N) recurso_alocado
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "finops_simulator.db";
    private static final int VERSAO_BANCO = 2;

    // ===== Tabela PROJETO =====
    public static final String TABELA_PROJETO = "projeto";
    public static final String COLUNA_PROJETO_ID = "id";
    public static final String COLUNA_PROJETO_NOME = "nome";
    public static final String COLUNA_PROJETO_DESCRICAO = "descricao";
    public static final String COLUNA_PROJETO_PROVEDOR = "provedor";
    public static final String COLUNA_PROJETO_ORCAMENTO = "orcamento_maximo";
    public static final String COLUNA_PROJETO_DATA = "data_criacao";

    // ===== Tabela TIPO_RECURSO (Catálogo de serviços) =====
    public static final String TABELA_TIPO = "tipo_recurso";
    public static final String COLUNA_TIPO_ID = "id";
    public static final String COLUNA_TIPO_NOME = "nome";
    public static final String COLUNA_TIPO_PROVEDOR = "provedor";
    public static final String COLUNA_TIPO_UNIDADE = "unidade";
    public static final String COLUNA_TIPO_CUSTO = "custo_unitario";
    public static final String COLUNA_TIPO_DESCRICAO = "descricao";

    // ===== Tabela RECURSO_ALOCADO (relaciona Projeto e TipoRecurso) =====
    public static final String TABELA_RECURSO = "recurso_alocado";
    public static final String COLUNA_RECURSO_ID = "id";
    public static final String COLUNA_RECURSO_PROJETO_ID = "projeto_id";
    public static final String COLUNA_RECURSO_TIPO_ID = "tipo_recurso_id";
    public static final String COLUNA_RECURSO_QUANTIDADE = "quantidade";
    public static final String COLUNA_RECURSO_PERIODO = "periodo";
    public static final String COLUNA_RECURSO_CUSTO = "custo_estimado";
    public static final String COLUNA_RECURSO_OBSERVACAO = "observacao";

    // ===== Comandos SQL de criação =====
    private static final String CRIAR_TABELA_PROJETO = "create table "
            + TABELA_PROJETO + "("
            + COLUNA_PROJETO_ID + " integer primary key autoincrement, "
            + COLUNA_PROJETO_NOME + " text not null, "
            + COLUNA_PROJETO_DESCRICAO + " text, "
            + COLUNA_PROJETO_PROVEDOR + " text not null, "
            + COLUNA_PROJETO_ORCAMENTO + " real, "
            + COLUNA_PROJETO_DATA + " text);";

    private static final String CRIAR_TABELA_TIPO = "create table "
            + TABELA_TIPO + "("
            + COLUNA_TIPO_ID + " integer primary key autoincrement, "
            + COLUNA_TIPO_NOME + " text not null, "
            + COLUNA_TIPO_PROVEDOR + " text not null, "
            + COLUNA_TIPO_UNIDADE + " text not null, "
            + COLUNA_TIPO_CUSTO + " real not null, "
            + COLUNA_TIPO_DESCRICAO + " text);";

    private static final String CRIAR_TABELA_RECURSO = "create table "
            + TABELA_RECURSO + "("
            + COLUNA_RECURSO_ID + " integer primary key autoincrement, "
            + COLUNA_RECURSO_PROJETO_ID + " integer not null, "
            + COLUNA_RECURSO_TIPO_ID + " integer not null, "
            + COLUNA_RECURSO_QUANTIDADE + " real not null, "
            + COLUNA_RECURSO_PERIODO + " real not null, "
            + COLUNA_RECURSO_CUSTO + " real, "
            + COLUNA_RECURSO_OBSERVACAO + " text, "
            + "foreign key(" + COLUNA_RECURSO_PROJETO_ID + ") references "
            + TABELA_PROJETO + "(" + COLUNA_PROJETO_ID + "), "
            + "foreign key(" + COLUNA_RECURSO_TIPO_ID + ") references "
            + TABELA_TIPO + "(" + COLUNA_TIPO_ID + "));";

    public DatabaseHelper(Context context) {
        super(context, NOME_BANCO, null, VERSAO_BANCO);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CRIAR_TABELA_PROJETO);
        database.execSQL(CRIAR_TABELA_TIPO);
        database.execSQL(CRIAR_TABELA_RECURSO);
        inserirCatalogoInicial(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int versaoAntiga, int versaoNova) {
        database.execSQL("drop table if exists " + TABELA_RECURSO);
        database.execSQL("drop table if exists " + TABELA_TIPO);
        database.execSQL("drop table if exists " + TABELA_PROJETO);
        onCreate(database);
    }

    /**
     * Catálogo pré-preenchido dos serviços de cloud (AWS, GCP, Azure, OCI)
     */
    private void inserirCatalogoInicial(SQLiteDatabase database) {
        String inicio = "insert into " + TABELA_TIPO + " ("
                + COLUNA_TIPO_NOME + ", " + COLUNA_TIPO_PROVEDOR + ", "
                + COLUNA_TIPO_UNIDADE + ", " + COLUNA_TIPO_CUSTO + ", "
                + COLUNA_TIPO_DESCRICAO + ") values ";

        // ===== AWS (Amazon Web Services) =====
        database.execSQL(inicio + "('EC2', 'AWS', 'hora', 0.096, 'Instância de computação elástica (t3.large)');");
        database.execSQL(inicio + "('S3 Standard', 'AWS', 'GB', 0.023, 'Armazenamento de objetos de alta durabilidade');");
        database.execSQL(inicio + "('RDS PostgreSQL', 'AWS', 'hora', 0.145, 'Banco de dados relacional gerenciado');");
        database.execSQL(inicio + "('Lambda', 'AWS', 'milhao_reqs', 0.20, 'Computação serverless orientada a eventos');");
        database.execSQL(inicio + "('DynamoDB', 'AWS', 'GB', 0.25, 'Banco de dados NoSQL de chave-valor');");
        database.execSQL(inicio + "('CloudWatch', 'AWS', 'GB', 0.50, 'Monitoramento, métricas e observabilidade');");
        database.execSQL(inicio + "('EKS', 'AWS', 'hora', 0.10, 'Cluster gerenciado do Kubernetes');");
        database.execSQL(inicio + "('EBS gp3', 'AWS', 'GB', 0.08, 'Armazenamento em bloco SSD de uso geral');");
        database.execSQL(inicio + "('CloudFront', 'AWS', 'GB', 0.085, 'Rede de entrega de conteúdo (CDN) e borda');");

        // ===== GCP (Google Cloud Platform) =====
        database.execSQL(inicio + "('Compute Engine', 'GCP', 'hora', 0.089, 'Máquinas virtuais escaláveis (e2-standard-2)');");
        database.execSQL(inicio + "('Cloud Storage', 'GCP', 'GB', 0.020, 'Armazenamento de objetos unificado');");
        database.execSQL(inicio + "('Cloud SQL', 'GCP', 'hora', 0.130, 'Banco gerenciado MySQL e PostgreSQL');");
        database.execSQL(inicio + "('Cloud Functions', 'GCP', 'milhao_reqs', 0.40, 'Computação serverless orientada a eventos');");
        database.execSQL(inicio + "('BigQuery', 'GCP', 'TB', 5.00, 'Data warehouse analítico serverless');");
        database.execSQL(inicio + "('GKE', 'GCP', 'hora', 0.10, 'Google Kubernetes Engine automatizado');");
        database.execSQL(inicio + "('BigTable', 'GCP', 'GB', 0.17, 'Banco NoSQL massivo para alta vazão');");
        database.execSQL(inicio + "('Dataproc', 'GCP', 'hora', 0.15, 'Clusters Apache Spark e Hadoop gerenciados');");
        database.execSQL(inicio + "('Cloud Logging', 'GCP', 'GB', 0.50, 'Ingestão e retenção centralizada de logs');");

        // ===== Azure (Microsoft Azure) =====
        database.execSQL(inicio + "('Virtual Machines', 'Azure', 'hora', 0.096, 'Computação sob demanda (D2s v5)');");
        database.execSQL(inicio + "('Blob Storage', 'Azure', 'GB', 0.018, 'Armazenamento seguro de objetos e blobs');");
        database.execSQL(inicio + "('Azure SQL', 'Azure', 'hora', 0.150, 'Banco relacional totalmente gerenciado');");
        database.execSQL(inicio + "('Azure Functions', 'Azure', 'milhao_reqs', 0.20, 'Execução de código serverless sob demanda');");
        database.execSQL(inicio + "('Cosmos DB', 'Azure', 'GB', 0.25, 'Banco NoSQL multi-modelo distribuído globalmente');");
        database.execSQL(inicio + "('AKS', 'Azure', 'hora', 0.10, 'Azure Kubernetes Service gerenciado');");
        database.execSQL(inicio + "('Azure Monitor', 'Azure', 'GB', 2.30, 'Coleta centralizada de métricas e logs');");
        database.execSQL(inicio + "('Managed Disks', 'Azure', 'GB', 0.075, 'Disco SSD gerenciado de alta performance');");
        database.execSQL(inicio + "('App Service', 'Azure', 'hora', 0.075, 'Hospedagem elástica de APIs e web apps');");

        // ===== OCI (Oracle Cloud Infrastructure) =====
        database.execSQL(inicio + "('Compute VM', 'OCI', 'hora', 0.065, 'Máquina virtual flexível E4 Flex');");
        database.execSQL(inicio + "('Bare Metal', 'OCI', 'hora', 1.20, 'Servidor dedicado com desempenho físico máximo');");
        database.execSQL(inicio + "('Object Storage', 'OCI', 'GB', 0.025, 'Armazenamento de objetos escalável');");
        database.execSQL(inicio + "('Autonomous Database', 'OCI', 'hora', 0.50, 'Banco relacional autogerenciado e autoajustável');");
        database.execSQL(inicio + "('OKE', 'OCI', 'hora', 0.10, 'Oracle Container Engine for Kubernetes');");
        database.execSQL(inicio + "('OCI Functions', 'OCI', 'milhao_reqs', 0.20, 'Computação serverless baseada em Fn Project');");
        database.execSQL(inicio + "('Block Volume', 'OCI', 'GB', 0.042, 'Volume em bloco NVMe de alto desempenho');");
        database.execSQL(inicio + "('Monitoring & Logging', 'OCI', 'GB', 0.05, 'Monitoramento e análise centralizada de eventos');");
        database.execSQL(inicio + "('MySQL HeatWave', 'OCI', 'hora', 0.115, 'Banco MySQL acelerado com motor analítico');");
    }
}
