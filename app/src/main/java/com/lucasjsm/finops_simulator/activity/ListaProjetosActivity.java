package com.lucasjsm.finops_simulator.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lucasjsm.finops_simulator.R;
import com.lucasjsm.finops_simulator.adapter.ProjetoAdapter;
import com.lucasjsm.finops_simulator.model.Projeto;
import com.lucasjsm.finops_simulator.model.ProjetoDAO;
import com.lucasjsm.finops_simulator.model.RecursoAlocadoDAO;

import java.util.ArrayList;
import java.util.List;

public class ListaProjetosActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private ListView listViewProjetos;
    private TextView tvVazio;
    private BottomNavigationView bottomNav;

    private ProjetoDAO projetoDao;
    private RecursoAlocadoDAO recursoDao;
    private List<Projeto> listaProjetos;
    private ProjetoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_projetos);

        projetoDao = new ProjetoDAO(this);
        recursoDao = new RecursoAlocadoDAO(this);

        listViewProjetos = (ListView) findViewById(R.id.listViewProjetos);
        tvVazio = (TextView) findViewById(R.id.tvListaProjetosVazio);
        bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        listViewProjetos.setOnItemClickListener(this);

        bottomNav.getMenu().findItem(R.id.nav_projetos).setChecked(true);
        bottomNav.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        projetoDao.open();
        recursoDao.open();
        carregarProjetos();
        bottomNav.getMenu().findItem(R.id.nav_projetos).setChecked(true);
    }

    @Override
    protected void onPause() {
        projetoDao.close();
        recursoDao.close();
        super.onPause();
    }

    private void carregarProjetos() {
        listaProjetos = projetoDao.getAll();

        if (listaProjetos == null) {
            listaProjetos = new ArrayList<>();
        }

        for (Projeto p : listaProjetos) {
            double custoTotal = recursoDao.calcularCustoTotal(p.getId());
            p.setCustoTotal(custoTotal);
        }

        adapter = new ProjetoAdapter(this, listaProjetos);
        listViewProjetos.setAdapter(adapter);

        if (listaProjetos.isEmpty()) {
            tvVazio.setVisibility(View.VISIBLE);
            listViewProjetos.setVisibility(View.GONE);
        } else {
            tvVazio.setVisibility(View.GONE);
            listViewProjetos.setVisibility(View.VISIBLE);
        }
    }

    public void novoProjeto(View v) {
        Intent intent = new Intent(getApplicationContext(), FormProjetoActivity.class);
        intent.putExtra("acao", -1);
        intent.putExtra("id", 0L);
        startActivity(intent);
    }

    public void onVoltarClick(View v) {
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (listaProjetos == null || position < 0 || position >= listaProjetos.size()) {
            return;
        }
        Projeto p = listaProjetos.get(position);
        Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
        i.putExtra("projeto_id", p.getId());
        startActivity(i);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_projetos) {
            return true;
        } else if (id == R.id.nav_inicio) {
            Intent intent = new Intent(ListaProjetosActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.nav_catalogo) {
            startActivity(new Intent(ListaProjetosActivity.this, ListaTiposActivity.class));
            finish();
            return true;
        }
        return false;
    }
}
