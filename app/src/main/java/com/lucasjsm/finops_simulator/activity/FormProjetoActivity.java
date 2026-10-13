package com.lucasjsm.finops_simulator.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lucasjsm.finops_simulator.R;
import com.lucasjsm.finops_simulator.database.DatabaseHelper;
import com.lucasjsm.finops_simulator.model.Projeto;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormProjetoActivity extends AppCompatActivity {

    private EditText etNome, etDescricao, etOrcamento;
    private Spinner spProvedor;
    private DatabaseHelper db;
    private long projetoId = -1;
    private Projeto projetoEdit;

    private static final String[] PROVEDORES = {"AWS", "GCP", "Azure", "OCI"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_projeto);

        db = new DatabaseHelper(this);

        etNome = findViewById(R.id.etProjetoNome);
        etDescricao = findViewById(R.id.etProjetoDescricao);
        etOrcamento = findViewById(R.id.etProjetoOrcamento);
        spProvedor = findViewById(R.id.spProjetoProvedor);

        ArrayAdapter<String> adapterProv = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, PROVEDORES);
        adapterProv.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProvedor.setAdapter(adapterProv);

        projetoId = getIntent().getLongExtra("projeto_id", -1);
        if (projetoId > 0) {
            setTitle("Editar Projeto");
            carregarProjeto();
            spProvedor.setEnabled(false); // RN01 – provedor não editável
        } else {
            setTitle("Novo Projeto");
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
                orcamento = Double.parseDouble(orcStr);
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
            projetoEdit.setNome(nome);
            projetoEdit.setDescricao(descricao);
            projetoEdit.setOrcamentoMaximo(orcamento);
            db.atualizarProjeto(projetoEdit);
            Toast.makeText(this, "Projeto atualizado", Toast.LENGTH_SHORT).show();
        } else {
            String data = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
            Projeto p = new Projeto(nome, descricao, provedor, orcamento, data);
            long id = db.inserirProjeto(p);
            if (id > 0) {
                Toast.makeText(this, "Projeto criado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Erro ao criar projeto", Toast.LENGTH_SHORT).show();
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
