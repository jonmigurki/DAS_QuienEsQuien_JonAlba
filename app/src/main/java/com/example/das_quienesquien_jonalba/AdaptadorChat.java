package com.example.das_quienesquien_jonalba;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdaptadorChat extends RecyclerView.Adapter<HolderChat> {

    private ArrayList<Mensaje> listaMensajes = new ArrayList<>();
    private String usuarioIdentificado;
    private Context context;

    public AdaptadorChat(String usuarioIdentificado, Context context) {
        this.usuarioIdentificado = usuarioIdentificado;
        this.context = context;
    }

    public void addMensaje(Mensaje m){
        listaMensajes.add(m);
        notifyItemInserted(listaMensajes.size());
    }

    @NonNull
    @Override
    public HolderChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_chat, null);
        HolderChat evh = new HolderChat(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull HolderChat holder, int position) {
        holder.elusuario.setText(listaMensajes.get(position).getUsuario());
        holder.elmensaje.setText(listaMensajes.get(position).getMensaje());

        if(listaMensajes.get(position).getUsuario().toString().equals(this.usuarioIdentificado)){
            holder.elusuario.setGravity(Gravity.RIGHT);
            holder.elmensaje.setGravity(Gravity.RIGHT);
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.mimensaje));
        }else{
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.sumensaje));
        }


    }

    @Override
    public int getItemCount() {
        return listaMensajes.size();
    }
}
