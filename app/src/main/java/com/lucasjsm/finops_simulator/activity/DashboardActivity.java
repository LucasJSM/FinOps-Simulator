package com.lucasjsm.finops_simulator.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.lucasjsm.finops_simulator.R;
import com.lucasjsm.finops_simulator.adapter.RecursoAdapter;
import com.lucasjsm.finops_simulator.database.DatabaseHelper;
import com.lucasjsm.finops_simulator.model.Projeto;
import com.lucasjsm.finops_simulator.model.RecursoAlocado;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private TextView tvNome, tvProvedor, tvOrcamento, tvCustoTotal, tvPercentual, tvVazio;
    private ListView listViewRecursos;
    private DatabaseHelper db;
    private long projetoId;
    private Projeto projeto;
    private List<RecursoAlocado> recursos;
    private RecursoAdapter adapter;

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

        tvNome = findViewById(R.id.tvDashNome);
        tvProvedor = findViewById(R.id.tvDashProvedor);
        tvOrcamento = findViewById(R.id.tvDashOrcamento);
        tvCustoTotal = findViewById(R.id.tvDashCustoTotal);
        tvPercentual = findViewById(R.id.tvDashPercentual);
        tvVazio = findViewById(R.id.tvDashVazio);
        listViewRecursos = findViewById(R.id.listViewRecursos);

        listViewRecursos.setOnItemClickListener(this);
        listViewRecursos.setOnItemLongClickListener(this);
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

        setTitle("Dashboard – " + projeto.getNome());
        tvNome.setText(projeto.getNome());
        tvProvedor.setText("Provedor: " + projeto.getProvedor());
        tvOrcamento.setText(String.format(Locale.US, "Orçamento: US$ %.2f", projeto.getOrcamentoMaximo()));
        tvCustoTotal.setText(String.format(Locale.US, "Custo Total: US$ %.2f", projeto.getCustoTotal()));

        if (projeto.getOrcamentoMaximo() > 0) {
            double pct = (projeto.getCustoTotal() / projeto.getOrcamentoMaximo()) * 100;
            tvPercentual.setText(String.format(Locale.US, "%.1f%% do orçamento", pct));
            if (pct > 100) {
                tvPercentual.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            } else {
                tvPercentual.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            }
        } else {
            tvPercentual.setText("Orçamento não definido");
        }

        recursos = db.listarRecursosPorProjeto(projetoId);
        if (recursos.isEmpty()) {
            tvVazio.setVisibility(View.VISIBLE);
            listViewRecursos.setVisibility(View.GONE);
        } else {
            tvVazio.setVisibility(View.GONE);
            listViewRecursos.setVisibility(View.VISIBLE);
            adapter = new RecursoAdapter(this, recursos);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        RecursoAlocado r = recursos.get(position);
        Intent i = new Intent(getApplicationContext(), FormRecursoActivity.class);
        i.putExtra("projeto_id", projetoId);
        i.putExtra("recurso_id", r.getId());
        startActivity(i);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        RecursoAlocado r = recursos.get(position);
        confirmarExclusaoRecurso(r);
        return true;
    }

    private void confirmarExclusaoRecurso(RecursoAlocado r) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir Recurso")
                .setMessage("Excluir \"" + r.getNomeTipo() + "\"?")
                .setPositiveButton("Excluir", (d, w) -> {
                    db.excluirRecurso(r.getId());
                    Toast.makeText(this, "Recurso excluído", Toast.LENGTH_SHORT).show();
                    carregarDashboard();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarExclusaoProjeto() {
        new AlertDialog.Builder(this)
                .setTitle("Excluir Projeto")
                .setMessage("Deseja realmente excluir o projeto \"" + projeto.getNome() + "\" e todos os seus recursos?")
                .setPositiveButton("Excluir", (d, w) -> {
                    db.excluirProjeto(projetoId);
                    Toast.makeText(this, "Projeto excluído", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
