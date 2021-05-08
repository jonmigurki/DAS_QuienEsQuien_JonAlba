package com.example.das_quienesquien_jonalba;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ElAdaptadorRecycler extends RecyclerView.Adapter<ElViewHolder>{

    private String[] losnombres;
    private int[] lasimagenes;
    private boolean[] seleccionados;
    private Context context;

    public ElAdaptadorRecycler (String[] nombres, int[] imagenes, Context c){
        losnombres = nombres;
        lasimagenes = imagenes;
        seleccionados = new boolean[nombres.length];
        context = c;
    }


    @NonNull
    @Override
    public ElViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View elLayoutDeCadaItem= LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_personajes,null);
        ElViewHolder evh = new ElViewHolder(elLayoutDeCadaItem);
        evh.seleccion = seleccionados;
        evh.elcontexto = context;
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ElViewHolder holder, int position) {
        holder.eltexto.setText(losnombres[position]);
        holder.laimagen.setImageResource(lasimagenes[position]);
    }

    @Override
    public int getItemCount() {
        return losnombres.length;
    }
}
