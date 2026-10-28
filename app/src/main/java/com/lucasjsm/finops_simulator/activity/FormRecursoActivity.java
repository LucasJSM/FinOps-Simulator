package com.lucasjsm.finops_simulator.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import com.lucasjsm.finops_simulator.model.RecursoAlocado;
import com.lucasjsm.finops_simulator.model.RecursoAlocadoDAO;
import com.lucasjsm.finops_simulator.model.TipoRecurso;
import com.lucasjsm.finops_simulator.model.TipoRecursoDAO;

import java.util.List;
import java.util.Locale;

/**
 * Activity para inclusão e alteração de Recursos Alocados a um Projeto.
 * - Parâmetros via Intent: acao (-1 para inclusão, 0 para alteração), id e projeto_id
 * - Métodos salvar(View v) e voltar(View v)
 */
public class FormRecursoActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    private Spinner spTipo;
    private EditText edQtd, edPeriodo, edObs;
    private TextView tvTitulo, tvProvedorInfo, tvPreview;
    private Button btnSalvar;
    private BottomNavigationView bottomNav;

    private int acao;
    private long id;
    private long projetoId;

    private ProjetoDAO projetoDao;
    private TipoRecursoDAO tipoDao;
    private RecursoAlocadoDAO recursoDao;

    private Projeto projeto;
    private List<TipoRecurso> tipos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_recurso);

        projetoDao = new ProjetoDAO(this);
        tipoDao = new TipoRecursoDAO(this);
        recursoDao = new RecursoAlocadoDAO(this);

        spTipo = (Spinner) findViewById(R.id.spTratarRecursoTipo);
        edQtd = (EditText) findViewById(R.id.etTratarRecursoQuantidade);
        edPeriodo = (EditText) findViewById(R.id.etTratarRecursoPeriodo);
        edObs = (EditText) findViewById(R.id.etTratarRecursoObs);
        tvTitulo = (TextView) findViewById(R.id.tvTratarRecursoTitulo);
        tvProvedorInfo = (TextView) findViewById(R.id.tvTratarRecursoProvedorInfo);
        tvPreview = (TextView) findViewById(R.id.tvTratarRecursoCustoPreview);
        btnSalvar = (Button) findViewById(R.id.btnTratarRecursoAcao);
        bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNav.getMenu().findItem(R.id.nav_projetos).setChecked(true);
        bottomNav.setOnNavigationItemSelectedListener(this);

        // Recebe os dados passados pela Intent (Padrão Slide 218)
        acao = getIntent().getExtras().getInt("acao"); // -1 (inclusão) ou 0 (alteração/exclusão)
        id = getIntent().getExtras().getLong("id");
        projetoId = getIntent().getExtras().getLong("projeto_id");

        projetoDao.open();
        projeto = projetoDao.buscar(projetoId);
        projetoDao.close();

        if (projeto == null) {
            Toast.makeText(this, "Projeto não encontrado!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvProvedorInfo.setText(projeto.getNome() + " • Provedor " + projeto.getProvedor());

        // Carrega serviços do catálogo filtrados pelo provedor do projeto
        tipoDao.open();
        tipos = tipoDao.getAllPorProvedor(projeto.getProvedor());
        tipoDao.close();

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

        // Configuração de preview dinâmico ao digitar
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                atualizarPreview();
            }
            @Override public void afterTextChanged(Editable s) {}
        };
        edQtd.addTextChangedListener(watcher);
        edPeriodo.addTextChangedListener(watcher);

        if (acao == -1) {
            // Inclusão (Slide 218)
            setTitle("Inserir Recurso");
            if (tvTitulo != null) tvTitulo.setText("Inserir Recurso");
            btnSalvar.setText("Salvar");
            edQtd.setText("1.0");
            edPeriodo.setText("1.0");
        } else {
            // Alteração ou exclusão (Slide 219)
            setTitle("Alterar Recurso");
            if (tvTitulo != null) tvTitulo.setText("Alterar Recurso");
            btnSalvar.setText("Salvar");

            recursoDao.open();
            RecursoAlocado aux = recursoDao.buscar(id);
            recursoDao.close();

            if (aux != null) {
                edQtd.setText(String.format(Locale.getDefault(), "%.1f", aux.getQuantidade()).replace(",", "."));
                edPeriodo.setText(String.format(Locale.getDefault(), "%.1f", aux.getPeriodo()).replace(",", "."));
                edObs.setText(aux.getObservacao());
                for (int i = 0; i < tipos.size(); i++) {
                    if (tipos.get(i).getId() == aux.getTipoRecursoId()) {
                        spTipo.setSelection(i);
                        break;
                    }
                }
            }
            spTipo.setEnabled(false); // Tipo de recurso mantido fixo na alteração
        }

        atualizarPreview();
    }

    private void atualizarPreview() {
        try {
            String qtdStr = edQtd.getText().toString().trim();
            String perStr = edPeriodo.getText().toString().trim();
            if (qtdStr.isEmpty() || perStr.isEmpty() || spTipo.getSelectedItem() == null) {
                tvPreview.setText("R$ -");
                return;
            }

            double qtd = Double.parseDouble(qtdStr.replace(",", "."));
            double per = Double.parseDouble(perStr.replace(",", "."));
            TipoRecurso t = (TipoRecurso) spTipo.getSelectedItem();

            RecursoAlocado dummy = new RecursoAlocado();
            dummy.setQuantidade(qtd);
            dummy.setPeriodo(per);
            double custo = dummy.calcularCusto(t.getCustoUnitario(), t.getUnidade());

            tvPreview.setText(String.format(Locale.getDefault(), "R$ %.2f", custo));
        } catch (Exception e) {
            tvPreview.setText("R$ -");
        }
    }

    /**
     * Metodo para salvar (inserção ou alteração)
     */
    public void salvar(View v) {
        if (tipos == null || tipos.isEmpty()) {
            Toast.makeText(this, "Nenhum tipo de recurso disponível", Toast.LENGTH_SHORT).show();
            return;
        }

        String qtdStr = edQtd.getText().toString().trim();
        String perStr = edPeriodo.getText().toString().trim();
        String obs = edObs.getText().toString().trim();

        if (qtdStr.isEmpty()) {
            edQtd.setError("Quantidade obrigatória");
            edQtd.requestFocus();
            return;
        }
        if (perStr.isEmpty()) {
            edPeriodo.setError("Período obrigatório");
            edPeriodo.requestFocus();
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

        TipoRecurso tipoSelecionado = (TipoRecurso) spTipo.getSelectedItem();
        if (tipoSelecionado == null) {
            Toast.makeText(this, "Selecione um serviço", Toast.LENGTH_SHORT).show();
            return;
        }

        RecursoAlocado r = new RecursoAlocado();
        r.setQuantidade(quantidade);
        r.setPeriodo(periodo);
        double custoEstimado = r.calcularCusto(tipoSelecionado.getCustoUnitario(), tipoSelecionado.getUnidade());

        recursoDao.open();
        if (acao == -1) {
            // Inserção
            recursoDao.inserir(projetoId, tipoSelecionado.getId(), quantidade, periodo, custoEstimado, obs);
            Toast.makeText(this, "Recurso incluído com sucesso!", Toast.LENGTH_SHORT).show();
        } else {
            // Alteração
            recursoDao.alterar(id, quantidade, periodo, custoEstimado, obs);
            Toast.makeText(this, "Recurso alterado com sucesso!", Toast.LENGTH_SHORT).show();
        }
        recursoDao.close();
        finish();
    }

    /**
     * Metodo para voltar
     */
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
        if (navId == R.id.nav_inicio) {
            Intent intent = new Intent(FormRecursoActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
            return true;
        } else if (navId == R.id.nav_projetos) {
            startActivity(new Intent(FormRecursoActivity.this, ListaProjetosActivity.class));
            finish();
            return true;
        } else if (navId == R.id.nav_catalogo) {
            startActivity(new Intent(FormRecursoActivity.this, ListaTiposActivity.class));
            finish();
            return true;
        }
        return false;
    }
}
