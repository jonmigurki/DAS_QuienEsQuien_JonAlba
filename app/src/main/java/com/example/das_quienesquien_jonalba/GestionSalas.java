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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GestionSalas extends AppCompatActivity {

    // Instanciamos la ListView
    ListView listView;

    // Lista de salas disponibles
    List<String> listaSalas;

    // Variables para almacenar la información del jugador y sala
    String nombreJugador = "";
    String nombreSala = "";
    String categoria = "";
    boolean valido = true;

    // Instancias de Firebase
    FirebaseDatabase database;
    DatabaseReference salaRef;
    DatabaseReference salasRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_salas);

        // Creamos una instancia de la base de datos de Firebase
        database = FirebaseDatabase.getInstance();

        // Recogemos el nombre de usuario
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            nombreJugador = extras.getString("usuario");
        }

        // Asignamos los id a la variable
        listView = findViewById(R.id.listView);

        // ArrayList con todas las salas disponibles
        listaSalas = new ArrayList<>();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Unirse a una sala existente y entrar como el jugador2
                nombreSala = (String) (listView.getItemAtPosition(position).toString());

                // Construimos el HashMap "jugador" con su nombre de usuario y un personaje
                HashMap<String,String> jugador = new HashMap<String,String>();
                jugador.put("usuario", nombreJugador);
                jugador.put("personaje", "");

                // Asignamos el jugador2 en Firebase
                salaRef = database.getReference("juegos");
                salaRef.child(nombreSala).child("jugador2").setValue(jugador);

                salaRef = database.getReference("juegos/" + nombreSala);
                salaRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Obtenemos la categoría
                        categoria = snapshot.child("categoria").getValue().toString();

                        // Si el jugador1 y el jugador actual son el mismo
                        if(snapshot.child("jugador1").child("usuario").getValue().toString().equals(nombreJugador)){
                            // No es válido
                            valido=false;
                        }else{
                            // De lo contrario, sí es válido
                            valido=true;
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                // Cuando el usuario presiona un elemento de la lista, se muestra
                // un mensaje de confirmación
                AlertDialog.Builder adb = new AlertDialog.Builder(GestionSalas.this);
                adb.setTitle("¿Quieres unirte a la partida?");
                adb.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        salaRef = database.getReference("juegos/" + nombreSala);
                        salaRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                // Obtenemos la categoría
                                categoria = snapshot.child("categoria").getValue().toString();

                                // Si el jugador1 y el jugador actual son el mismo
                                if(snapshot.child("jugador1").child("usuario").getValue().toString().equals(nombreJugador)){
                                    // No es válido
                                    valido=false;
                                }else{
                                    // De lo contrario, sí es válido
                                    valido=true;
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        // Si es válido
                        if(valido) {
                            // Redirigimos al usuario a la pantalla de juego, pasándole
                            // el nombre de usuario, la categoria en la que se va a jugar
                            // y la sala
                            Intent intent = new Intent(getApplicationContext(), Juego.class);
                            intent.putExtra("usuario", nombreJugador);
                            intent.putExtra("categoria", categoria);
                            intent.putExtra("sala", nombreSala);
                            startActivity(intent);
                            finish();
                        }else{
                            // Si el usuario no es válido, no puede jugar
                            Toast.makeText(GestionSalas.this, "No puedes entrar. No puede estar el mismo jugador jugando contra sí mismo", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                adb.show();

            }
        });

        mostrarListaSalas();
    }

    // Método que muestra la lista de las salas disponibles
    private void mostrarListaSalas(){
        // Obtenemos la referencia de "juegos" en la base de datos de Firebase
        salasRef = database.getReference("juegos");
        salasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Mostrar la lista de los juegos disponibles

                // Limpiamos la lista
                listaSalas.clear();
                // Obtenemos las salas e iteramos
                Iterable<DataSnapshot> salas = dataSnapshot.getChildren();
                for(DataSnapshot snapshot : salas){
                    // Añadimos cada sala a la lista
                    listaSalas.add(snapshot.getKey());

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(GestionSalas.this, android.R.layout.simple_list_item_1, listaSalas);
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Notificamos al usuario de que ha ocurrido un error
                Toast.makeText(GestionSalas.this,"Ha ocurrido un error.",Toast.LENGTH_SHORT).show();
            }
        });

    }
}