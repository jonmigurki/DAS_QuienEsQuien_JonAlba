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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Chat extends AppCompatActivity {

    EditText txtMensaje;
    Button btnEnviar, btnAtras;

    String usuarioIdentificado, jugada;

    AdaptadorChat adaptador;

    RecyclerView lalista;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuarioIdentificado = extras.getString("usuario");
            jugada = extras.getString("jugada");
        }

        adaptador = new AdaptadorChat(this);

        lalista = (RecyclerView) findViewById(R.id.RecyclerView2);
        LinearLayoutManager layout = new LinearLayoutManager(this);

        lalista.setLayoutManager(layout);
        lalista.setAdapter(adaptador);

        txtMensaje = (EditText) findViewById(R.id.mensajeChat);


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("juegos");


        btnEnviar = (Button) findViewById(R.id.btnEnviar);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // adaptador.addMensaje(new Mensaje(usuarioIdentificado, txtMensaje.getText().toString()));

                databaseReference =  firebaseDatabase.getReference("juegos/" + jugada + "/mensajes");
                Mensaje m = new Mensaje(usuarioIdentificado, txtMensaje.getText().toString());
                databaseReference.push().setValue(m);
                txtMensaje.setText("");
                adaptador.addMensaje(m);
                View view = Chat.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

            }
        });

        adaptador.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                lalista.scrollToPosition(adaptador.getItemCount()-1);
            }
        });


        //Obtenemos los mensajes de Firebase
        databaseReference = firebaseDatabase.getReference("juegos/" + jugada + "/mensajes");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds : snapshot.getChildren()) {
                    Mensaje m = ds.getValue(Mensaje.class);
                    adaptador.addMensaje(m);
                }

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