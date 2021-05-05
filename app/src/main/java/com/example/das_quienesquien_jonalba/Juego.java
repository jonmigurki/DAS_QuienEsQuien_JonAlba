package com.example.das_quienesquien_jonalba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Juego extends AppCompatActivity {

    DatabaseReference databaseReference;

    TextView turnoJugador;
    Button btnCambiarTurno;

    String turnoActual;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);



        turnoJugador = (TextView) findViewById(R.id.txtTurnoJugador);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("juego");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String turno = snapshot.child("turno").getValue().toString();
                turnoActual = turno;
                turnoJugador.setText("Turno del jugador " + turno);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Failed to read value.", error.toException().toString());

            }
        });


        RecyclerView lalista = (RecyclerView) findViewById(R.id.RecyclerView);

        //  int[] personajes = {R.drawable.bart, R.drawable.edna, R.drawable.homer, R.drawable.lisa, R.drawable.seymour};
        int[] personajes = getImagenesCategoria("los_simpsons");
        String[] nombres = getNombresCategoria("los_simpsons");

        ElAdaptadorRecycler eladaptador = new ElAdaptadorRecycler(nombres, personajes,this);
        lalista.setAdapter(eladaptador);

        GridLayoutManager elLayoutRejillaIgual = new GridLayoutManager(this, 5, GridLayoutManager.VERTICAL, false);
        lalista.setLayoutManager(elLayoutRejillaIgual);

        btnCambiarTurno = (Button) findViewById(R.id.btnCambiarTurno);
        btnCambiarTurno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(turnoActual.equals("1")){
                    databaseReference.child("turno").setValue("2");
                }else{
                    databaseReference.child("turno").setValue("1");
                }
            }
        });



    }


    private int[] getImagenesCategoria(String categoria) {

        int[] resultado = new int[15];

        for (int x = 0; x < 15; x++) {

            int im = getResources().getIdentifier("los_simpsons_" + String.valueOf(x + 1), "drawable", Juego.this.getPackageName());
            resultado[x] = im;

        }

        return resultado;

    }

    private String[] getNombresCategoria(String categoria) {

        String[] resultado = new String[15];

        InputStream is = this.getResources().openRawResource(R.raw.categorias);

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));


        try {
            String linea = reader.readLine();

            boolean categoriaEncontrada = false;

            while (!categoriaEncontrada) {

                linea = reader.readLine();

                if (linea.equals(categoria)) {
                    categoriaEncontrada = true;

                }

            }

            linea = reader.readLine();

            resultado = linea.split(";");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultado;

    }

}