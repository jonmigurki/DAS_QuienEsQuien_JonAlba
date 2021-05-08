package com.example.das_quienesquien_jonalba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class Juego extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    String usuarioIdentificado;

    TextView turnoJugador;
    Button btnCambiarTurno, btnChat;
    ImageView imagenPersonaje;

    String turnoActual;
    String nombreP, imagenP;    //Variables que guardan el nombre del personaje ('Homer Simpson') y su imagen asociada ('los_simpsons_5.png')

    String categoria = "los_simpsons";

    String jugada;


    int[] rutapersonajes;
    String[] nombrespersonajes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuarioIdentificado = extras.getString("usuario");
        }


        //  int[] personajes = {R.drawable.bart, R.drawable.edna, R.drawable.homer, R.drawable.lisa, R.drawable.seymour};
        rutapersonajes = getImagenesCategoria(categoria);
        nombrespersonajes = getNombresCategoria(categoria);


        imagenPersonaje = (ImageView) findViewById(R.id.imgPersonaje);

        obtenerPersonajeAleatorio();


        firebaseDatabase = FirebaseDatabase.getInstance();
     //   databaseReference = firebaseDatabase.getReference("juegos");

        HashMap<String,String> jugador = new HashMap<String,String>();
        jugador.put("usuario", usuarioIdentificado);
        jugador.put("personaje", nombreP);

       /* HashMap<String,String> jugador2 = new HashMap<String,String>();
        jugador2.put("usuario", "alba01");
        jugador2.put("personaje", "Homer Simpson");
        */



       // Date fecha = Calendar.getInstance().getTime();
        String fecha = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String hora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

     //   Log.d("FECHA", fecha);
      //  Log.d("HORA", hora);

        //jugada = "juego_" + fecha + "_" + hora;
        jugada = "juego1";

        databaseReference = firebaseDatabase.getReference("juegos");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(jugada).hasChild("jugador1")) {
                    databaseReference.child(jugada).child("jugador2").setValue(jugador);
                }else{
                    databaseReference.child(jugada).child("jugador1").setValue(jugador);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


       // databaseReference.child(jugada).child("jugador2").setValue(jugador2);



        turnoJugador = (TextView) findViewById(R.id.txtTurnoJugador);

 /*       databaseReference = FirebaseDatabase.getInstance().getReference().child("juego");

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
*/

        RecyclerView lalista = (RecyclerView) findViewById(R.id.RecyclerView1);


        ElAdaptadorRecycler eladaptador = new ElAdaptadorRecycler(nombrespersonajes, rutapersonajes,this);
        lalista.setAdapter(eladaptador);

        GridLayoutManager elLayoutRejillaIgual = new GridLayoutManager(this, 5, GridLayoutManager.VERTICAL, false);
        lalista.setLayoutManager(elLayoutRejillaIgual);

    /*    btnCambiarTurno = (Button) findViewById(R.id.btnCambiarTurno);
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
*/

        btnChat = (Button) findViewById(R.id.btnChat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Juego.this, Chat.class);
                i.putExtra("usuario", usuarioIdentificado);
                i.putExtra("jugada", jugada);
                startActivity(i);
            }
        });



    }


    private int[] getImagenesCategoria(String categoria) {

        int[] resultado = new int[15];

        for (int x = 0; x < 15; x++) {

            int im = getResources().getIdentifier(categoria + "_" + String.valueOf(x + 1), "drawable", Juego.this.getPackageName());
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


    private void obtenerPersonajeAleatorio(){

        Random r = new Random();
        int random = r.nextInt(15);

        imagenPersonaje.setImageResource(rutapersonajes[random]);

        nombreP = nombrespersonajes[random];

    }

}