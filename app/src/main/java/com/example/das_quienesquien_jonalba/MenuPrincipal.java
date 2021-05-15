package com.example.das_quienesquien_jonalba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import java.util.List;

public class MenuPrincipal extends AppCompatActivity {

    // Instanciamos la ListView
    ListView ListViewItem;
    List<ItemsMenuView> list;

    String[] listaCategorias = {"Los Simpson", "Otros"};
    String[] listaJugadores = {"JugadorPruebas", "Jugador2"};

    String nombreUsuario;

    //--------------------------------------


    String nombreJugador = "";
    String nombreSala = "";

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
                }else if(item.nombre=="Crear juego"){
                    nombreSala = nombreJugador;
                    salaRef = database.getReference("salas/"+ nombreSala +"/jugador1");

                    crearSala();

                    salaRef.setValue(nombreJugador);


                }else if(item.nombre=="Unirse a juego"){
                    Intent intent = new Intent (view.getContext(), GestionSalas.class);
                    intent.putExtra("usuario", nombreUsuario);
                    startActivity(intent);
                    finish();

                }
            }
        });


    }



    private void crearSala() {
        salaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                Intent intent = new Intent(getApplicationContext(), Juego.class);
                intent.putExtra("nombreSala", nombreSala);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                // Notificamos al usuario de que ha ocurrido un error
                Toast.makeText(MenuPrincipal.this,"Ha ocurrido un error.",Toast.LENGTH_SHORT).show();
            }
        });
    }




    // Método para añadir datos a la lista personalizada
    private List<ItemsMenuView> getData() {
        list = new ArrayList<>();

        // Añadimos los items a la lista
        list.add(new ItemsMenuView(1, R.drawable.interrogacion,"Categoría", "Escoge una categoría."));
        list.add(new ItemsMenuView(2, R.drawable.usuario,"Crear juego", "Crea un juego y espera a que alguien se una."));
        list.add(new ItemsMenuView(3, R.drawable.usuario,"Unirse a juego", "Únete a un juego ya creado."));



        // Devolvemos la lista
        return list;
    }
}