package com.example.das_quienesquien_jonalba;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class HolderChat extends RecyclerView.ViewHolder {

    TextView elusuario;
    TextView elmensaje;

    public HolderChat(View itemView){

        super(itemView);

        elusuario = (TextView) itemView.findViewById(R.id.usuario_cardview);
        elmensaje = (TextView) itemView.findViewById(R.id.mensaje_cardview);

    }


}
