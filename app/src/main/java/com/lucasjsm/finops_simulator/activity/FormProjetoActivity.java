package com.lucasjsm.finops_simulator.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lucasjsm.finops_simulator.R;
import com.lucasjsm.finops_simulator.database.DatabaseHelper;
import com.lucasjsm.finops_simulator.model.Projeto;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Formulário de criação/edição de Projeto.
 * Layout alinhado ao protótipo: cabeçalho azul, campos com ícones, Spinner de provedor.
 */
public class FormProjetoActivity extends AppCompatActivity {

    private EditText etNome, etDescricao, etOrcamento;
    private Spinner spProvedor;
    private TextView tvTitulo, tvSubtitulo;
    private DatabaseHelper db;
    private long projetoId = -1;
    private Projeto projetoEdit;
    private BottomNavigationView bottomNav;

    private static final String[] PROVEDORES = {"AWS", "GCP", "Azure", "OCI"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_projeto);

        db = new DatabaseHelper(this);

        etNome = (EditText) findViewById(R.id.etProjetoNome);
        etDescricao = (EditText) findViewById(R.id.etProjetoDescricao);
        etOrcamento = (EditText) findViewById(R.id.etProjetoOrcamento);
        spProvedor = (Spinner) findViewById(R.id.spProjetoProvedor);
        tvTitulo = (TextView) findViewById(R.id.tvFormProjetoTitulo);
        tvSubtitulo = (TextView) findViewById(R.id.tvFormProjetoSubtitulo);
        bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        ArrayAdapter<String> adapterProv = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, PROVEDORES);
        adapterProv.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProvedor.setAdapter(adapterProv);

        projetoId = getIntent().getLongExtra("projeto_id", -1);
        if (projetoId > 0) {
            if (tvTitulo != null) {
                tvTitulo.setText("Editar Projeto");
            }
            if (tvSubtitulo != null) {
                tvSubtitulo.setText("Atualize os parâmetros da simulação");
            }
            carregarProjeto();
            // Provedor não pode ser alterado após a criação
            spProvedor.setEnabled(false);
        } else {
            if (tvTitulo != null) {
                tvTitulo.setText("Criar Projeto");
            }
            if (tvSubtitulo != null) {
                tvSubtitulo.setText("Defina os parâmetros base da simulação");
            }
        }

        // Bottom Navigation — nenhum item "ativo" específico do formulário;
        // mantém Projetos como referência visual de contexto
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_projetos);
            bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();
                    if (id == R.id.nav_inicio) {
                        startActivity(new Intent(FormProjetoActivity.this, HomeActivity.class));
                        finish();
                        return true;
                    } else if (id == R.id.nav_projetos) {
                        startActivity(new Intent(FormProjetoActivity.this, ListaProjetosActivity.class));
                        finish();
                        return true;
                    } else if (id == R.id.nav_catalogo) {
                        Toast.makeText(FormProjetoActivity.this, "Catálogo em breve", Toast.LENGTH_SHORT).show();
                        return true;
                    } else if (id == R.id.nav_dashboard) {
                        startActivity(new Intent(FormProjetoActivity.this, ListaProjetosActivity.class));
                        finish();
                        return true;
                    } else if (id == R.id.nav_sobre) {
                        Toast.makeText(FormProjetoActivity.this, "Sobre em breve", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    private void carregarProjeto() {
        projetoEdit = db.buscarProjetoPorId(projetoId);
        if (projetoEdit != null) {
            etNome.setText(projetoEdit.getNome());
            etDescricao.setText(projetoEdit.getDescricao());
            etOrcamento.setText(String.valueOf(projetoEdit.getOrcamentoMaximo()));
            for (int i = 0; i < PROVEDORES.length; i++) {
                if (PROVEDORES[i].equals(projetoEdit.getProvedor())) {
                    spProvedor.setSelection(i);
                    break;
                }
            }
        }
    }

    /** Botão voltar do cabeçalho azul */
    public void onVoltarClick(View v) {
        finish();
    }

    /**
     * Salva o projeto no SQLite.
     * - Criação: redireciona para DashboardActivity com o projeto_id e fecha esta tela.
     * - Edição: apenas fecha (volta à tela anterior).
     */
    public void salvar(View v) {
        String nome = etNome.getText().toString().trim();
        String descricao = etDescricao.getText().toString().trim();
        String provedor = spProvedor.getSelectedItem().toString();
        String orcStr = etOrcamento.getText().toString().trim();

        if (nome.isEmpty()) {
            etNome.setError("Nome obrigatório");
            etNome.requestFocus();
            return;
        }

        double orcamento = 0;
        if (!orcStr.isEmpty()) {
            try {
                orcamento = Double.parseDouble(orcStr.replace(",", "."));
                if (orcamento < 0) {
                    etOrcamento.setError("Orçamento não pode ser negativo");
                    return;
                }
            } catch (NumberFormatException e) {
                etOrcamento.setError("Valor inválido");
                return;
            }
        }

        if (projetoId > 0 && projetoEdit != null) {
            // Edição: mantém provedor original
            projetoEdit.setNome(nome);
            projetoEdit.setDescricao(descricao);
            projetoEdit.setOrcamentoMaximo(orcamento);
            db.atualizarProjeto(projetoEdit);
            Toast.makeText(this, "Projeto atualizado", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            // Criação: grava e abre o Dashboard do novo projeto
            String data = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
            Projeto p = new Projeto(nome, descricao, provedor, orcamento, data);
            long id = db.inserirProjeto(p);
            if (id > 0) {
                Toast.makeText(this, "Projeto criado", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
                i.putExtra("projeto_id", id);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(this, "Erro ao criar projeto", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
