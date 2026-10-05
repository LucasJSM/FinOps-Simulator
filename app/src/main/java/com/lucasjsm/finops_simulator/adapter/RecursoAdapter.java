package com.lucasjsm.finops_simulator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lucasjsm.finops_simulator.R;
import com.lucasjsm.finops_simulator.model.RecursoAlocado;

import java.util.List;
import java.util.Locale;

public class RecursoAdapter extends ArrayAdapter<RecursoAlocado> {

    public RecursoAdapter(Context context, List<RecursoAlocado> recursos) {
        super(context, 0, recursos);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_recurso, parent, false);
        }

        RecursoAlocado r = getItem(position);
        if (r != null) {
            TextView tvNome = convertView.findViewById(R.id.tvItemRecursoNome);
            TextView tvDetalhe = convertView.findViewById(R.id.tvItemRecursoDetalhe);
            TextView tvCusto = convertView.findViewById(R.id.tvItemRecursoCusto);

            tvNome.setText(r.getNomeTipo() != null ? r.getNomeTipo() : "Recurso");
            tvDetalhe.setText(String.format(Locale.US, "%.1f × %.1f %s", r.getQuantidade(), r.getPeriodo(),
                    r.getUnidade() != null ? r.getUnidade() : ""));
            tvCusto.setText(String.format(Locale.US, "US$ %.2f", r.getCustoEstimado()));
        }
        return convertView;
    }
}
