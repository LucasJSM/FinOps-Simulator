package com.lucasjsm.finops_simulator.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lucasjsm.finops_simulator.R;
import com.lucasjsm.finops_simulator.adapter.TipoRecursoAdapter;
import com.lucasjsm.finops_simulator.model.TipoRecurso;
import com.lucasjsm.finops_simulator.model.TipoRecursoDAO;

import java.util.List;
import java.util.Locale;

public class ListaTiposActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener,
        BottomNavigationView.OnNavigationItemSelectedListener {

    private Spinner spProvedor;
    private ListView listViewTipos;
    private TextView tvVazio;
    private BottomNavigationView bottomNav;

    private TipoRecursoDAO tipoDao;
    private List<TipoRecurso> tipos;
    private TipoRecursoAdapter adapter;

    private static final String[] FILTROS_PROVEDOR = {
            "Todos os Provedores", "AWS", "GCP", "Azure", "OCI"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_tipos);

        tipoDao = new TipoRecursoDAO(this);

        spProvedor = (Spinner) findViewById(R.id.spTiposProvedor);
        listViewTipos = (ListView) findViewById(R.id.listViewTipos);
        tvVazio = (TextView) findViewById(R.id.tvTiposVazio);
        bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        ArrayAdapter<String> adapterFiltro = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, FILTROS_PROVEDOR);
        adapterFiltro.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProvedor.setAdapter(adapterFiltro);

        spProvedor.setOnItemSelectedListener(this);
        listViewTipos.setOnItemClickListener(this);

        bottomNav.getMenu().findItem(R.id.nav_catalogo).setChecked(true);
        bottomNav.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tipoDao.open();
        carregarTipos();
        bottomNav.getMenu().findItem(R.id.nav_catalogo).setChecked(true);
    }

    @Override
    protected void onPause() {
        tipoDao.close();
        super.onPause();
    }

    private void carregarTipos() {
        int pos = spProvedor.getSelectedItemPosition();
        if (pos <= 0) {
            tipos = tipoDao.getAll();
        } else {
            String provedorSelecionado = FILTROS_PROVEDOR[pos];
            tipos = tipoDao.getAllPorProvedor(provedorSelecionado);
        }

        if (tipos == null || tipos.isEmpty()) {
            tvVazio.setVisibility(View.VISIBLE);
            listViewTipos.setVisibility(View.GONE);
        } else {
            tvVazio.setVisibility(View.GONE);
            listViewTipos.setVisibility(View.VISIBLE);
            adapter = new TipoRecursoAdapter(this, tipos);
            listViewTipos.setAdapter(adapter);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        tipoDao.open();
        carregarTipos();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (tipos == null || position < 0 || position >= tipos.size()) {
            return;
        }
        TipoRecurso t = tipos.get(position);
        String msg = String.format(Locale.getDefault(),
                "%s (%s)\nCusto: R$ %.3f / %s\n%s",
                t.getNome(), t.getProvedor(), t.getCustoUnitario(), t.getUnidade(),
                t.getDescricao() != null ? t.getDescricao() : "");
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void voltar(View v) {
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_catalogo) {
            return true;
        } else if (id == R.id.nav_inicio) {
            Intent intent = new Intent(ListaTiposActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.nav_projetos) {
            startActivity(new Intent(ListaTiposActivity.this, ListaProjetosActivity.class));
            finish();
            return true;
        }
        return false;
    }
}
