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

    //Constructora del Adaptador
    public AdaptadorChat(String usuarioIdentificado, Context context) {
        this.usuarioIdentificado = usuarioIdentificado;
        this.context = context;
    }

    //Cuando envíamos un mensaje, se añade a la lista y se notifica que hay un elemento nuevo en ella
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

        //Visualizamos el mensaje en el layout
        holder.elusuario.setText(listaMensajes.get(position).getUsuario());
        holder.elmensaje.setText(listaMensajes.get(position).getMensaje());

        //Si el mensaje lo hemos escrito nosotros, el cardview se pone de un color concreto y el texto se alinea a la derecha
        if(listaMensajes.get(position).getUsuario().toString().equals(this.usuarioIdentificado)){
            holder.elusuario.setGravity(Gravity.RIGHT);
            holder.elmensaje.setGravity(Gravity.RIGHT);
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.mimensaje));
        }else{
            //Si el mensaje no lo hemos escrito nosotros, se pone de otro color concreto y el texto se alinea a la izquierda (por defecto)
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.sumensaje));
        }


    }

    //Devuelve el tamaño de la lista de mensajes
    @Override
    public int getItemCount() {
        return listaMensajes.size();
    }
}
