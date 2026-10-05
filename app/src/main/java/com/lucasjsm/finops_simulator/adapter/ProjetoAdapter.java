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
import com.lucasjsm.finops_simulator.model.Projeto;

import java.util.List;
import java.util.Locale;

public class ProjetoAdapter extends ArrayAdapter<Projeto> {

    public ProjetoAdapter(Context context, List<Projeto> projetos) {
        super(context, 0, projetos);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_projeto, parent, false);
        }

        Projeto p = getItem(position);
        if (p != null) {
            TextView tvNome = convertView.findViewById(R.id.tvItemProjetoNome);
            TextView tvProvedor = convertView.findViewById(R.id.tvItemProjetoProvedor);
            TextView tvCusto = convertView.findViewById(R.id.tvItemProjetoCusto);

            tvNome.setText(p.getNome());
            tvProvedor.setText(p.getProvedor());
            tvCusto.setText(String.format(Locale.US, "US$ %.2f", p.getCustoTotal()));
        }
        return convertView;
    }
}
