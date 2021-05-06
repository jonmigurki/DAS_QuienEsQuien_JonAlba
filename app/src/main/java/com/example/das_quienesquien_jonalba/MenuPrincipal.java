package com.example.das_quienesquien_jonalba;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MenuPrincipal extends AppCompatActivity {

    // Instanciamos la ListView
    ListView ListViewItem;
    List<ItemsMenuView> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        // Asignamos los id a las variables
        ListViewItem = findViewById(R.id.ListViewItem);

        // Creamos un ApaptadorListView
        AdaptadorListView adaptador = new AdaptadorListView(this, getData());
        ListViewItem.setAdapter(adaptador);


        // Acciones cuando se presiona un elemento del ListViewItem
        ListViewItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                ItemsMenuView item = list.get(i);
                // ............
                if(item.nombre=="Exámenes"){


                    Toast.makeText(MenuPrincipal.this, "Ha ocurrido un error.", Toast.LENGTH_LONG).show();



                    // ............
                }else if(item.nombre=="Tareas"){
                    Toast.makeText(MenuPrincipal.this, "Ha ocurrido un error.", Toast.LENGTH_LONG).show();

                }
            }
        });


    }


    // Método para añadir datos a la lista personalizada
    private List<ItemsMenuView> getData() {
        list = new ArrayList<>();

        // Añadimos los items a la lista
        list.add(new ItemsMenuView(1, R.drawable.interrogacion,"Exámenes", "Escribe aquí las fechas de tus exámenes."));
        list.add(new ItemsMenuView(2, R.drawable.interrogacion,"Tareas", "Escribe aquí tus tareas."));


        // Devolvemos la lista
        return list;
    }
}