package com.example.das_quienesquien_jonalba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MenuPrincipal extends AppCompatActivity {

    // Instanciamos la ListView
    ListView ListViewItem;
    List<ItemsMenuView> list;

    String[] listaCategorias = {"Los Simpson", "ANHQV – Aquí No Hay Quien Viva", "Disney"};
    String[] listaJugadores = {"JugadorPruebas", "Jugador2"};

    String nombreUsuario;

    //--------------------------------------


    String nombreJugador = "";
    String nombreSala = "";
    String categoria = "";

    FirebaseDatabase database;
    DatabaseReference salaRef;
    DatabaseReference salasRef;
    //--------------------------------------

    List<String> listaSalas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);


        database = FirebaseDatabase.getInstance();


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            nombreJugador = extras.getString("usuario");
        }
        listaSalas = new ArrayList<>();


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
                if(item.id==1){

                    // Creamos el AlertDialog con las opciones disponibles
                    AlertDialog.Builder alert = new AlertDialog.Builder(MenuPrincipal.this);
                    alert.setSingleChoiceItems(listaCategorias, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            // Obtenemos la categoría seleccionada por el usuario
                            String c = listaCategorias[i];
                            switch(i){
                                case 0:
                                    categoria = "los_simpsons";
                                    break;
                                case 1:
                                    categoria = "anhqv";
                                    break;
                                case 2:
                                    categoria = "disney";
                                    break;
                            }
                            //categoria = listaCategorias[i];
                            // Creamos la sala
                            crearSalaJuego();

                            // Cuando ya se ha selecionado
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDia = alert.create();
                    // Mostrar el alert
                    alertDia.show();


                }else if(item.id==2){
                    Intent intent = new Intent (view.getContext(), GestionSalas.class);
                    intent.putExtra("usuario", nombreJugador);
                    startActivity(intent);
                    finish();

                }
            }
        });
    }



    private void crearSalaJuego(){
        //nombreSala = nombreJugador;
        nombreSala = "juego1";

        HashMap<String,String> jugador = new HashMap<String,String>();
        jugador.put("usuario", nombreJugador);
        jugador.put("personaje", "");

        salaRef = database.getReference("juegos");
        salaRef.child("juego1").child("jugador1").setValue(jugador);
        salaRef = database.getReference("juegos/" + nombreSala);
        salaRef.child("categoria").setValue(categoria);

        Intent intent = new Intent(getApplicationContext(), Juego.class);
        intent.putExtra("usuario", nombreJugador);
        intent.putExtra("categoria", categoria);
        startActivity(intent);
    }



    // Método para añadir datos a la lista personalizada
    private List<ItemsMenuView> getData() {
        list = new ArrayList<>();

        // Añadimos los items a la lista
        list.add(new ItemsMenuView(1, R.drawable.player1,"CREAR JUEGO", "Crea un juego nuevo eligiendo una categoría y espera a que se una un contrincante"));
        list.add(new ItemsMenuView(2, R.drawable.player2,"UNIRSE", "Únete a un juego ya creado"));

        // Devolvemos la lista
        return list;
    }
}