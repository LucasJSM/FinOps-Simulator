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
import com.lucasjsm.finops_simulator.adapter.TipoAdapter;
import com.lucasjsm.finops_simulator.database.DatabaseHelper;
import com.lucasjsm.finops_simulator.model.TipoRecurso;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * Lista de Tipos de Recurso – catálogo (UC06).
 */
public class ListaTiposActivity extends AppCompatActivity {

    private ListView listViewTipos;
    private TextView tvVazio;
    private DatabaseHelper db;
    private List<TipoRecurso> tipos;
    private TipoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_tipos);
        setTitle("Catálogo de Tipos de Recurso");

        db = new DatabaseHelper(this);
        listViewTipos = findViewById(R.id.listViewTipos);
        tvVazio = findViewById(R.id.tvListaTiposVazio);
        FloatingActionButton fabNovo = findViewById(R.id.fabNovoTipo);

        fabNovo.setOnClickListener(v -> {
            Intent i = new Intent(ListaTiposActivity.this, FormTipoActivity.class);
            startActivity(i);
        });

        listViewTipos.setOnItemClickListener((parent, view, position, id) -> {
            TipoRecurso t = tipos.get(position);
            Intent i = new Intent(ListaTiposActivity.this, FormTipoActivity.class);
            i.putExtra("tipo_id", t.getId());
            startActivity(i);
        });

        listViewTipos.setOnItemLongClickListener((parent, view, position, id) -> {
            TipoRecurso t = tipos.get(position);
            confirmarExclusao(t);
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarTipos();
    }

    private void carregarTipos() {
        tipos = db.listarTipos();
        if (tipos.isEmpty()) {
            tvVazio.setVisibility(View.VISIBLE);
            listViewTipos.setVisibility(View.GONE);
        } else {
            tvVazio.setVisibility(View.GONE);
            listViewTipos.setVisibility(View.VISIBLE);
            adapter = new TipoAdapter(this, tipos);
            listViewTipos.setAdapter(adapter);
        }
    }

    private void confirmarExclusao(TipoRecurso t) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir Tipo")
                .setMessage("Excluir \"" + t.getNome() + "\" (" + t.getProvedor() + ")?")
                .setPositiveButton("Excluir", (d, w) -> {
                    int result = db.excluirTipo(t.getId());
                    if (result == -1) {
                        Toast.makeText(this, "Não é possível excluir: tipo está em uso por recursos alocados", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Tipo excluído", Toast.LENGTH_SHORT).show();
                        carregarTipos();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
