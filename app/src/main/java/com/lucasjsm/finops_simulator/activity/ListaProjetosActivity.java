package com.lucasjsm.finops_simulator.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lucasjsm.finops_simulator.R;
import com.lucasjsm.finops_simulator.adapter.ProjetoAdapter;
import com.lucasjsm.finops_simulator.database.DatabaseHelper;
import com.lucasjsm.finops_simulator.model.Projeto;

import java.util.List;

public class ListaProjetosActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private ListView listViewProjetos;
    private TextView tvVazio;
    private DatabaseHelper db;
    private List<Projeto> projetos;
    private ProjetoAdapter adapter;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_projetos);

        db = new DatabaseHelper(this);

        listViewProjetos = (ListView) findViewById(R.id.listViewProjetos);
        tvVazio = (TextView) findViewById(R.id.tvListaProjetosVazio);
        bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        listViewProjetos.setOnItemClickListener(this);
        listViewProjetos.setOnItemLongClickListener(this);

        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_projetos);
            bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();
                    if (id == R.id.nav_inicio) {
                        startActivity(new Intent(ListaProjetosActivity.this, HomeActivity.class));
                        finish();
                        return true;
                    } else if (id == R.id.nav_projetos) {
                        return true;
                    } else if (id == R.id.nav_catalogo) {
                        Toast.makeText(ListaProjetosActivity.this, "Catálogo em breve", Toast.LENGTH_SHORT).show();
                        return true;
                    } else if (id == R.id.nav_dashboard) {
                        // Sem projeto específico: permanece na lista
                        return true;
                    } else if (id == R.id.nav_sobre) {
                        Toast.makeText(ListaProjetosActivity.this, "Sobre em breve", Toast.LENGTH_SHORT).show();
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
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_projetos);
        }
        carregarProjetos();
    }

    private void carregarProjetos() {
        projetos = db.listarProjetos();

        if (projetos == null || projetos.isEmpty()) {
            tvVazio.setVisibility(View.VISIBLE);
            listViewProjetos.setVisibility(View.GONE);
        } else {
            tvVazio.setVisibility(View.GONE);
            listViewProjetos.setVisibility(View.VISIBLE);
            adapter = new ProjetoAdapter(this, projetos, db);
            listViewProjetos.setAdapter(adapter);
        }
    }

    public void novoProjeto(View v) {
        Intent i = new Intent(getApplicationContext(), FormProjetoActivity.class);
        startActivity(i);
    }

    public void onVoltarClick(View v) {
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (projetos == null || position < 0 || position >= projetos.size()) {
            return;
        }
        Projeto p = projetos.get(position);
        Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
        i.putExtra("projeto_id", p.getId());
        startActivity(i);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (projetos == null || position < 0 || position >= projetos.size()) {
            return true;
        }
        Projeto p = projetos.get(position);
        mostrarOpcoesProjeto(p);
        return true;
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
                .setMessage("Deseja realmente excluir o projeto \"" + p.getNome()
                        + "\"?\nTodos os recursos alocados serão removidos.")
                .setPositiveButton("Excluir", (d, w) -> {
                    db.excluirProjeto(p.getId());
                    Toast.makeText(this, "Projeto excluído", Toast.LENGTH_SHORT).show();
                    carregarProjetos();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
