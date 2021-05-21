package com.example.das_quienesquien_jonalba;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;


public class ElViewHolder extends RecyclerView.ViewHolder {

    public TextView eltexto;
    public ImageView laimagen;
    public boolean[] seleccion;
    public Context elcontexto;

    public ElViewHolder(@NonNull View itemView){
        super(itemView);
        eltexto = itemView.findViewById(R.id.texto);
        laimagen = itemView.findViewById(R.id.foto);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Obtenemos la clase desde donde accedemos gracias al contexto
                String clase = elcontexto.getClass().toString();

                switch (clase) {

                    //Si venimos de Juego --> Se ha pulsado en un personaje del tablero --> Se pone en filtro negro o se quita
                    case "class com.example.das_quienesquien_jonalba.Juego":


                        if (seleccion[getAdapterPosition()] == true) {
                            seleccion[getAdapterPosition()] = false;
                            laimagen.setColorFilter(null);
                        } else {
                            seleccion[getAdapterPosition()] = true;
                            laimagen.setColorFilter(Color.BLACK);

                        }

                        break;


                        //Si venimos de Resolver --> Un jugador ha seleccionado un personaje --> Aparece un AlertDialog pidiendo confirmación
                    case "class com.example.das_quienesquien_jonalba.Resolver":


                       AlertDialog.Builder adb = new AlertDialog.Builder(elcontexto);
                        adb.setTitle("¿Crees que es " + eltexto.getText().toString() + "?");
                        adb.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                //Comprobamos si el personaje seleccionado es el correcto
                                Resolver r = (Resolver) elcontexto;
                                r.comprobarPersonaje(eltexto.getText().toString());
                                dialog.dismiss();

                            }
                        });

                        adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        adb.show();


                }
            }
        });



    }
}
