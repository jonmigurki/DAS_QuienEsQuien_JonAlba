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

    boolean juego = false; //indica si el juego ha comenzado o no

    boolean hayGanador=false;

    String nombreP1;

    String categoria = "";

    //Nombre que tiene la sala actual en la base de datos de Firebase
    String nombreSala;

    //Guardamos toda la información acerca de los personajes de la categoría elegida en cada jugada
    int[] rutapersonajes;
    String[] nombrespersonajes;

    TextView txtSala;   //TextView que visualiza el número de la sala en la pantalla

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);

        //Obtenemos el usuario identificado, la categoría escogida y la sala creada
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuarioIdentificado = extras.getString("usuario");
            categoria = extras.getString("categoria");
            nombreSala = extras.getString("sala");
        }

        //Indicamos el nombre de la sala y la visualizamos en el layout
        txtSala = (TextView) findViewById(R.id.txtSala);
        txtSala.setText("SALA " + nombreSala.split("_")[0].substring(4));

        //Obtenemos todos los nombres de los personajes y sus imágenes dada la categoría escogida
        rutapersonajes = getImagenesCategoria(categoria);
        nombrespersonajes = getNombresCategoria(categoria);

        //Obtenemos el ImageView del layout para visualizar el personaje del jugador
        imagenPersonaje = (ImageView) findViewById(R.id.imgPersonaje);

        //Se obtiene un personaje aleatorio con el que cada jugador jugará
        obtenerPersonajeAleatorio();


        firebaseDatabase = FirebaseDatabase.getInstance();



        databaseReference = firebaseDatabase.getReference("juegos");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Si hay en la BD un jugador1 y jugador2 la partida comienza
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



        //TextView donde se visualizan las instrucciones a los jugadores en cada momento de la partida
        turnoJugador = (TextView) findViewById(R.id.txtInformacionPartida);


        //Creamos el tablero con los personajes
        RecyclerView lalista = (RecyclerView) findViewById(R.id.RecyclerView3);

        ElAdaptadorRecycler eladaptador = new ElAdaptadorRecycler(nombrespersonajes, rutapersonajes, this);
        lalista.setAdapter(eladaptador);

        GridLayoutManager elLayoutRejillaIgual = new GridLayoutManager(this, 5, GridLayoutManager.VERTICAL, false);
        lalista.setLayoutManager(elLayoutRejillaIgual);


        //Cuando pulsemos el botón de "CHAT" nos llevará a la actividad del chat
        btnChat = (Button) findViewById(R.id.btnChat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (juego) {
                    Intent i = new Intent(Juego.this, Chat.class);
                    i.putExtra("usuario", usuarioIdentificado);
                    i.putExtra("sala", nombreSala);
                    startActivity(i);
                } else {
                    Toast.makeText(Juego.this, "Espera a que el Jugador 2 entre", Toast.LENGTH_SHORT).show();
                }
            }
        });



        btnDescartados = (Button) findViewById(R.id.btnDescartados);


        btnResolver = (Button) findViewById(R.id.btnResolver);

        //El usuario decide resolver ya el juego
        btnResolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (juego) {        //Si la partida ha comenzado
                    databaseReference = firebaseDatabase.getReference("juegos/" + nombreSala);

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.child("turno").getValue().toString().equals(usuarioIdentificado)) {    //Es su turno

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


                            } else {        //No es su turno

                                Toast.makeText(Juego.this, "No puedes resolver hasta que sea tu turno", Toast.LENGTH_SHORT).show();

                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else {    //Si la partida aún no ha comenzado (el jugador 2 aún no ha entrado en la sala)
                    Toast.makeText(Juego.this, "Espera a que el Jugador 2 entre", Toast.LENGTH_SHORT).show();
                }
            }
        });

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


    private void obtenerPersonajeAleatorio() {

        //Establecemos un número aleatorio entre 0 y 14
        Random r = new Random();
        int random1 = r.nextInt(15);

        imagenPersonaje.setImageResource(rutapersonajes[random1]);

        //Guardamos el personaje que le ha tocado
        nombreP1 = nombrespersonajes[random1];


    }


    public void prejuego() {

        //Se acaba de empezar la partida
        databaseReference = firebaseDatabase.getReference("juegos/" + nombreSala);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String j1 = snapshot.child("jugador1").child("usuario").getValue().toString();
                String j2 = snapshot.child("jugador2").child("usuario").getValue().toString();

                //Guardamos en la BD el personaje del usuario identificado
                if (j1.equals(usuarioIdentificado)) {
                    databaseReference.child("jugador1").child("personaje").setValue(nombreP1);
                } else if (j2.equals(usuarioIdentificado)) {
                    databaseReference.child("jugador2").child("personaje").setValue(nombreP1);
                }


                jugador1 = j1;
                jugador2 = j2;

                //Guardamos en la BD variables nuevas (turno, preguntaRealizada, respuestaRealizada, ronda...)

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

    public void juego() {

        //Mantenemos un Listener en la BD para cualquier cambio que ocurra en ella
        databaseReference = firebaseDatabase.getReference("juegos/" + nombreSala);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //El botón de "Hecho" se mantendrá oculto hasta que llegue el momento de descartar a los personajes
                btnDescartados.setVisibility(View.INVISIBLE);

                //Si el turno actual es el del usuario identificado
                if (snapshot.child("turno").getValue().toString().equals(usuarioIdentificado)) {

                    //Si aún no se ha hecho una pregunta, se le indica que la haga
                    if (snapshot.child("preguntaRealizada").getValue().toString().equals("false")) {
                        turnoJugador.setText("Vete al chat y haz una pregunta");
                    }

                    //Si el otro jugador ya le ha respondido a la pregunta, se le indica que la lea y descarte a los personajes de su tablero
                    if (snapshot.child("respuestaRealizada").getValue().toString().equals("true")) {

                        //Se deben descartar los personajes que no cumplan con la característica

                        turnoJugador.setText("Ya ha respondido. Lee la respuesta en el chat y descarta a los personajes");

                        //Ponemos visible el botón de "Hecho"
                        btnDescartados.setVisibility(View.VISIBLE);

                        //Si ya ha hecho la pregunta, pero aún no hay respuesta, se le indica que espere a que el otro jugador conteste
                    } else if (snapshot.child("respuestaRealizada").getValue().toString().equals("false") &&
                            snapshot.child("preguntaRealizada").getValue().toString().equals("true")) {

                        turnoJugador.setText("Espera a que el otro jugador responda");

                    }


                    //Si el turno es del otro jugador
                } else if (!(snapshot.child("turno").getValue().toString().equals(usuarioIdentificado))) {

                    //Si el jugador ya ha respondido, se le indica que espere a que el contrincante descarte los personajes de su tablero
                    if (snapshot.child("respuestaRealizada").getValue().toString().equals("true")) {
                        turnoJugador.setText("Espera a que el otro jugador descarte sus personajes");
                    }

                    //Si el contrincante ya ha preguntado y este jugador aún no ha dado respuesta, se le indica que la realice en el chat
                    if (snapshot.child("preguntaRealizada").getValue().toString().equals("true") &&
                            snapshot.child("respuestaRealizada").getValue().toString().equals("false")) {

                        turnoJugador.setText("El otro jugador ya ha preguntado. Responde en el chat");

                        //Si aún no hay pregunta, se le indica que espere a que el contrincante la realice
                    } else if (snapshot.child("preguntaRealizada").getValue().toString().equals("false")) {

                        turnoJugador.setText("Espera a que el otro jugador pregunte");
                    }


                }


                //Si un jugador ha resuelto ya el juego y ha sido el otro jugador al usuario que se ha identificado
                if (snapshot.child("ganador").exists() && snapshot.child("comprueba").exists() && !(snapshot.child("comprueba").getValue().toString().equals(usuarioIdentificado))) {

                    //Gana el que no ha resuelto (el que resuelve ha fallado)
                    if (snapshot.child("ganador").getValue().toString().equals(usuarioIdentificado)) {

                        mostrarAnimacion("ganado");

                    } else {

                        mostrarAnimacion("perdido");

                    }

                }


                //Si hay un jugador que ha abandonado (ha salido de la aplicación) y no es el identificado, éste ha ganado
                if (snapshot.child("abandona").exists() && !(snapshot.child("abandona").getValue().toString().equals(usuarioIdentificado))) {

                    mostrarAnimacion("ganado");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //Cuando el usuario ya haya descartado, pulsará el botón de "Hecho"
        btnDescartados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseReference = firebaseDatabase.getReference("juegos/" + nombreSala);

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //Cambiamos el turno actual
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

                        //Y volvemos a poner invisible el botón
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

        hayGanador=true;

        //Si la animación debe ser de ganador
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

            //Si la animación debe ser de perdedor
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


    public void onBackPressed() {

        if(!hayGanador) {

            AlertDialog.Builder alertdialog = new AlertDialog.Builder(this);
            alertdialog.setTitle("¿Deseas salir?");
            alertdialog.setMessage("Si sales perderás la partida...");
            alertdialog.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    databaseReference = firebaseDatabase.getReference("juegos/" + nombreSala);
                    databaseReference.child("abandona").setValue(usuarioIdentificado);

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.child("jugador1").getValue().toString().equals(usuarioIdentificado)) {
                                String j2 = snapshot.child("jugador2").child("usuario").getValue().toString();
                                databaseReference.child("ganador").setValue(j2);
                            } else {
                                String j1 = snapshot.child("jugador1").child("usuario").getValue().toString();
                                databaseReference.child("ganador").setValue(j1);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                    Intent i = new Intent(Juego.this, MenuPrincipal.class);
                    startActivity(i);
                    finish();
                }
            });

            alertdialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            alertdialog.show();

        }
    }

}