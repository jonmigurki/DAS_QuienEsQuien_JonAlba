package com.example.das_quienesquien_jonalba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Resolver extends AppCompatActivity {

    String usuarioIdentificado, jugada;
    String[] nombrespersonajes;
    int[] rutapersonajes;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resolver);



        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuarioIdentificado = extras.getString("usuario");
            jugada = extras.getString("jugada");
            nombrespersonajes = extras.getStringArray("nombrespersonajes");
            rutapersonajes = extras.getIntArray("rutapersonajes");
        }


        //Volvemos a crear el tablero con los personajes
        RecyclerView lalista = (RecyclerView) findViewById(R.id.RecyclerView3);

        ElAdaptadorRecycler eladaptador = new ElAdaptadorRecycler(nombrespersonajes, rutapersonajes, this);
        lalista.setAdapter(eladaptador);

        GridLayoutManager elLayoutRejillaIgual = new GridLayoutManager(this, 5, GridLayoutManager.VERTICAL, false);
        lalista.setLayoutManager(elLayoutRejillaIgual);


    }


    public void comprobarPersonaje(String personaje){


        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference("juegos/" + jugada);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                databaseReference.child("comprueba").setValue(usuarioIdentificado);

                if(snapshot.child("jugador1").child("usuario").getValue().equals(usuarioIdentificado)){

                    String p = snapshot.child("jugador2").child("personaje").getValue().toString();
                    String j2 = snapshot.child("jugador2").child("usuario").getValue().toString();

                    if(personaje.equals(p)){

                        Toast.makeText(Resolver.this, "HAS GANADO", Toast.LENGTH_SHORT).show();     //GANA EL JUGADOR 1

                        databaseReference.child("ganador").setValue(usuarioIdentificado);

                        mostrarAnimacion("ganado");

                    }else{

                        Toast.makeText(Resolver.this, "HAS PERDIDO", Toast.LENGTH_SHORT).show();    //PIERDE EL JUGADOR 1

                        databaseReference.child("ganador").setValue(j2);

                        mostrarAnimacion("perdido");

                    }

                } else if(snapshot.child("jugador2").child("usuario").getValue().equals(usuarioIdentificado)){

                    String p = snapshot.child("jugador1").child("personaje").getValue().toString();
                    String j1 = snapshot.child("jugador1").child("usuario").getValue().toString();


                    if(personaje.equals(p)){

                        Toast.makeText(Resolver.this, "HAS GANADO", Toast.LENGTH_SHORT).show();     //GANA EL JUGADOR 2

                        databaseReference.child("ganador").setValue(usuarioIdentificado);

                        mostrarAnimacion("ganado");

                    }else{

                        Toast.makeText(Resolver.this, "HAS PERDIDO", Toast.LENGTH_SHORT).show();       //PIERDE EL JUGADOR 2

                        databaseReference.child("ganador").setValue(j1);

                        mostrarAnimacion("perdido");

                    }

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void mostrarAnimacion(String resultado){

        if(resultado.equals("ganado")){

            ImageView image = new ImageView(Resolver.this);
            String i = "ganado";
            int im = getResources().getIdentifier(i, "drawable", this.getPackageName());
            image.setImageResource(im);


            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("¡¡HAS GANADO!!");
            adb.setView(image);
            adb.setPositiveButton("Compartir", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {


                    Intent i = new Intent(Resolver.this, MenuPrincipal.class);
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

                    Intent i = new Intent(Resolver.this, MenuPrincipal.class);
                    i.putExtra("usuario", usuarioIdentificado);
                    startActivity(i);
                    finish();

                }
            });

            adb.show();




        } else {


            ImageView image = new ImageView(Resolver.this);
            String i = "perdido";
            int im = getResources().getIdentifier(i, "drawable", this.getPackageName());
            image.setImageResource(im);


            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("¡¡HAS PERDIDO!!");
            adb.setView(image);
            adb.setPositiveButton("Compartir", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {


                    Intent i = new Intent(Resolver.this, MenuPrincipal.class);
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

                    Intent i = new Intent(Resolver.this, MenuPrincipal.class);
                    i.putExtra("usuario", usuarioIdentificado);
                    startActivity(i);
                    finish();

                }
            });

            adb.show();




        }
    }
}