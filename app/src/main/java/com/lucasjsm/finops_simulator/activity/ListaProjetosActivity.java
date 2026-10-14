package com.lucasjsm.finops_simulator.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.lucasjsm.finops_simulator.R;
import com.lucasjsm.finops_simulator.adapter.ProjetoAdapter;
import com.lucasjsm.finops_simulator.database.DatabaseHelper;
import com.lucasjsm.finops_simulator.model.Projeto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * Tela inicial – Lista de Projetos (UC01).
 */
public class ListaProjetosActivity extends AppCompatActivity {

    private ListView listViewProjetos;
    private TextView tvVazio;
    private DatabaseHelper db;
    private List<Projeto> projetos;
    private ProjetoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_projetos);
        setTitle("FinOps Simulator – Projetos");

        db = new DatabaseHelper(this);
        listViewProjetos = findViewById(R.id.listViewProjetos);
        tvVazio = findViewById(R.id.tvListaProjetosVazio);
        FloatingActionButton fabNovo = findViewById(R.id.fabNovoProjeto);

        fabNovo.setOnClickListener(v -> {
            Intent i = new Intent(ListaProjetosActivity.this, FormProjetoActivity.class);
            startActivity(i);
        });

        listViewProjetos.setOnItemClickListener((parent, view, position, id) -> {
            Projeto p = projetos.get(position);
            Intent i = new Intent(ListaProjetosActivity.this, DashboardActivity.class);
            i.putExtra("projeto_id", p.getId());
            startActivity(i);
        });

        listViewProjetos.setOnItemLongClickListener((parent, view, position, id) -> {
            Projeto p = projetos.get(position);
            mostrarOpcoesProjeto(p);
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarProjetos();
    }

    private void carregarProjetos() {
        projetos = db.listarProjetos();
        if (projetos.isEmpty()) {
            tvVazio.setVisibility(View.VISIBLE);
            listViewProjetos.setVisibility(View.GONE);
        } else {
            tvVazio.setVisibility(View.GONE);
            listViewProjetos.setVisibility(View.VISIBLE);
            adapter = new ProjetoAdapter(this, projetos);
            listViewProjetos.setAdapter(adapter);
        }
    }

    private void mostrarOpcoesProjeto(Projeto p) {
        String[] opcoes = {"Editar", "Excluir", "Cancelar"};
        new AlertDialog.Builder(this)
                .setTitle(p.getNome())
                .setItems(opcoes, (dialog, which) -> {
                    if (which == 0) {
                        Intent i = new Intent(ListaProjetosActivity.this, FormProjetoActivity.class);
                        i.putExtra("projeto_id", p.getId());
                        startActivity(i);
                    } else if (which == 1) {
                        confirmarExclusao(p);
                    }
                })
                .show();
    }

    private void confirmarExclusao(Projeto p) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir Projeto")
                .setMessage("Deseja realmente excluir o projeto \"" + p.getNome() + "\"?\nTodos os recursos alocados serão removidos.")
                .setPositiveButton("Excluir", (d, w) -> {
                    db.excluirProjeto(p.getId());
                    Toast.makeText(this, "Projeto excluído", Toast.LENGTH_SHORT).show();
                    carregarProjetos();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
