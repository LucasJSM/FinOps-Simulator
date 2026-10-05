package com.lucasjsm.finops_simulator.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lucasjsm.finops_simulator.R;
import com.lucasjsm.finops_simulator.database.DatabaseHelper;
import com.lucasjsm.finops_simulator.model.TipoRecurso;

/**
 * Formulário de criação/edição de Tipo de Recurso (UC07 / UC08).
 */
public class FormTipoActivity extends AppCompatActivity {

    private EditText etNome, etUnidade, etCusto, etDescricao;
    private Spinner spProvedor;
    private Button btnSalvar, btnCancelar;
    private DatabaseHelper db;
    private long tipoId = -1;
    private TipoRecurso tipoEdit;

    private static final String[] PROVEDORES = {"AWS", "GCP", "Azure", "OCI"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_tipo);

        db = new DatabaseHelper(this);
        etNome = findViewById(R.id.etTipoNome);
        etUnidade = findViewById(R.id.etTipoUnidade);
        etCusto = findViewById(R.id.etTipoCusto);
        etDescricao = findViewById(R.id.etTipoDescricao);
        spProvedor = findViewById(R.id.spTipoProvedor);
        btnSalvar = findViewById(R.id.btnTipoSalvar);
        btnCancelar = findViewById(R.id.btnTipoCancelar);

        ArrayAdapter<String> adapterProv = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, PROVEDORES);
        adapterProv.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProvedor.setAdapter(adapterProv);

        tipoId = getIntent().getLongExtra("tipo_id", -1);
        if (tipoId > 0) {
            setTitle("Editar Tipo de Recurso");
            carregarTipo();
        } else {
            setTitle("Novo Tipo de Recurso");
        }

        btnSalvar.setOnClickListener(v -> salvar());
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void carregarTipo() {
        tipoEdit = db.buscarTipoPorId(tipoId);
        if (tipoEdit != null) {
            etNome.setText(tipoEdit.getNome());
            etUnidade.setText(tipoEdit.getUnidade());
            etCusto.setText(String.valueOf(tipoEdit.getCustoUnitario()));
            etDescricao.setText(tipoEdit.getDescricao());
            for (int i = 0; i < PROVEDORES.length; i++) {
                if (PROVEDORES[i].equals(tipoEdit.getProvedor())) {
                    spProvedor.setSelection(i);
                    break;
                }
            }
        }
    }

    private void salvar() {
        String nome = etNome.getText().toString().trim();
        String unidade = etUnidade.getText().toString().trim();
        String custoStr = etCusto.getText().toString().trim();
        String descricao = etDescricao.getText().toString().trim();
        String provedor = spProvedor.getSelectedItem().toString();

        if (nome.isEmpty()) {
            etNome.setError("Nome obrigatório");
            return;
        }
        if (unidade.isEmpty()) {
            etUnidade.setError("Unidade obrigatória");
            return;
        }
        if (custoStr.isEmpty()) {
            etCusto.setError("Custo unitário obrigatório");
            return;
        }

        double custo;
        try {
            custo = Double.parseDouble(custoStr);
            if (custo < 0) {
                etCusto.setError("Custo não pode ser negativo");
                return;
            }
        } catch (NumberFormatException e) {
            etCusto.setError("Valor inválido");
            return;
        }

        if (tipoId > 0 && tipoEdit != null) {
            tipoEdit.setNome(nome);
            tipoEdit.setProvedor(provedor);
            tipoEdit.setUnidade(unidade);
            tipoEdit.setCustoUnitario(custo);
            tipoEdit.setDescricao(descricao);
            db.atualizarTipo(tipoEdit);
            Toast.makeText(this, "Tipo atualizado", Toast.LENGTH_SHORT).show();
        } else {
            TipoRecurso t = new TipoRecurso(nome, provedor, unidade, custo, descricao);
            long id = db.inserirTipo(t);
            if (id > 0) {
                Toast.makeText(this, "Tipo cadastrado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Erro ao cadastrar", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        finish();
    }
}
