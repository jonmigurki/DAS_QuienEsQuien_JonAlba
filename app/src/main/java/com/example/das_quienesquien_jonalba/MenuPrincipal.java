package com.example.das_quienesquien_jonalba;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
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

    String[] listaCategorias = {"Los Simpson", "Otros"};
    String[] listaJugadores = {"JugadorPruebas", "Jugador2"};

    String nombreUsuario;

    //--------------------------------------



    //--------------------------------------



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            nombreUsuario = extras.getString("usuario");
        }


        //--------------------------------------

        // ---------------------------

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
                if(item.nombre=="Categoría"){

                    Toast.makeText(MenuPrincipal.this, "Categoría.", Toast.LENGTH_LONG).show();

                    // Creamos el AlertDialog con las opciones disponibles
                    AlertDialog.Builder alert = new AlertDialog.Builder(MenuPrincipal.this);
                    alert.setSingleChoiceItems(listaCategorias, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            if(i==0){
                                // Si se selecciona el primer elemento, es la categoría "Los Simpson"

                                // PASAR EL NOMBRE Y LO DE LOS JUGADORES,
                                // ENVIAR PETICIÓN. FIREBASE.

                            }else if (i==1){
                                // Si se selecciona el primer elemento, es la categoría "---"

                            }
                            // Cuando ya se ha selecionado
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDia = alert.create();
                    // Mostrar el alert
                    alertDia.show();

                    // ............
                }else if(item.nombre=="Crear/Unirse juego"){
                    Intent intent = new Intent (view.getContext(), GestionSalas.class);
                    intent.putExtra("usuario", nombreUsuario);
                    startActivity(intent);
                    finish();

                }
            }
        });


    }






    // Método para añadir datos a la lista personalizada
    private List<ItemsMenuView> getData() {
        list = new ArrayList<>();

        // Añadimos los items a la lista
        list.add(new ItemsMenuView(1, R.drawable.interrogacion,"Categoría", "Escoge una categoría."));
        list.add(new ItemsMenuView(2, R.drawable.usuario,"Crear/Unirse juego", "Crea un juego y espera a que alguien se una."));
        //list.add(new ItemsMenuView(2, R.drawable.usuario,"Unirse a juego", "Únete a un juego ya creado."));



        // Devolvemos la lista
        return list;
    }
}