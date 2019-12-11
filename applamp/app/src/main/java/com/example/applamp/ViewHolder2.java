package com.example.applamp;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder2 extends RecyclerView.ViewHolder {
    public Button Parear;
    public TextView texto;


    public ViewHolder2(View itemView) {
        super(itemView);
        this.Parear = (Button) itemView.findViewById(R.id.Parear);
        this.texto = (TextView) itemView.findViewById(R.id.texto);
    }
}