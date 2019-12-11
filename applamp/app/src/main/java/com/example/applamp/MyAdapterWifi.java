package com.example.applamp;



import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapterWifi extends RecyclerView.Adapter<ViewHolder3> {
    private Context context;

    private List<String> wifi;

    public MyAdapterWifi(Context context, List<String> wifi) {
        this.context = context;
        this.wifi = wifi;
    }

    @NonNull
    @Override
    public ViewHolder3 onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View listItemView = inflater.inflate(R.layout.redes, viewGroup, false);

        ViewHolder3 viewHolder = new ViewHolder3(listItemView);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder3 viewHolder, int i) {
        final String ssid = this.wifi.get(i);

        TextView rede = viewHolder.rede;
        rede.setText(ssid);

        View itemView = viewHolder.itemView;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent myIntent = new Intent(context, SenhaWifi.class);
                    myIntent.putExtra("ssid", ssid);
                    context.startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, "Não há aplicação que possa responder essa ação!" + "Por favor, instale um navegador.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return this.wifi.size();
    }
}