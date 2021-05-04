package com.example.das_quienesquien_jonalba;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
                if(seleccion[getAdapterPosition()]==true){
                    seleccion[getAdapterPosition()]=false;
                    laimagen.setColorFilter(null);
                }else{
                    seleccion[getAdapterPosition()]=true;
                    laimagen.setColorFilter(Color.BLACK);

                }
            }
        });



      /*
      itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(laimagen.getParent()!=null) {
                    ((ViewGroup) laimagen.getParent()).removeView(laimagen);
                }

                //Drawable imagen2 = laimagen.getDrawable();
                //ImageView imageView2 = null;
                //imageView2.setImageDrawable(imagen2);

                AlertDialog.Builder builder =
                        new AlertDialog.Builder(elcontexto).
                                setMessage("Imagen agrandada").
                                setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        if(laimagen.getParent()!=null) {
                                            ((ViewGroup) laimagen.getParent()).removeView(laimagen);
                                        }

                                    }
                                }).
                                setView(laimagen);
                builder.create().show();

                return true;
            }
        });*/
    }
}
