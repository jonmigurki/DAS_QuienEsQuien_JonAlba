package com.example.das_quienesquien_jonalba;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdaptadorChat extends RecyclerView.Adapter<HolderChat> {

    private ArrayList<Mensaje> listaMensajes = new ArrayList<>();
    private Context context;

    public AdaptadorChat(Context context) {
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

    }

    @Override
    public int getItemCount() {
        return listaMensajes.size();
    }
}
