package com.example.das_quienesquien_jonalba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

public class Juego extends AppCompatActivity {

    //Variables para la conexión a la Realtime Database de Firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //Guardamos el usuario que se ha identificado en el juego
    String usuarioIdentificado;

    TextView turnoJugador;
    Button btnResolver, btnChat, btnDescartados;
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

    String categoria = "";

    //Nombre que tiene la sala actual en la base de datos de Firebase
    String nombreSala;

    //Guardamos toda la información acerca de los personajes de la categoría elegida en cada jugada
    int[] rutapersonajes;
    String[] nombrespersonajes;

    TextView txtSala;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuarioIdentificado = extras.getString("usuario");
            categoria = extras.getString("categoria");
            nombreSala = extras.getString("sala");
        }


        txtSala = (TextView) findViewById(R.id.txtSala);
        txtSala.setText("SALA " + nombreSala.split("_")[0].substring(4));


        //  int[] personajes = {R.drawable.bart, R.drawable.edna, R.drawable.homer, R.drawable.lisa, R.drawable.seymour};
        rutapersonajes = getImagenesCategoria(categoria);
        nombrespersonajes = getNombresCategoria(categoria);

        Log.d("HOLAA", "HOLAA");
        Log.d("NOMBRES PERSONAJES", nombrespersonajes[0].toString());

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
    //    String fecha = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    //    String hora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        //   Log.d("FECHA", fecha);
        //  Log.d("HORA", hora);

        //jugada = "juego_" + fecha + "_" + hora;
       // jugada = "juego1";


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


                if (snapshot.child(nombreSala).hasChild("jugador1") && snapshot.child(nombreSala).hasChild("jugador2") && !juego) {
                    turnoJugador.setText("Empieza la partida");
                    juego = true;
                    prejuego();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // databaseReference.child(jugada).child("jugador2").setValue(jugador2);


        turnoJugador = (TextView) findViewById(R.id.txtInformacionPartida);

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
        RecyclerView lalista = (RecyclerView) findViewById(R.id.RecyclerView3);

        ElAdaptadorRecycler eladaptador = new ElAdaptadorRecycler(nombrespersonajes, rutapersonajes, this);
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
                if(juego) {
                    Intent i = new Intent(Juego.this, Chat.class);
                    i.putExtra("usuario", usuarioIdentificado);
                    i.putExtra("sala", nombreSala);
                    startActivity(i);
                }else{
                    Toast.makeText(Juego.this, "Espera a que el Jugador 2 entre", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //MÉTODO CON EL QUE EMPIEZA EL JUEGO
        // juego();

        btnDescartados = (Button) findViewById(R.id.btnDescartados);


        btnResolver = (Button) findViewById(R.id.btnResolver);

        btnResolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(juego) {
                    databaseReference = firebaseDatabase.getReference("juegos/" + nombreSala);

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.child("turno").getValue().toString().equals(usuarioIdentificado)) {

                                AlertDialog.Builder adb = new AlertDialog.Builder(Juego.this);
                                adb.setTitle("¿Deseas resolver ya? No podrás volver");
                                adb.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent i = new Intent(Juego.this, Resolver.class);
                                        i.putExtra("sala", nombreSala);
                                        i.putExtra("usuario", usuarioIdentificado);
                                        i.putExtra("nombrespersonajes", nombrespersonajes);
                                        i.putExtra("rutapersonajes", rutapersonajes);
                                        startActivity(i);
                                        finish();

                                    }
                                });

                                adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                adb.show();


                            } else {

                                Toast.makeText(Juego.this, "No puedes resolver hasta que sea tu turno", Toast.LENGTH_SHORT).show();

                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }else{
                    Toast.makeText(Juego.this, "Espera a que el Jugador 2 entre", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


        //Obtenemos una lista con todas las instancias de las imágenes de la categoría elegida
        private int[] getImagenesCategoria (String categoria){

            int[] resultado = new int[15];

            for (int x = 0; x < 15; x++) {

                int im = getResources().getIdentifier(categoria + "_" + String.valueOf(x + 1), "drawable", Juego.this.getPackageName());
                resultado[x] = im;

            }

            return resultado;

        }


        //Obtenemos una lista con todos los nombres de los personajes de la categoría elegida
        private String[] getNombresCategoria (String categoria){

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


        private void obtenerPersonajeAleatorio () {

            //Establecemos un número aleatorio entre 0 y 14
            Random r = new Random();
            int random1 = r.nextInt(15);
            //int random2 = r.nextInt(15);

            imagenPersonaje.setImageResource(rutapersonajes[random1]);
            //imagenPersonaje.setImageResource(rutapersonajes[random2]);

            //Guardamos el personaje que le ha tocado
            nombreP1 = nombrespersonajes[random1];
            //nombreP2 = nombrespersonajes[random2];

        }


        public void prejuego () {

            //Se acaba de empezar la partida
            databaseReference = firebaseDatabase.getReference("juegos/" + nombreSala);

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String j1 = snapshot.child("jugador1").child("usuario").getValue().toString();
                    String j2 = snapshot.child("jugador2").child("usuario").getValue().toString();

                    if (j1.equals(usuarioIdentificado)) {
                        databaseReference.child("jugador1").child("personaje").setValue(nombreP1);
                    } else if (j2.equals(usuarioIdentificado)) {
                        databaseReference.child("jugador2").child("personaje").setValue(nombreP1);
                    }

                    //databaseReference.child("jugador1").child("personaje").setValue(nombreP1);
                    //databaseReference.child("jugador2").child("personaje").setValue(nombreP2);


                    jugador1 = j1;
                    jugador2 = j2;


                    databaseReference.child("turno").setValue(jugador1);
                    turno = jugador1;

                    databaseReference = firebaseDatabase.getReference("juegos/" + nombreSala);
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

        public void juego () {


            databaseReference = firebaseDatabase.getReference("juegos/" + nombreSala);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    btnDescartados.setVisibility(View.INVISIBLE);

                    Log.d("HOLAAAAA", "ESTOY AQUII PRUEBA 11 JUEGO");
                    Log.d("USUARIO IDENTIFICADO", usuarioIdentificado);

                    if (snapshot.child("turno").getValue().toString().equals(usuarioIdentificado)) {

                        //MI TURNO
                        //Debo escribir la pregunta

                        if (snapshot.child("preguntaRealizada").getValue().toString().equals("false")) {
                            turnoJugador.setText("Vete al chat y haz una pregunta");
                        }

                        if (snapshot.child("respuestaRealizada").getValue().toString().equals("true")) {

                            //Se deben descartar los personajes que no cumplan con la característica

                            turnoJugador.setText("Ya ha respondido. Lee la respuesta en el chat y descarta a los personajes");

                            btnDescartados.setVisibility(View.VISIBLE);

                        } else if (snapshot.child("respuestaRealizada").getValue().toString().equals("false") &&
                                snapshot.child("preguntaRealizada").getValue().toString().equals("true")) {

                            turnoJugador.setText("Espera a que el otro jugador responda");

                        }


                    } else if (!(snapshot.child("turno").getValue().toString().equals(usuarioIdentificado))) {

                        //EL TURNO DEL OTRO JUGADOR
                        //Debo responder a su pregunta

                        if (snapshot.child("respuestaRealizada").getValue().toString().equals("true")) {
                            turnoJugador.setText("Espera a que el otro jugador descarte sus personajes");
                        }


                        if (snapshot.child("preguntaRealizada").getValue().toString().equals("true") &&
                                snapshot.child("respuestaRealizada").getValue().toString().equals("false")) {

                            turnoJugador.setText("El otro jugador ya ha preguntado. Responde en el chat");

                        } else if (snapshot.child("preguntaRealizada").getValue().toString().equals("false")) {

                            turnoJugador.setText("Espera a que el otro jugador pregunte");
                        }


                    }


                    if(snapshot.child("ganador").exists() && !(snapshot.child("comprueba").getValue().toString().equals(usuarioIdentificado))){


                        if(snapshot.child("ganador").getValue().toString().equals(usuarioIdentificado)){        //Gana el que no ha resuelto (el que resuelve ha fallado)

                            mostrarAnimacion("ganado");

                        }else{

                            mostrarAnimacion("perdido");

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

                    databaseReference = firebaseDatabase.getReference("juegos/" + nombreSala);

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.child("turno").getValue().toString().equals(jugador1)) {
                                databaseReference.child("turno").setValue(jugador2);
                            } else if (snapshot.child("turno").getValue().toString().equals(jugador2)) {
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

        public void mostrarAnimacion(String resultado) {

            if (resultado.equals("ganado")) {

                ImageView image = new ImageView(Juego.this);
                String i = "ganado";
                int im = getResources().getIdentifier(i, "drawable", this.getPackageName());
                image.setImageResource(im);


                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setTitle("¡¡HAS GANADO!!");
                adb.setView(image);
                adb.setPositiveButton("Compartir", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        Intent i = new Intent(Juego.this, MenuPrincipal.class);
                        i.putExtra("usuario", usuarioIdentificado);
                        startActivity(i);


                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_TEXT, "¡HE GANADO AL QUIEN ES QUIEN. CORRE Y DESCÁRGATELO, QUE ESTÁ SUPER CHULO!");
                        intent.setType("text/plain");
                        intent.setPackage("com.whatsapp");
                        startActivity(intent);


                        finish();


                    }
                });

                adb.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent i = new Intent(Juego.this, MenuPrincipal.class);
                        i.putExtra("usuario", usuarioIdentificado);
                        startActivity(i);
                        finish();

                    }
                });

                adb.show();


            } else {


                ImageView image = new ImageView(Juego.this);
                String i = "perdido";
                int im = getResources().getIdentifier(i, "drawable", this.getPackageName());
                image.setImageResource(im);


                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setTitle("¡¡HAS PERDIDO!!");
                adb.setView(image);
                adb.setPositiveButton("Compartir", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        Intent i = new Intent(Juego.this, MenuPrincipal.class);
                        i.putExtra("usuario", usuarioIdentificado);
                        startActivity(i);


                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_TEXT, "He perdido al QUIEN ES QUIEN :( \n Pero nunca me rendiré!!!");
                        intent.setType("text/plain");
                        intent.setPackage("com.whatsapp");
                        startActivity(intent);


                        finish();


                    }
                });

                adb.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent i = new Intent(Juego.this, MenuPrincipal.class);
                        i.putExtra("usuario", usuarioIdentificado);
                        startActivity(i);
                        finish();

                    }
                });

                adb.show();


            }

        }

    }

