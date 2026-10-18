package com.lucasjsm.finops_simulator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lucasjsm.finops_simulator.R;
import com.lucasjsm.finops_simulator.database.DatabaseHelper;
import com.lucasjsm.finops_simulator.model.Projeto;

import java.util.List;
import java.util.Locale;

public class ProjetoAdapter extends ArrayAdapter<Projeto> {

    private final LayoutInflater inflater;

    public ProjetoAdapter(Context context, List<Projeto> projetos, DatabaseHelper db) {
        super(context, 0, projetos);
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_projeto, parent, false);
            holder = new ViewHolder();
            holder.tvNome = (TextView) convertView.findViewById(R.id.tvItemProjetoNome);
            holder.tvProvedor = (TextView) convertView.findViewById(R.id.tvItemProjetoProvedor);
            holder.tvCusto = (TextView) convertView.findViewById(R.id.tvItemProjetoCusto);
            holder.tvOrcamento = (TextView) convertView.findViewById(R.id.tvItemProjetoOrcamento);
            holder.pbOrcamento = (ProgressBar) convertView.findViewById(R.id.pbItemProjetoOrcamento);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Projeto p = getItem(position);
        if (p == null) {
            return convertView;
        }

        holder.tvNome.setText(p.getNome() != null ? p.getNome() : "—");
        holder.tvProvedor.setText(p.getProvedor() != null ? p.getProvedor() : "—");

        double custo = p.getCustoTotal();
        double orcamento = p.getOrcamentoMaximo();

        holder.tvCusto.setText(String.format(Locale.getDefault(), "R$ %,.2f/mês", custo));
        holder.tvOrcamento.setText(String.format(Locale.getDefault(), "R$ %,.2f/mês", orcamento));

        int pct = 0;
        int pctReal = 0;
        if (orcamento > 0) {
            pctReal = (int) Math.round((custo / orcamento) * 100.0);
            pct = Math.min(pctReal, 100);
        }
        holder.pbOrcamento.setProgress(pct);

        if (pctReal > 100) {
            holder.pbOrcamento.setProgressDrawable(
                    getContext().getResources().getDrawable(R.drawable.progress_budget_danger));
        } else if (pctReal >= 80) {
            holder.pbOrcamento.setProgressDrawable(
                    getContext().getResources().getDrawable(R.drawable.progress_budget_warning));
        } else {
            holder.pbOrcamento.setProgressDrawable(
                    getContext().getResources().getDrawable(R.drawable.progress_budget));
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView tvNome;
        TextView tvProvedor;
        TextView tvCusto;
        TextView tvOrcamento;
        ProgressBar pbOrcamento;
    }
}
