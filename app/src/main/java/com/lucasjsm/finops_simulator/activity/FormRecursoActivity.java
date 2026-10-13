package com.lucasjsm.finops_simulator.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lucasjsm.finops_simulator.R;
import com.lucasjsm.finops_simulator.database.DatabaseHelper;
import com.lucasjsm.finops_simulator.model.Projeto;
import com.lucasjsm.finops_simulator.model.RecursoAlocado;
import com.lucasjsm.finops_simulator.model.TipoRecurso;

import java.util.List;
import java.util.Locale;

public class FormRecursoActivity extends AppCompatActivity {

    private Spinner spTipo;
    private EditText etQuantidade, etPeriodo, etObs;
    private TextView tvCustoPreview, tvProvedorInfo;
    private DatabaseHelper db;
    private long projetoId;
    private long recursoId = -1;
    private Projeto projeto;
    private List<TipoRecurso> tipos;
    private RecursoAlocado recursoEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_recurso);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

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

        tvProvedorInfo.setText("Provedor do projeto: " + projeto.getProvedor());

        tipos = db.listarTiposPorProvedor(projeto.getProvedor());
        if (tipos.isEmpty()) {
            Toast.makeText(this, "Não há tipos de recurso cadastrados para " + projeto.getProvedor() + ". Cadastre tipos primeiro.", Toast.LENGTH_LONG).show();
        }

        ArrayAdapter<TipoRecurso> adapterTipo = new ArrayAdapter<TipoRecurso>(this, android.R.layout.simple_spinner_item, tipos);
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipo.setAdapter(adapterTipo);

        if (recursoId > 0) {
            setTitle("Editar Recurso");
            carregarRecurso();
            spTipo.setEnabled(false);
        } else {
            setTitle("Adicionar Recurso");
        }

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

    private void atualizarPreview() {
        try {
            double qtd = Double.parseDouble(etQuantidade.getText().toString().trim());
            double per = Double.parseDouble(etPeriodo.getText().toString().trim());
            if (spTipo.getSelectedItem() != null) {
                TipoRecurso t = (TipoRecurso) spTipo.getSelectedItem();

                double multiplicadorTempo = 1.0;
                if (t.getUnidade() != null && t.getUnidade().trim().equalsIgnoreCase("hora")) {
                    multiplicadorTempo = 730.0;
                }

                double custo = qtd * t.getCustoUnitario() * multiplicadorTempo * per;
                tvCustoPreview.setText(String.format(Locale.US, "Custo estimado: US$ %.2f", custo));
            }
        } catch (Exception e) {
            tvCustoPreview.setText("Custo estimado: —");
        }
    }

    public void salvar(View v) {
        if (tipos.isEmpty()) {
            Toast.makeText(this, "Cadastre tipos de recurso para este provedor primeiro", Toast.LENGTH_LONG).show();
            return;
        }

        String qtdStr = etQuantidade.getText().toString().trim();
        String perStr = etPeriodo.getText().toString().trim();
        String obs = etObs.getText().toString().trim();

        if (qtdStr.isEmpty()) {
            etQuantidade.setError("Quantidade obrigatória");
            return;
        }
        if (perStr.isEmpty()) {
            etPeriodo.setError("Período obrigatório");
            return;
        }

        double quantidade, periodo;
        try {
            quantidade = Double.parseDouble(qtdStr);
            periodo = Double.parseDouble(perStr);
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
            RecursoAlocado r = new RecursoAlocado(projetoId, tipoSelecionado.getId(), quantidade, periodo, obs);
            r.calcularCusto(tipoSelecionado.getCustoUnitario(), tipoSelecionado.getUnidade());
            long id = db.inserirRecurso(r);
            if (id > 0) {
                Toast.makeText(this, "Recurso adicionado – US$ " + String.format(Locale.US, "%.2f", r.getCustoEstimado()), Toast.LENGTH_SHORT).show();
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