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

    //Variables para la conexión a la Realtime Database de Firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //Guardamos el usuario que se ha identificado en el juego
    String usuarioIdentificado;

    TextView turnoJugador;
    Button btnCambiarTurno, btnChat, btnDescartados;
    ImageView imagenPersonaje;



    //VARIABLES DE LA JUGADA
    String jugador1 = "", jugador2 = "";
    String turno; //quién hace la pregunta
    int ronda; //ronda 1, 2, 3, ...
    boolean preguntaRealizada = false;
    boolean respuestaRealizada = false; //estos dos booleanos ayudarán a que SOLAMENTE se pueda hacer una pregunta y una respuesta por ronda (sólo 1 mensaje por ronda)

    boolean juego = false;
    boolean fin = false;    //indica si la partida ha acabado


    String nombreP1, nombreP2, imagenP;    //Variables que guardan el nombre del personaje ('Homer Simpson') y su imagen asociada ('los_simpsons_5.png')

    String categoria = "los_simpsons";

    //Nombre que tiene la jugada actual en la base de datos de Firebase
    String jugada;

    //Guardamos toda la información acerca de los personajes de la categoría elegida en cada jugada
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

        //Se obtiene un personaje aleatorio con el que cada jugador jugará
        obtenerPersonajeAleatorio();


        firebaseDatabase = FirebaseDatabase.getInstance();
     //   databaseReference = firebaseDatabase.getReference("juegos");

/*
        HashMap<String,String> jugador = new HashMap<String,String>();
        jugador.put("usuario", usuarioIdentificado);
        jugador.put("personaje", nombreP);

       HashMap<String,String> jugador2 = new HashMap<String,String>();
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


        //Guardamos a los jugadores en la nueva partida
        //  -Si no hay un "jugador1" lo establecemos como "jugador1"
        //  -Si ya hay un "jugador1" lo establecemos como "jugador2"
        databaseReference = firebaseDatabase.getReference("juegos");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            /*
                if (snapshot.child(jugada).hasChild("jugador1") && !(snapshot.child(jugada).hasChild("jugador2")) && !(usuarioIdentificado.equals(jugador1))) {
                    databaseReference.child(jugada).child("jugador2").setValue(jugador);

                }else if(!(snapshot.child(jugada).hasChild("jugador1"))) {
                    databaseReference.child(jugada).child("jugador1").setValue(jugador);
                    turnoJugador.setText("Esperando la aprobación del jugador2");
                    jugador1 = usuarioIdentificado;

                }else if(snapshot.child(jugada).hasChild("jugador1") && snapshot.child(jugada).hasChild("jugador2") && !juego){
                    turnoJugador.setText("Empieza la partida");
                    juego=true;
                    prejuego();
                }*/



                if(snapshot.child(jugada).hasChild("jugador1") && snapshot.child(jugada).hasChild("jugador2") && !juego){
                    turnoJugador.setText("Empieza la partida");
                    juego=true;
                    prejuego();
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


        //Creamos el tablero con los personajes
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

        //Cuando pulsemos el botón de "CHAT" nos llevará a la actividad del chat
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


        //MÉTODO CON EL QUE EMPIEZA EL JUEGO
       // juego();

        btnDescartados = (Button) findViewById(R.id.btnDescartados);

    }


    //Obtenemos una lista con todas las instancias de las imágenes de la categoría elegida
    private int[] getImagenesCategoria(String categoria) {

        int[] resultado = new int[15];

        for (int x = 0; x < 15; x++) {

            int im = getResources().getIdentifier(categoria + "_" + String.valueOf(x + 1), "drawable", Juego.this.getPackageName());
            resultado[x] = im;

        }

        return resultado;

    }


    //Obtenemos una lista con todos los nombres de los personajes de la categoría elegida
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

        //Establecemos un número aleatorio entre 0 y 14
        Random r = new Random();
        int random1 = r.nextInt(15);
        int random2 = r.nextInt(15);

        imagenPersonaje.setImageResource(rutapersonajes[random1]);
        imagenPersonaje.setImageResource(rutapersonajes[random2]);

        //Guardamos el personaje que le ha tocado
        nombreP1 = nombrespersonajes[random1];
        nombreP2 = nombrespersonajes[random2];

    }




    public void prejuego() {

        //Se acaba de empezar la partida
        databaseReference = firebaseDatabase.getReference("juegos/" + jugada);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String j1 = snapshot.child("jugador1").child("usuario").getValue().toString();
                String j2 = snapshot.child("jugador2").child("usuario").getValue().toString();

                if(j1.equals(usuarioIdentificado)){
                    databaseReference.child("jugador1").child("personaje").setValue(nombreP1);
                }else if(j2.equals(usuarioIdentificado)){
                    databaseReference.child("jugador2").child("personaje").setValue(nombreP1);
                }

                //databaseReference.child("jugador1").child("personaje").setValue(nombreP1);
                //databaseReference.child("jugador2").child("personaje").setValue(nombreP2);



                jugador1 = j1;
                jugador2 = j2;


                databaseReference.child("turno").setValue(jugador1);
                turno = jugador1;

                databaseReference = firebaseDatabase.getReference("juegos/" + jugada);
                databaseReference.child("ronda").setValue(1);
                ronda = 1;
                databaseReference.child("preguntaRealizada").setValue("false");
                preguntaRealizada = false;
                databaseReference.child("respuestaRealizada").setValue("false");
                respuestaRealizada = false;

                juego();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    public void juego(){


        databaseReference = firebaseDatabase.getReference("juegos/" + jugada);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                btnDescartados.setVisibility(View.INVISIBLE);

                if(snapshot.child("turno").getValue().toString().equals(usuarioIdentificado)){

                    //MI TURNO
                    //Debo escribir la pregunta

                    if(snapshot.child("preguntaRealizada").getValue().toString().equals("false")){
                        turnoJugador.setText("Vete al chat y haz una pregunta");
                    }

                    if(snapshot.child("respuestaRealizada").getValue().toString().equals("true")){

                        //Se deben descartar los personajes que no cumplan con la característica

                        turnoJugador.setText("Te toca descartar los personajes");

                        btnDescartados.setVisibility(View.VISIBLE);

                    } else if(snapshot.child("respuestaRealizada").getValue().toString().equals("false") &&
                    snapshot.child("preguntaRealizada").getValue().toString().equals("true")){

                        turnoJugador.setText("Espera a que el otro jugador responda");

                    }





                }else if(!(snapshot.child("turno").getValue().toString().equals(usuarioIdentificado))){

                    //EL TURNO DEL OTRO JUGADOR
                    //Debo responder a su pregunta

                    if(snapshot.child("respuestaRealizada").getValue().toString().equals("true")){
                        turnoJugador.setText("Espera a que el otro jugador descarte sus personajes");
                    }


                    if(snapshot.child("preguntaRealizada").getValue().toString().equals("true") &&
                    snapshot.child("respuestaRealizada").getValue().toString().equals("false")){

                        turnoJugador.setText("Responde en el chat");

                    }else if(snapshot.child("preguntaRealizada").getValue().toString().equals("false")){

                        turnoJugador.setText("Espera a que el otro jugador pregunte");
                    }


                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        btnDescartados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseReference = firebaseDatabase.getReference("juegos/" + jugada);

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child("turno").getValue().toString().equals(jugador1)){
                            databaseReference.child("turno").setValue(jugador2);
                        }else if(snapshot.child("turno").getValue().toString().equals(jugador2)){
                            databaseReference.child("turno").setValue(jugador1);
                        }

                        //Al cambiar el turno, volvemos a empezar con una nueva ronda (no se han hecho aún pregunta y respuesta)
                        databaseReference.child("preguntaRealizada").setValue("false");
                        preguntaRealizada = false;
                        databaseReference.child("respuestaRealizada").setValue("false");
                        respuestaRealizada = false;


                        btnDescartados.setVisibility(View.INVISIBLE);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });



    }

}