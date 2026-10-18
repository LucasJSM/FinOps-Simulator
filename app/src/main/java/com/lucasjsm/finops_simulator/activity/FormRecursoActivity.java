package com.lucasjsm.finops_simulator.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import com.lucasjsm.finops_simulator.model.RecursoAlocado;
import com.lucasjsm.finops_simulator.model.TipoRecurso;

import java.util.List;
import java.util.Locale;

/**
 * Formulário de adição/edição de recurso alocado.
 * Preview de custo atualiza no blur e ao digitar (multiplicador 730h quando unidade = hora).
 */
public class FormRecursoActivity extends AppCompatActivity {

    private Spinner spTipo;
    private EditText etQuantidade, etPeriodo, etObs;
    private TextView tvCustoPreview, tvProvedorInfo, tvTitulo;
    private DatabaseHelper db;
    private long projetoId;
    private long recursoId = -1;
    private Projeto projeto;
    private List<TipoRecurso> tipos;
    private RecursoAlocado recursoEdit;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_recurso);

        db = new DatabaseHelper(this);
        projetoId = getIntent().getLongExtra("projeto_id", -1);
        recursoId = getIntent().getLongExtra("recurso_id", -1);

        if (projetoId < 0) {
            Toast.makeText(this, "Projeto inválido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        projeto = db.buscarProjetoPorId(projetoId);
        if (projeto == null) {
            Toast.makeText(this, "Projeto não encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        spTipo = (Spinner) findViewById(R.id.spRecursoTipo);
        etQuantidade = (EditText) findViewById(R.id.etRecursoQuantidade);
        etPeriodo = (EditText) findViewById(R.id.etRecursoPeriodo);
        etObs = (EditText) findViewById(R.id.etRecursoObs);
        tvCustoPreview = (TextView) findViewById(R.id.tvRecursoCustoPreview);
        tvProvedorInfo = (TextView) findViewById(R.id.tvRecursoProvedorInfo);
        tvTitulo = (TextView) findViewById(R.id.tvFormRecursoTitulo);
        bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        // Subtítulo: Nome do projeto • Provedor
        tvProvedorInfo.setText(projeto.getNome() + " • Provedor " + projeto.getProvedor());

        tipos = db.listarTiposPorProvedor(projeto.getProvedor());
        if (tipos.isEmpty()) {
            Toast.makeText(this, "Não há tipos de recurso para " + projeto.getProvedor()
                    + ". Cadastre tipos primeiro.", Toast.LENGTH_LONG).show();
        }

        // Adapter nativo do Spinner (usa TipoRecurso.toString())
        ArrayAdapter<TipoRecurso> adapterTipo = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, tipos);
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipo.setAdapter(adapterTipo);

        spTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                atualizarPreview();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (recursoId > 0) {
            if (tvTitulo != null) {
                tvTitulo.setText("Editar Recurso");
            }
            carregarRecurso();
            spTipo.setEnabled(false);
        } else {
            if (tvTitulo != null) {
                tvTitulo.setText("Adicionar Recurso");
            }
        }

        // Preview ao perder foco
        View.OnFocusChangeListener previewListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    atualizarPreview();
                }
            }
        };
        etQuantidade.setOnFocusChangeListener(previewListener);
        etPeriodo.setOnFocusChangeListener(previewListener);

        // Preview enquanto digita
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                atualizarPreview();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        etQuantidade.addTextChangedListener(watcher);
        etPeriodo.addTextChangedListener(watcher);

        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_catalogo);
            bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();
                    if (id == R.id.nav_inicio) {
                        startActivity(new Intent(FormRecursoActivity.this, HomeActivity.class));
                        finish();
                        return true;
                    } else if (id == R.id.nav_projetos) {
                        startActivity(new Intent(FormRecursoActivity.this, ListaProjetosActivity.class));
                        finish();
                        return true;
                    } else if (id == R.id.nav_catalogo) {
                        Toast.makeText(FormRecursoActivity.this, "Catálogo em breve", Toast.LENGTH_SHORT).show();
                        return true;
                    } else if (id == R.id.nav_dashboard) {
                        Intent i = new Intent(FormRecursoActivity.this, DashboardActivity.class);
                        i.putExtra("projeto_id", projetoId);
                        startActivity(i);
                        finish();
                        return true;
                    } else if (id == R.id.nav_sobre) {
                        Toast.makeText(FormRecursoActivity.this, "Sobre em breve", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    private void carregarRecurso() {
        recursoEdit = db.buscarRecursoPorId(recursoId);
        if (recursoEdit != null) {
            etQuantidade.setText(String.valueOf(recursoEdit.getQuantidade()));
            etPeriodo.setText(String.valueOf(recursoEdit.getPeriodo()));
            etObs.setText(recursoEdit.getObservacao());
            for (int i = 0; i < tipos.size(); i++) {
                if (tipos.get(i).getId() == recursoEdit.getTipoRecursoId()) {
                    spTipo.setSelection(i);
                    break;
                }
            }
            atualizarPreview();
        }
    }

    /**
     * Atualiza o card verde de custo estimado.
     * Multiplicador 730 quando a unidade do tipo é "hora".
     */
    private void atualizarPreview() {
        try {
            String qtdStr = etQuantidade.getText().toString().trim();
            String perStr = etPeriodo.getText().toString().trim();
            if (qtdStr.isEmpty() || perStr.isEmpty() || spTipo.getSelectedItem() == null) {
                tvCustoPreview.setText("R$ -");
                return;
            }

            double qtd = Double.parseDouble(qtdStr.replace(",", "."));
            double per = Double.parseDouble(perStr.replace(",", "."));
            TipoRecurso t = (TipoRecurso) spTipo.getSelectedItem();

            double multiplicadorTempo = 1.0;
            if (t.getUnidade() != null && t.getUnidade().trim().equalsIgnoreCase("hora")) {
                multiplicadorTempo = 730.0;
            }

            double custo = qtd * t.getCustoUnitario() * multiplicadorTempo * per;
            tvCustoPreview.setText(String.format(Locale.getDefault(), "R$ %.2f", custo));
        } catch (Exception e) {
            tvCustoPreview.setText("R$ -");
        }
    }

    public void onVoltarClick(View v) {
        finish();
    }

    public void salvar(View v) {
        if (tipos == null || tipos.isEmpty()) {
            Toast.makeText(this, "Cadastre tipos de recurso para este provedor primeiro",
                    Toast.LENGTH_LONG).show();
            return;
        }

        String qtdStr = etQuantidade.getText().toString().trim();
        String perStr = etPeriodo.getText().toString().trim();
        String obs = etObs.getText().toString().trim();

        if (qtdStr.isEmpty()) {
            etQuantidade.setError("Volume obrigatório");
            etQuantidade.requestFocus();
            return;
        }
        if (perStr.isEmpty()) {
            etPeriodo.setError("Período obrigatório");
            etPeriodo.requestFocus();
            return;
        }

        double quantidade, periodo;
        try {
            quantidade = Double.parseDouble(qtdStr.replace(",", "."));
            periodo = Double.parseDouble(perStr.replace(",", "."));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Valores numéricos inválidos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (quantidade <= 0) {
            etQuantidade.setError("Deve ser maior que zero");
            return;
        }
        if (periodo <= 0) {
            etPeriodo.setError("Deve ser maior que zero");
            return;
        }

        TipoRecurso tipoSelecionado = (TipoRecurso) spTipo.getSelectedItem();
        if (tipoSelecionado == null) {
            Toast.makeText(this, "Selecione um tipo de recurso", Toast.LENGTH_SHORT).show();
            return;
        }

        if (recursoId > 0 && recursoEdit != null) {
            recursoEdit.setQuantidade(quantidade);
            recursoEdit.setPeriodo(periodo);
            recursoEdit.setObservacao(obs);
            recursoEdit.calcularCusto(tipoSelecionado.getCustoUnitario(), tipoSelecionado.getUnidade());
            db.atualizarRecurso(recursoEdit);
            Toast.makeText(this, "Recurso atualizado", Toast.LENGTH_SHORT).show();
        } else {
            RecursoAlocado r = new RecursoAlocado(projetoId, tipoSelecionado.getId(),
                    quantidade, periodo, obs);
            r.calcularCusto(tipoSelecionado.getCustoUnitario(), tipoSelecionado.getUnidade());
            long id = db.inserirRecurso(r);
            if (id > 0) {
                Toast.makeText(this, "Recurso adicionado – R$ "
                                + String.format(Locale.getDefault(), "%.2f", r.getCustoEstimado()),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Erro ao adicionar recurso", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
