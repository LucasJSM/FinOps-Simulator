package com.lucasjsm.finops_simulator.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lucasjsm.finops_simulator.R;
import com.lucasjsm.finops_simulator.activity.DashboardActivity;
import com.lucasjsm.finops_simulator.activity.FormRecursoActivity;
import com.lucasjsm.finops_simulator.model.Projeto;

import java.util.List;
import java.util.Locale;

/**
 * Adapter da ListView da Home.
 * Exibe cards de resumo com campos somente leitura e botões de ação internos.
 */
public class HomeProjetoAdapter extends ArrayAdapter<Projeto> {

    private final LayoutInflater inflater;

    public HomeProjetoAdapter(Context context, List<Projeto> projetos) {
        super(context, 0, projetos);
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_projeto_home, parent, false);
            holder = new ViewHolder();
            holder.tvNumero = (TextView) convertView.findViewById(R.id.tvItemHomeNumero);
            holder.tvChipCusto = (TextView) convertView.findViewById(R.id.tvItemHomeChipCusto);
            holder.tvNome = (TextView) convertView.findViewById(R.id.tvItemHomeNome);
            holder.tvProvedor = (TextView) convertView.findViewById(R.id.tvItemHomeProvedor);
            holder.tvOrcamento = (TextView) convertView.findViewById(R.id.tvItemHomeOrcamento);
            holder.btnDashboard = (Button) convertView.findViewById(R.id.btnItemHomeDashboard);
            holder.btnRecursos = (Button) convertView.findViewById(R.id.btnItemHomeRecursos);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Projeto p = getItem(position);
        if (p == null) {
            return convertView;
        }

        // Numeração visual (1-based)
        holder.tvNumero.setText(String.format(Locale.getDefault(), "PROJETO #%02d", position + 1));

        // Chip: custo total / mês e percentual do orçamento
        double custo = p.getCustoTotal();
        double orcamento = p.getOrcamentoMaximo();
        int pct = 0;
        if (orcamento > 0) {
            pct = (int) Math.round((custo / orcamento) * 100.0);
        }
        holder.tvChipCusto.setText(String.format(Locale.getDefault(),
                "R$ %.2f/mês (%d%%)", custo, pct));

        // Cor do chip conforme status
        if (pct > 100) {
            holder.tvChipCusto.setTextColor(getContext().getResources().getColor(R.color.status_danger_text));
            holder.tvChipCusto.setBackgroundResource(R.drawable.bg_chip_cost_danger);
        } else if (pct >= 80) {
            holder.tvChipCusto.setTextColor(getContext().getResources().getColor(R.color.status_warning_text));
            holder.tvChipCusto.setBackgroundResource(R.drawable.bg_chip_cost_warning);
        } else {
            holder.tvChipCusto.setTextColor(getContext().getResources().getColor(R.color.primary));
            holder.tvChipCusto.setBackgroundResource(R.drawable.bg_chip_cost);
        }

        holder.tvNome.setText(p.getNome() != null ? p.getNome() : "—");
        holder.tvProvedor.setText(p.getProvedor() != null ? p.getProvedor() : "—");
        holder.tvOrcamento.setText(String.format(Locale.getDefault(), "%.0f", orcamento));

        // Clique: Abrir Dashboard
        holder.btnDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), DashboardActivity.class);
                i.putExtra("projeto_id", p.getId());
                getContext().startActivity(i);
            }
        });

        // Clique: Recursos (FormRecursoActivity para alocar)
        holder.btnRecursos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), FormRecursoActivity.class);
                i.putExtra("projeto_id", p.getId());
                getContext().startActivity(i);
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView tvNumero;
        TextView tvChipCusto;
        TextView tvNome;
        TextView tvProvedor;
        TextView tvOrcamento;
        Button btnDashboard;
        Button btnRecursos;
    }
}
