package com.example.applamp;



import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapterNotificacoes extends RecyclerView.Adapter<ViewHolder2> {
    private Context context;
    private List<NotificacaoObject> mensagens;

    public MyAdapterNotificacoes(Context context, List<NotificacaoObject> mensagens) {
        this.context = context;
        this.mensagens = mensagens;
    }

    @NonNull
    @Override
    public ViewHolder2 onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View listItemView = inflater.inflate(R.layout.mensagem, viewGroup, false);

        ViewHolder2 viewHolder = new ViewHolder2(listItemView);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder2 viewHolder, int i) {
        final NotificacaoObject notificacao = this.mensagens.get(i);

        TextView texto = viewHolder.texto;
        Button parear = viewHolder.Parear;
        texto.setText(notificacao.text);

        if(!notificacao.to_aceppt) {
            parear.setVisibility(View.GONE);
        } else {
            if(notificacao.accepted) {
                parear.setText("PAREADO");
                parear.setEnabled(false);
            } else {
                parear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((Notificacoes)context).acceptRelation(notificacao);
                    }
                });
            }
        }
    }
    @Override
    public int getItemCount() {
        return this.mensagens.size();
    }
}