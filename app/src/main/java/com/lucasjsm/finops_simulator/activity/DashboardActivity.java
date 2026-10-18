package com.lucasjsm.finops_simulator.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lucasjsm.finops_simulator.R;
import com.lucasjsm.finops_simulator.adapter.RecursoAdapter;
import com.lucasjsm.finops_simulator.database.DatabaseHelper;
import com.lucasjsm.finops_simulator.model.Projeto;
import com.lucasjsm.finops_simulator.model.RecursoAlocado;

import java.util.List;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity
        implements RecursoAdapter.OnRecursoActionListener {

    private TextView tvNome, tvProvedor, tvOrcamento, tvCustoTotal, tvPercentual;
    private LinearLayout llVazio, llProgresso;
    private ListView listViewRecursos;
    private ProgressBar pbOrcamento;
    private DatabaseHelper db;
    private long projetoId;
    private Projeto projeto;
    private List<RecursoAlocado> recursos;
    private RecursoAdapter adapter;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        db = new DatabaseHelper(this);
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

        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_dashboard);
            bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();
                    if (id == R.id.nav_inicio) {
                        startActivity(new Intent(DashboardActivity.this, HomeActivity.class));
                        finish();
                        return true;
                    } else if (id == R.id.nav_projetos) {
                        startActivity(new Intent(DashboardActivity.this, ListaProjetosActivity.class));
                        finish();
                        return true;
                    } else if (id == R.id.nav_catalogo) {
                        Toast.makeText(DashboardActivity.this, "Catálogo em breve", Toast.LENGTH_SHORT).show();
                        return true;
                    } else if (id == R.id.nav_dashboard) {
                        return true;
                    } else if (id == R.id.nav_sobre) {
                        Toast.makeText(DashboardActivity.this, "Sobre em breve", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarDashboard();
    }

    private void carregarDashboard() {
        projeto = db.buscarProjetoPorId(projetoId);
        if (projeto == null) {
            Toast.makeText(this, "Projeto não encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvNome.setText(projeto.getNome());
        tvProvedor.setText("Provedor: " + projeto.getProvedor());
        tvOrcamento.setText(String.format(Locale.getDefault(),
                "Orçamento Máximo: R$ %,.2f", projeto.getOrcamentoMaximo()));

        recursos = db.listarRecursosPorProjeto(projetoId);
        double custo = projeto.getCustoTotal();
        double orcamento = projeto.getOrcamentoMaximo();

        if (recursos == null || recursos.isEmpty()) {
            // Estado vazio
            tvCustoTotal.setText("Custo Total Utilizado: -");
            llProgresso.setVisibility(View.GONE);
            llVazio.setVisibility(View.VISIBLE);
            listViewRecursos.setVisibility(View.GONE);
        } else {
            // Com recursos
            tvCustoTotal.setText(String.format(Locale.getDefault(),
                    "Custo Total Utilizado: R$ %,.2f", custo));
            llProgresso.setVisibility(View.VISIBLE);
            llVazio.setVisibility(View.GONE);
            listViewRecursos.setVisibility(View.VISIBLE);

            int pct = 0;
            if (orcamento > 0) {
                pct = (int) Math.round((custo / orcamento) * 100.0);
                if (pct > 100) {
                    pct = 100; // barra visual limitada a 100; texto mostra real
                }
            }
            int pctReal = 0;
            if (orcamento > 0) {
                pctReal = (int) Math.round((custo / orcamento) * 100.0);
            }
            pbOrcamento.setProgress(pct);
            if (pctReal > 100) {
                pbOrcamento.setProgressDrawable(
                        getResources().getDrawable(R.drawable.progress_budget_danger));
                tvPercentual.setTextColor(getResources().getColor(R.color.status_danger_text));
            } else if (pctReal >= 80) {
                pbOrcamento.setProgressDrawable(
                        getResources().getDrawable(R.drawable.progress_budget_warning));
                tvPercentual.setTextColor(getResources().getColor(R.color.status_warning_text));
            } else {
                pbOrcamento.setProgressDrawable(
                        getResources().getDrawable(R.drawable.progress_budget));
                tvPercentual.setTextColor(getResources().getColor(R.color.text_secondary));
            }
            tvPercentual.setText(String.format(Locale.getDefault(),
                    "%d%% do orçamento utilizado", pctReal));

            adapter = new RecursoAdapter(this, recursos, this);
            listViewRecursos.setAdapter(adapter);
        }
    }

    public void adicionarRecurso(View v) {
        Intent i = new Intent(getApplicationContext(), FormRecursoActivity.class);
        i.putExtra("projeto_id", projetoId);
        startActivity(i);
    }

    public void editarProjeto(View v) {
        Intent i = new Intent(getApplicationContext(), FormProjetoActivity.class);
        i.putExtra("projeto_id", projetoId);
        startActivity(i);
    }

    public void excluirProjeto(View v) {
        confirmarExclusaoProjeto();
    }

    public void onVoltarClick(View v) {
        finish();
    }

    @Override
    public void onEditarRecurso(RecursoAlocado recurso) {
        Intent i = new Intent(getApplicationContext(), FormRecursoActivity.class);
        i.putExtra("projeto_id", projetoId);
        i.putExtra("recurso_id", recurso.getId());
        startActivity(i);
    }

    @Override
    public void onExcluirRecurso(RecursoAlocado recurso) {
        confirmarExclusaoRecurso(recurso);
    }

    private void confirmarExclusaoRecurso(RecursoAlocado r) {
        String nome = r.getNomeTipo() != null ? r.getNomeTipo() : "Recurso";
        new AlertDialog.Builder(this)
                .setTitle("Excluir Recurso")
                .setMessage("Excluir \"" + nome + "\"?")
                .setPositiveButton("Excluir", (d, w) -> {
                    db.excluirRecurso(r.getId());
                    Toast.makeText(this, "Recurso excluído", Toast.LENGTH_SHORT).show();
                    carregarDashboard();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarExclusaoProjeto() {
        String nome = projeto != null ? projeto.getNome() : "este projeto";
        new AlertDialog.Builder(this)
                .setTitle("Excluir Projeto")
                .setMessage("Deseja realmente excluir o projeto \"" + nome + "\" e todos os seus recursos?")
                .setPositiveButton("Excluir", (d, w) -> {
                    db.excluirProjeto(projetoId);
                    Toast.makeText(this, "Projeto excluído", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
