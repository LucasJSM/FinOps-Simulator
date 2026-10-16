package com.lucasjsm.finops_simulator.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lucasjsm.finops_simulator.R;
import com.lucasjsm.finops_simulator.adapter.HomeProjetoAdapter;
import com.lucasjsm.finops_simulator.database.DatabaseHelper;
import com.lucasjsm.finops_simulator.model.Projeto;

import java.util.List;

/**
 * Tela inicial (Home) do FinOps Simulator.
 * Exibe hero card, atalho para criar projeto e lista resumida de projetos.
 */
public class HomeActivity extends AppCompatActivity {

    private ListView listViewProjetos;
    private TextView tvVazio;
    private DatabaseHelper db;
    private List<Projeto> projetos;
    private HomeProjetoAdapter adapter;
    private BottomNavigationView bottomNav;
    private View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = new DatabaseHelper(this);

        listViewProjetos = (ListView) findViewById(R.id.listViewHomeProjetos);
        tvVazio = (TextView) findViewById(R.id.tvHomeVazio);
        bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        // Header da ListView (Hero + seção "Selecione um projeto")
        headerView = LayoutInflater.from(this).inflate(R.layout.header_home, listViewProjetos, false);
        listViewProjetos.addHeaderView(headerView, null, false);

        // Bottom Navigation — aba Início selecionada
        bottomNav.setSelectedItemId(R.id.nav_inicio);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_inicio) {
                    // Já estamos na Home
                    return true;
                } else if (id == R.id.nav_projetos) {
                    startActivity(new Intent(HomeActivity.this, ListaProjetosActivity.class));
                    return true;
                } else if (id == R.id.nav_catalogo) {
                    // Catálogo ainda não implementado nesta etapa
                    Toast.makeText(HomeActivity.this, "Catálogo em breve", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.nav_dashboard) {
                    // Abre a lista de projetos para o usuário escolher qual dashboard ver
                    startActivity(new Intent(HomeActivity.this, ListaProjetosActivity.class));
                    return true;
                } else if (id == R.id.nav_sobre) {
                    Toast.makeText(HomeActivity.this, "Sobre em breve", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Garante que a aba Início continue marcada ao voltar
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_inicio);
        }
        carregarProjetos();
    }

    private void carregarProjetos() {
        projetos = db.listarProjetos();

        if (projetos == null || projetos.isEmpty()) {
            // Mantém o header visível; esconde a lista de itens e mostra mensagem
            listViewProjetos.setVisibility(View.VISIBLE);
            // Adapter vazio para não mostrar itens
            adapter = new HomeProjetoAdapter(this, projetos != null ? projetos : new java.util.ArrayList<Projeto>());
            listViewProjetos.setAdapter(adapter);
            tvVazio.setVisibility(View.GONE);
        } else {
            tvVazio.setVisibility(View.GONE);
            listViewProjetos.setVisibility(View.VISIBLE);
            adapter = new HomeProjetoAdapter(this, projetos);
            listViewProjetos.setAdapter(adapter);
        }
    }

    /** Botão do Hero: Criar Novo Projeto */
    public void onCriarProjetoClick(View v) {
        Intent i = new Intent(getApplicationContext(), FormProjetoActivity.class);
        startActivity(i);
    }

    /** Link "Ver todos" → Lista completa de projetos */
    public void onVerTodosClick(View v) {
        Intent i = new Intent(getApplicationContext(), ListaProjetosActivity.class);
        startActivity(i);
    }

    /** Botão info do header → placeholder Sobre */
    public void onInfoClick(View v) {
        Toast.makeText(this, "Sobre o aplicativo em breve", Toast.LENGTH_SHORT).show();
    }
}
