package com.example.das_quienesquien_jonalba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_salas);

        database = FirebaseDatabase.getInstance();



        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            nombreJugador = extras.getString("usuario");
        }



        //SharedPreferences preferences = getSharedPreferences("PREFS", 0);
        //playerName = preferences.getString("playerName", "");
        nombreSala = nombreJugador;

        listView = findViewById(R.id.listView);
        button = findViewById(R.id.button);

        // Todas las salas disponibles
        listaSalas = new ArrayList<>();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creamos la sala y añadimos al jugador como jugador1
                button.setText("Creando sala");
                button.setEnabled(false);

                nombreSala = nombreJugador;
                salaRef = database.getReference("salas/"+ nombreSala +"/jugador1");
                
                crearSala();

                salaRef.setValue(nombreJugador);
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Unirse a una sala existente y entrar como el jugador2
                nombreSala = listaSalas.get(position);
                salaRef = database.getReference("salas/"+ nombreSala +"/jugador2");
                crearSala();
                salaRef.setValue(nombreJugador);
            }
        });

        mostrarListaSalas();
    }

    private void crearSala() {
        salaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                button.setText("Crear sala");
                button.setEnabled(true);

                Intent intent = new Intent(getApplicationContext(), Juego.class);
                intent.putExtra("nombreSala", nombreSala);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                button.setText("Crear sala");
                button.setEnabled(true);
                // Notificamos al usuario de que ha ocurrido un error
                Toast.makeText(GestionSalas.this,"Ha ocurrido un error.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarListaSalas(){
        salasRef = database.getReference("salas");
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