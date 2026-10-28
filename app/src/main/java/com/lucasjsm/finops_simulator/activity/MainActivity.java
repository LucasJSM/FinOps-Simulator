package com.lucasjsm.finops_simulator.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lucasjsm.finops_simulator.R;
import com.lucasjsm.finops_simulator.adapter.HomeProjetoAdapter;
import com.lucasjsm.finops_simulator.model.Projeto;
import com.lucasjsm.finops_simulator.model.ProjetoDAO;
import com.lucasjsm.finops_simulator.model.RecursoAlocadoDAO;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    private ListView listViewProjetos;
    private TextView tvVazio;
    private View headerView;
    private BottomNavigationView bottomNav;

    private ProjetoDAO projetoDao;
    private RecursoAlocadoDAO recursoDao;
    private HomeProjetoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        projetoDao = new ProjetoDAO(this);
        recursoDao = new RecursoAlocadoDAO(this);

        listViewProjetos = (ListView) findViewById(R.id.listViewMainProjetos);
        tvVazio = (TextView) findViewById(R.id.tvMainVazio);
        bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        headerView = LayoutInflater.from(this).inflate(R.layout.header_home, listViewProjetos, false);
        listViewProjetos.addHeaderView(headerView, null, false);

        bottomNav.getMenu().findItem(R.id.nav_inicio).setChecked(true);
        bottomNav.setOnNavigationItemSelectedListener(this);

        listViewProjetos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int itemPos = position - listViewProjetos.getHeaderViewsCount();
                if (itemPos >= 0 && adapter != null && itemPos < adapter.getCount()) {
                    Projeto p = adapter.getItem(itemPos);
                    if (p != null) {
                        Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                        intent.putExtra("projeto_id", p.getId());
                        startActivity(intent);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        projetoDao.open();
        recursoDao.open();
        carregarProjetos();
        bottomNav.getMenu().findItem(R.id.nav_inicio).setChecked(true);
    }

    @Override
    protected void onPause() {
        projetoDao.close();
        recursoDao.close();
        super.onPause();
    }

    private void carregarProjetos() {
        List<Projeto> listaProjetos = projetoDao.getAll();

        if (listaProjetos == null) {
            listaProjetos = new ArrayList<>();
        }

        for (Projeto p : listaProjetos) {
            double custoTotal = recursoDao.calcularCustoTotal(p.getId());
            p.setCustoTotal(custoTotal);
        }

        adapter = new HomeProjetoAdapter(this, listaProjetos);
        listViewProjetos.setAdapter(adapter);

        if (listaProjetos.isEmpty()) {
            tvVazio.setVisibility(View.VISIBLE);
        } else {
            tvVazio.setVisibility(View.GONE);
        }
    }

    public void onCriarProjetoClick(View v) {
        Intent intent = new Intent(getApplicationContext(), FormProjetoActivity.class);
        intent.putExtra("acao", -1);
        intent.putExtra("id", 0L);
        startActivity(intent);
    }

    public void onVerTodosClick(View v) {
        Intent intent = new Intent(getApplicationContext(), ListaProjetosActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_inicio) {
            return true;
        } else if (id == R.id.nav_projetos) {
            startActivity(new Intent(MainActivity.this, ListaProjetosActivity.class));
            return true;
        } else if (id == R.id.nav_catalogo) {
            startActivity(new Intent(MainActivity.this, ListaTiposActivity.class));
            return true;
        }
        return false;
    }
}
