package com.example.applamp;



import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
    private Context context;

    private List<Luminaria> luminarias;

    public MyAdapter(Context context, List<Luminaria> luminarias) {
        this.context = context;
        this.luminarias = luminarias;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View listItemView = inflater.inflate(R.layout.luminaria, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(listItemView);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Luminaria luminaria = this.luminarias.get(i);

        Button lampada = viewHolder.lampada;
        lampada.setText(luminaria.nome);

        //View itemView = viewHolder.itemView;
        lampada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent myIntent = new Intent(context, Funcoes.class);
                    myIntent.putExtra("code", luminaria.cod);
                    context.startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, "Não há aplicação que possa responder essa ação!" + "Por favor, instale um navegador.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return this.luminarias.size();
    }
}


