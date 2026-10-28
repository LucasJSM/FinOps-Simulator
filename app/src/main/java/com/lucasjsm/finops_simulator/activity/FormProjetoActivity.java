package com.lucasjsm.finops_simulator.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lucasjsm.finops_simulator.R;
import com.lucasjsm.finops_simulator.model.Projeto;
import com.lucasjsm.finops_simulator.model.ProjetoDAO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormProjetoActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    private EditText edNome, edDescricao, edOrcamento;
    private Spinner spProvedor;
    private TextView tvTitulo, tvSubtitulo;
    private Button btnSalvar;
    private BottomNavigationView bottomNav;

    private int acao;
    private long id;
    private ProjetoDAO dao;

    private static final String[] PROVEDORES = {"AWS", "GCP", "Azure", "OCI"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_projeto);

        dao = new ProjetoDAO(this);

        edNome = (EditText) findViewById(R.id.etTratarProjetoNome);
        edDescricao = (EditText) findViewById(R.id.etTratarProjetoDescricao);
        edOrcamento = (EditText) findViewById(R.id.etTratarProjetoOrcamento);
        spProvedor = (Spinner) findViewById(R.id.spTratarProjetoProvedor);
        tvTitulo = (TextView) findViewById(R.id.tvTratarProjetoTitulo);
        tvSubtitulo = (TextView) findViewById(R.id.tvTratarProjetoSubtitulo);
        btnSalvar = (Button) findViewById(R.id.btnTratarProjetoAcao);
        bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        ArrayAdapter<String> adapterProv = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, PROVEDORES);
        adapterProv.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProvedor.setAdapter(adapterProv);

        bottomNav.getMenu().findItem(R.id.nav_projetos).setChecked(true);
        bottomNav.setOnNavigationItemSelectedListener(this);

        // Recebe os parâmetros via Intent: acao (-1 = criação, 0 = edição) e id
        acao = getIntent().getExtras().getInt("acao");
        id = getIntent().getExtras().getLong("id");

        if (acao == -1) {
            // Criação de Projeto
            setTitle("Criar Projeto");
            if (tvTitulo != null) tvTitulo.setText("Criar Projeto");
            if (tvSubtitulo != null) tvSubtitulo.setText("Defina os parâmetros base da simulação");
        } else {
            // Edição de Projeto
            setTitle("Editar Projeto");
            if (tvTitulo != null) tvTitulo.setText("Editar Projeto");
            if (tvSubtitulo != null) tvSubtitulo.setText("Atualize os parâmetros da simulação");

            dao.open();
            Projeto aux = dao.buscar(id);
            if (aux != null) {
                edNome.setText(aux.getNome());
                edDescricao.setText(aux.getDescricao());
                edOrcamento.setText(String.format(Locale.getDefault(), "%.1f", aux.getOrcamentoMaximo()).replace(",", "."));
                for (int i = 0; i < PROVEDORES.length; i++) {
                    if (PROVEDORES[i].equalsIgnoreCase(aux.getProvedor())) {
                        spProvedor.setSelection(i);
                        break;
                    }
                }
            }
            dao.close();
            spProvedor.setEnabled(false);
        }
    }

    // Salvar (criação ou alteração)
    public void salvar(View v) {
        String nome = edNome.getText().toString().trim();
        String descricao = edDescricao.getText().toString().trim();
        String provedor = spProvedor.getSelectedItem().toString();
        String orcStr = edOrcamento.getText().toString().trim();

        if (nome.isEmpty()) {
            edNome.setError("Nome obrigatório");
            edNome.requestFocus();
            return;
        }

        double orcamento = 0.0;
        if (!orcStr.isEmpty()) {
            try {
                orcamento = Double.parseDouble(orcStr.replace(",", "."));
            } catch (NumberFormatException e) {
                edOrcamento.setError("Valor inválido");
                return;
            }
        }

        dao.open();
        if (acao == -1) {
            String data = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
            dao.inserir(nome, descricao, provedor, orcamento, data);
            Toast.makeText(this, "Projeto criado com sucesso!", Toast.LENGTH_SHORT).show();
        } else {
            dao.alterar(id, nome, descricao, orcamento);
            Toast.makeText(this, "Projeto atualizado com sucesso!", Toast.LENGTH_SHORT).show();
        }
        dao.close();
        finish();
    }

    public void voltar(View v) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNav != null) {
            bottomNav.getMenu().findItem(R.id.nav_projetos).setChecked(true);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int navId = item.getItemId();
        if (navId == R.id.nav_projetos) {
            finish();
            return true;
        } else if (navId == R.id.nav_inicio) {
            Intent intent = new Intent(FormProjetoActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
            return true;
        } else if (navId == R.id.nav_catalogo) {
            startActivity(new Intent(FormProjetoActivity.this, ListaTiposActivity.class));
            finish();
            return true;
        }
        return false;
    }
}
