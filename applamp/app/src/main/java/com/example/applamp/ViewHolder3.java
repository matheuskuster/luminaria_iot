package com.example.applamp;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder3 extends RecyclerView.ViewHolder {
    public TextView rede;

    public ViewHolder3(View itemView) {
        super(itemView);
        this.rede = (TextView) itemView.findViewById(R.id.rede);
    }
}
