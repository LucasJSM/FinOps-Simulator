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
import com.lucasjsm.finops_simulator.model.TipoRecurso;

import java.util.List;
import java.util.Locale;

public class TipoAdapter extends ArrayAdapter<TipoRecurso> {

    public TipoAdapter(Context context, List<TipoRecurso> tipos) {
        super(context, 0, tipos);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tipo, parent, false);
        }

        TipoRecurso t = getItem(position);
        if (t != null) {
            TextView tvNome = convertView.findViewById(R.id.tvItemTipoNome);
            TextView tvProvedor = convertView.findViewById(R.id.tvItemTipoProvedor);
            TextView tvCusto = convertView.findViewById(R.id.tvItemTipoCusto);

            tvNome.setText(t.getNome());
            tvProvedor.setText(t.getProvedor());
            tvCusto.setText(String.format(Locale.US, "US$ %.4f / %s", t.getCustoUnitario(), t.getUnidade()));
        }
        return convertView;
    }
}
