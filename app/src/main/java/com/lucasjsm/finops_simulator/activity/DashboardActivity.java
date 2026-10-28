package com.lucasjsm.finops_simulator.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lucasjsm.finops_simulator.R;
import com.lucasjsm.finops_simulator.adapter.RecursoAdapter;
import com.lucasjsm.finops_simulator.model.Projeto;
import com.lucasjsm.finops_simulator.model.ProjetoDAO;
import com.lucasjsm.finops_simulator.model.RecursoAlocado;
import com.lucasjsm.finops_simulator.model.RecursoAlocadoDAO;
import com.lucasjsm.finops_simulator.model.TipoRecurso;
import com.lucasjsm.finops_simulator.model.TipoRecursoDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener, RecursoAdapter.OnRecursoActionListener,
        BottomNavigationView.OnNavigationItemSelectedListener {

    private TextView tvNome, tvProvedor, tvOrcamento, tvCustoTotal, tvPercentual;
    private LinearLayout llVazio, llProgresso;
    private ListView listViewRecursos;
    private ProgressBar pbOrcamento;
    private BottomNavigationView bottomNav;

    private ProjetoDAO projetoDao;
    private RecursoAlocadoDAO recursoDao;
    private TipoRecursoDAO tipoDao;

    private long projetoId;
    private Projeto projeto;
    private List<RecursoAlocado> listaRecursos;
    private RecursoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        projetoDao = new ProjetoDAO(this);
        recursoDao = new RecursoAlocadoDAO(this);
        tipoDao = new TipoRecursoDAO(this);

        projetoId = getIntent().getLongExtra("projeto_id", -1);
        if (projetoId < 0) {
            Toast.makeText(this, "Projeto inválido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvNome = (TextView) findViewById(R.id.tvDashNome);
        tvProvedor = (TextView) findViewById(R.id.tvDashProvedor);
        tvOrcamento = (TextView) findViewById(R.id.tvDashOrcamento);
        tvCustoTotal = (TextView) findViewById(R.id.tvDashCustoTotal);
        tvPercentual = (TextView) findViewById(R.id.tvDashPercentual);
        llVazio = (LinearLayout) findViewById(R.id.llDashVazio);
        llProgresso = (LinearLayout) findViewById(R.id.llDashProgresso);
        listViewRecursos = (ListView) findViewById(R.id.listViewRecursos);
        pbOrcamento = (ProgressBar) findViewById(R.id.pbDashOrcamento);
        bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        listViewRecursos.setOnItemClickListener(this);

        bottomNav.getMenu().findItem(R.id.nav_projetos).setChecked(true);
        bottomNav.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        projetoDao.open();
        recursoDao.open();
        tipoDao.open();
        carregarDashboard();
        bottomNav.getMenu().findItem(R.id.nav_projetos).setChecked(true);
    }

    @Override
    protected void onPause() {
        projetoDao.close();
        recursoDao.close();
        tipoDao.close();
        super.onPause();
    }

    private void carregarDashboard() {
        projeto = projetoDao.buscar(projetoId);
        if (projeto == null) {
            finish();
            return;
        }

        tvNome.setText(projeto.getNome());
        tvProvedor.setText("Provedor: " + projeto.getProvedor());
        tvOrcamento.setText(String.format(Locale.getDefault(),
                "Orçamento Máximo: R$ %,.2f", projeto.getOrcamentoMaximo()));

        double custo = recursoDao.calcularCustoTotal(projetoId);
        projeto.setCustoTotal(custo);
        double orcamento = projeto.getOrcamentoMaximo();

        listaRecursos = recursoDao.getAllPorProjeto(projetoId);
        if (listaRecursos == null) {
            listaRecursos = new ArrayList<>();
        }

        if (listaRecursos.isEmpty()) {
            tvCustoTotal.setText("Custo Total Utilizado: -");
            llProgresso.setVisibility(View.GONE);
            llVazio.setVisibility(View.VISIBLE);
            listViewRecursos.setVisibility(View.GONE);
        } else {
            tvCustoTotal.setText(String.format(Locale.getDefault(),
                    "Custo Total Utilizado: R$ %,.2f", custo));
            llProgresso.setVisibility(View.VISIBLE);
            llVazio.setVisibility(View.GONE);
            listViewRecursos.setVisibility(View.VISIBLE);

            int pctReal = 0;
            if (orcamento > 0) {
                pctReal = (int) Math.round((custo / orcamento) * 100.0);
            }
            pbOrcamento.setProgress(Math.min(pctReal, 100));

            if (pctReal > 100) {
                pbOrcamento.setProgressDrawable(
                        ContextCompat.getDrawable(this, R.drawable.progress_budget_danger));
                tvPercentual.setTextColor(ContextCompat.getColor(this, R.color.status_danger_text));
            } else if (pctReal >= 80) {
                pbOrcamento.setProgressDrawable(
                        ContextCompat.getDrawable(this, R.drawable.progress_budget_warning));
                tvPercentual.setTextColor(ContextCompat.getColor(this, R.color.status_warning_text));
            } else {
                pbOrcamento.setProgressDrawable(
                        ContextCompat.getDrawable(this, R.drawable.progress_budget));
                tvPercentual.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            }
            tvPercentual.setText(String.format(Locale.getDefault(),
                    "%d%% do orçamento utilizado", pctReal));

            for (RecursoAlocado r : listaRecursos) {
                TipoRecurso tipo = tipoDao.buscar(r.getTipoRecursoId());
                if (tipo != null) {
                    r.setNomeTipo(tipo.getNome());
                    r.setProvedor(tipo.getProvedor());
                    r.setUnidade(tipo.getUnidade());
                    r.setCustoUnitario(tipo.getCustoUnitario());
                }
            }

            adapter = new RecursoAdapter(this, listaRecursos, this);
            listViewRecursos.setAdapter(adapter);
        }
    }

    public void adicionarRecurso(View v) {
        Intent intent = new Intent(getApplicationContext(), FormRecursoActivity.class);
        intent.putExtra("acao", -1);
        intent.putExtra("id", 0L);
        intent.putExtra("projeto_id", projetoId);
        startActivity(intent);
    }

    public void editarProjeto(View v) {
        Intent intent = new Intent(getApplicationContext(), FormProjetoActivity.class);
        intent.putExtra("acao", 0);
        intent.putExtra("id", projetoId);
        startActivity(intent);
    }

    public void excluirProjeto(View v) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir Projeto")
                .setMessage("Deseja realmente excluir o projeto \"" + (projeto != null ? projeto.getNome() : "") + "\" e todos os seus recursos?")
                .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        projetoDao.open();
                        projetoDao.apagar(projetoId);
                        projetoDao.close();
                        Toast.makeText(getApplicationContext(), "Projeto excluído!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    public void onVoltarClick(View v) {
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (listaRecursos == null || position < 0 || position >= listaRecursos.size()) {
            return;
        }
        RecursoAlocado r = listaRecursos.get(position);
        onEditarRecurso(r);
    }

    @Override
    public void onEditarRecurso(RecursoAlocado recurso) {
        Intent intent = new Intent(getApplicationContext(), FormRecursoActivity.class);
        intent.putExtra("acao", 0);
        intent.putExtra("id", recurso.getId());
        intent.putExtra("projeto_id", projetoId);
        startActivity(intent);
    }

    @Override
    public void onExcluirRecurso(final RecursoAlocado recurso) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir Recurso")
                .setMessage("Deseja realmente excluir o recurso \"" + (recurso.getNomeTipo() != null ? recurso.getNomeTipo() : "") + "\"?")
                .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        recursoDao.open();
                        recursoDao.apagar(recurso.getId());
                        recursoDao.close();
                        Toast.makeText(getApplicationContext(), "Recurso excluído!", Toast.LENGTH_SHORT).show();
                        carregarDashboard();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_inicio) {
            Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.nav_projetos) {
            startActivity(new Intent(DashboardActivity.this, ListaProjetosActivity.class));
            finish();
            return true;
        } else if (id == R.id.nav_catalogo) {
            startActivity(new Intent(DashboardActivity.this, ListaTiposActivity.class));
            finish();
            return true;
        }
        return false;
    }
}
