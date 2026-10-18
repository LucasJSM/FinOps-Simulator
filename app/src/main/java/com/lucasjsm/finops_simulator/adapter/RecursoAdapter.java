package com.lucasjsm.finops_simulator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lucasjsm.finops_simulator.R;
import com.lucasjsm.finops_simulator.model.RecursoAlocado;

import java.util.List;
import java.util.Locale;

public class RecursoAdapter extends ArrayAdapter<RecursoAlocado> {

    public interface OnRecursoActionListener {
        void onEditarRecurso(RecursoAlocado recurso);
        void onExcluirRecurso(RecursoAlocado recurso);
    }

    private final LayoutInflater inflater;
    private final OnRecursoActionListener listener;

    public RecursoAdapter(Context context, List<RecursoAlocado> recursos,
                          OnRecursoActionListener listener) {
        super(context, 0, recursos);
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_recurso, parent, false);
            holder = new ViewHolder();
            holder.tvNome = (TextView) convertView.findViewById(R.id.tvItemRecursoNome);
            holder.tvDetalhe = (TextView) convertView.findViewById(R.id.tvItemRecursoDetalhe);
            holder.tvCusto = (TextView) convertView.findViewById(R.id.tvItemRecursoCusto);
            holder.btnEditar = (ImageButton) convertView.findViewById(R.id.btnItemRecursoEditar);
            holder.btnExcluir = (ImageButton) convertView.findViewById(R.id.btnItemRecursoExcluir);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final RecursoAlocado r = getItem(position);
        if (r == null) {
            return convertView;
        }

        holder.tvNome.setText(r.getNomeTipo() != null ? r.getNomeTipo() : "Recurso");

        String unidade = r.getUnidade() != null ? r.getUnidade() : "";
        holder.tvDetalhe.setText(String.format(Locale.getDefault(),
                "%.1f %s x %.1f mês",
                r.getQuantidade(), unidade, r.getPeriodo()));

        holder.tvCusto.setText(String.format(Locale.getDefault(),
                "R$ %.2f", r.getCustoEstimado()));

        holder.btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onEditarRecurso(r);
                }
            }
        });

        holder.btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onExcluirRecurso(r);
                }
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView tvNome;
        TextView tvDetalhe;
        TextView tvCusto;
        ImageButton btnEditar;
        ImageButton btnExcluir;
    }
}
