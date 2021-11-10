package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rlds.controlefinanceiro.R;

import java.util.List;

import model.Movimentacao;

public class Movimentacao_adapter extends RecyclerView.Adapter<Movimentacao_adapter.MyViewHolder> {
    private List<Movimentacao>listaMovimentacao;
    private Context context;


    public Movimentacao_adapter(List<Movimentacao>lista, Context context) {
        this.listaMovimentacao = lista;
        this.context = context;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.movimentacao_lista_adapter,parent,false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Movimentacao movimentacao = listaMovimentacao.get(position);
        holder.txt_data.setText(movimentacao.getData());
        holder.txt_valor.setText(String.valueOf(movimentacao.getValor()));
        holder.txt_descricao.setText(movimentacao.getDescricao());
        holder.txt_categoria.setText(movimentacao.getCategoria());
        holder.txt_valor.setTextColor(context.getResources().getColor(R.color.cor_verde));
        if (movimentacao.getTipo().equals("d")){
            holder.txt_valor.setTextColor(context.getResources().getColor(R.color.cor_vermelha));
            holder.txt_valor.setText("-" + movimentacao.getValor());

        }




    }

    @Override
    public int getItemCount() {
        return listaMovimentacao.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{
        TextView txt_descricao, txt_valor, txt_categoria, txt_data;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_categoria = itemView.findViewById(R.id.textCategoriaLista);
            txt_descricao = itemView.findViewById(R.id.txtDescricaoLista);
            txt_valor = itemView.findViewById(R.id.textValorLista);
            txt_data = itemView.findViewById(R.id.textDataLista);
        }
    }
}
