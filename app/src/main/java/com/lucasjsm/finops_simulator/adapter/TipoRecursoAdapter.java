package com.lucasjsm.finops_simulator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.lucasjsm.finops_simulator.R;
import com.lucasjsm.finops_simulator.model.TipoRecurso;

import java.util.List;
import java.util.Locale;

/**
 * Adapter da ListView do ListaTiposActivity
 */
public class TipoRecursoAdapter extends ArrayAdapter<TipoRecurso> {

    private final LayoutInflater inflater;

    public TipoRecursoAdapter(Context context, List<TipoRecurso> tipos) {
        super(context, 0, tipos);
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_tipo_recurso, parent, false);
            holder = new ViewHolder();
            holder.tvNome = (TextView) convertView.findViewById(R.id.tvItemTipoNome);
            holder.tvProvedor = (TextView) convertView.findViewById(R.id.tvItemTipoProvedor);
            holder.tvCusto = (TextView) convertView.findViewById(R.id.tvItemTipoCusto);
            holder.tvDescricao = (TextView) convertView.findViewById(R.id.tvItemTipoDescricao);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TipoRecurso t = getItem(position);
        if (t == null) {
            return convertView;
        }

        holder.tvNome.setText(t.getNome());
        holder.tvProvedor.setText(t.getProvedor());

        String unidade = t.getUnidade();
        if ("milhao_reqs".equalsIgnoreCase(unidade)) {
            unidade = "milhão reqs";
        }
        holder.tvCusto.setText(String.format(Locale.getDefault(),
                "R$ %.3f / %s", t.getCustoUnitario(), unidade));

        if (t.getDescricao() != null && !t.getDescricao().trim().isEmpty()) {
            holder.tvDescricao.setText(t.getDescricao());
            holder.tvDescricao.setVisibility(View.VISIBLE);
        } else {
            holder.tvDescricao.setVisibility(View.GONE);
        }

        // Cores do chip conforme o provedor
        int corTexto;
        int corFundo;
        String prov = t.getProvedor() != null ? t.getProvedor() : "";
        if (prov.equalsIgnoreCase("AWS")) {
            corTexto = R.color.chip_aws_text;
            corFundo = R.color.chip_aws_bg;
        } else if (prov.equalsIgnoreCase("GCP")) {
            corTexto = R.color.chip_gcp_text;
            corFundo = R.color.chip_gcp_bg;
        } else if (prov.equalsIgnoreCase("Azure")) {
            corTexto = R.color.chip_azure_text;
            corFundo = R.color.chip_azure_bg;
        } else {
            corTexto = R.color.chip_oci_text;
            corFundo = R.color.chip_oci_bg;
        }
        holder.tvProvedor.setTextColor(ContextCompat.getColor(getContext(), corTexto));
        holder.tvProvedor.getBackground().mutate().setTint(ContextCompat.getColor(getContext(), corFundo));

        return convertView;
    }

    private static class ViewHolder {
        TextView tvNome;
        TextView tvProvedor;
        TextView tvCusto;
        TextView tvDescricao;
    }
}
