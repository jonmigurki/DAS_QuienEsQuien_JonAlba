package com.example.das_quienesquien_jonalba;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Chat extends AppCompatActivity {

    EditText txtMensaje;
    Button btnEnviar, btnAtras;

    String usuarioIdentificado, nombreSala;

    AdaptadorChat adaptador;

    RecyclerView lalista;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    //Este booleano sirve para controlar uno de los listeners y que no se visualicen los mensajes por duplicado
    boolean recienEntrado = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuarioIdentificado = extras.getString("usuario");
            nombreSala = extras.getString("sala");
        }

        recienEntrado = true;

        adaptador = new AdaptadorChat(usuarioIdentificado, this);

        lalista = (RecyclerView) findViewById(R.id.RecyclerView2);
        LinearLayoutManager layout = new LinearLayoutManager(this);

        lalista.setLayoutManager(layout);
        lalista.setAdapter(adaptador);

        txtMensaje = (EditText) findViewById(R.id.mensajeChat);


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("juegos");



        //Cuando escribimos un mensaje y lo env??amos, se guarda dicho mensaje en la BD de Firebase
        btnEnviar = (Button) findViewById(R.id.btnEnviar);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseReference = firebaseDatabase.getReference("juegos/" + nombreSala);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child("preguntaRealizada").getValue().toString().equals("true")){

                            //Hay dos opciones:
                            // 1. Es el turno del jugador identificado en la aplicaci??n
                            if(snapshot.child("turno").getValue().toString().equals(usuarioIdentificado)){
                                Toast.makeText(Chat.this, "S??lo puedes hacer una pregunta por ronda", Toast.LENGTH_SHORT).show();
                            }

                            //2. Es el turno del otro jugador identificado
                            //El jugador debe responder a la pregunta
                            else{

                                if(snapshot.child("respuestaRealizada").getValue().toString().equals("true")){
                                    Toast.makeText(Chat.this, "S??lo puedes dar una respuesta por ronda", Toast.LENGTH_SHORT).show();
                                }else {

                                    databaseReference = firebaseDatabase.getReference("juegos/" + nombreSala + "/mensajes");

                                    Mensaje m = new Mensaje(usuarioIdentificado, txtMensaje.getText().toString());
                                    databaseReference.push().setValue(m);
                                    txtMensaje.setText("");

                                    View view = Chat.this.getCurrentFocus();
                                    if (view != null) {
                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                    }

                                    databaseReference = firebaseDatabase.getReference("juegos/" + nombreSala);
                                    databaseReference.child("respuestaRealizada").setValue("true");

                                }

                            }

                        }else{

                            if(snapshot.child("turno").getValue().toString().equals(usuarioIdentificado)) {
                                databaseReference = firebaseDatabase.getReference("juegos/" + nombreSala + "/mensajes");
                                Mensaje m = new Mensaje(usuarioIdentificado, txtMensaje.getText().toString());
                                databaseReference.push().setValue(m);

                                //Borramos el texto que acabamos de enviar
                                txtMensaje.setText("");


                                //Bajamos el teclado de la pantalla
                                View view = Chat.this.getCurrentFocus();
                                if (view != null) {
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                }

                                databaseReference = firebaseDatabase.getReference("juegos/" + nombreSala);
                                databaseReference.child("preguntaRealizada").setValue("true");
                            }

                            else{

                                Toast.makeText(Chat.this, "Le toca al otro jugador hacer la pregunta. Espera.", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



            }
        });


        //M??todo muy importante!! Cuando se env??amos o recibimos un mensaje, se baja el ReclyclerView hasta abajo para leerlo (como en Whatsapp)
        adaptador.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                lalista.scrollToPosition(adaptador.getItemCount()-1);
            }
        });



        //Cuando el otro jugador env??e un mensaje y se guarde en Firebase,
        //recoger ese mensaje y mostrarlo en el chat
        databaseReference = firebaseDatabase.getReference("juegos/" + nombreSala + "/mensajes");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //Firebase reconoce que se ha a??adido un nuevo mensaje
                Mensaje m = snapshot.getValue(Mensaje.class);
                adaptador.addMensaje(m);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull  DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        btnAtras = (Button) findViewById(R.id.btnAtras);
        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}