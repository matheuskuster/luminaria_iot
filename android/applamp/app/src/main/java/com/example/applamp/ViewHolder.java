package com.example.applamp;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.applamp.R;

import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {
    public Button lampada;


    public ViewHolder(View itemView) {
        super(itemView);
        this.lampada = (Button) itemView.findViewById(R.id.lampada);
    }
}