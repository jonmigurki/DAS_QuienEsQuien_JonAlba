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

    ListView listView;
    Button button;

    List<String> listaSalas;

    String nombreJugador = "";
    String nombreSala = "";

    FirebaseDatabase database;
    DatabaseReference salaRef;
    DatabaseReference salasRef;

    String categoria = "";

    boolean valido = true;

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

        nombreSala = nombreJugador;

        // Asignamos los id a las variables
        listView = findViewById(R.id.listView);

        // ArrayList con todas las salas disponibles
        listaSalas = new ArrayList<>();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Unirse a una sala existente y entrar como el jugador2
                nombreSala = (String) (listView.getItemAtPosition(position).toString());

                HashMap<String,String> jugador = new HashMap<String,String>();
                jugador.put("usuario", nombreJugador);
                jugador.put("personaje", "");

                salaRef = database.getReference("juegos");
                salaRef.child(nombreSala).child("jugador2").setValue(jugador);

                salaRef = database.getReference("juegos/" + nombreSala);
                salaRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        categoria = snapshot.child("categoria").getValue().toString();

                        if(snapshot.child("jugador1").child("usuario").getValue().toString().equals(nombreJugador)){
                            valido=false;
                        }else{
                            valido=true;
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                AlertDialog.Builder adb = new AlertDialog.Builder(GestionSalas.this);
                adb.setTitle("¿Quieres unirte a la partida?");
                adb.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        salaRef = database.getReference("juegos/" + nombreSala);
                        salaRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                categoria = snapshot.child("categoria").getValue().toString();

                                if(snapshot.child("jugador1").child("usuario").getValue().toString().equals(nombreJugador)){
                                    valido=false;
                                }else{
                                    valido=true;
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                        if(valido) {
                            Intent intent = new Intent(getApplicationContext(), Juego.class);
                            intent.putExtra("usuario", nombreJugador);
                            intent.putExtra("categoria", categoria);
                            intent.putExtra("sala", nombreSala);
                            startActivity(intent);
                            finish();
                        }else{
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


    private void mostrarListaSalas(){
        salasRef = database.getReference("juegos");
        salasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Mostrar la lista de los juegos disponibles
                listaSalas.clear();
                Iterable<DataSnapshot> salas = dataSnapshot.getChildren();
                for(DataSnapshot snapshot : salas){
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