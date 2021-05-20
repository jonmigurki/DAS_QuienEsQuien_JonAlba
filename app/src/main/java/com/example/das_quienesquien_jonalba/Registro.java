package com.example.das_quienesquien_jonalba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity {

    String token;

    // Instanciamos los objetos para los campos y botones
    EditText editTextNombreUsuario, editTextContrasena, editTextContrasena2, editTextNombreReal, editTextApellidos;
    Button buttonRegistrarse, buttonIniciarSesion;

    // Variable con el ID del canal de notificaciones
    private static final String CHANNEL_ID = "101";

    // URL del .php para el registro en el servidor
    private String URL = "http://ec2-54-242-79-204.compute-1.amazonaws.com/aarsuaga010/WEB/QuienEsQuien/registro.php";

    private String nombreUsu, contra, contra2, nombreReal, apellidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        getToken();

        // Asignamos los id a las variables
        editTextNombreUsuario= (EditText)findViewById(R.id.editTextNombreUsuario);
        editTextContrasena= (EditText)findViewById(R.id.editTextContrasena);
        editTextContrasena2= (EditText)findViewById(R.id.editTextContrasena2);
        editTextNombreReal= (EditText)findViewById(R.id.editTextNombreReal);
        editTextApellidos= (EditText)findViewById(R.id.editTextApellidos);

        buttonRegistrarse= (Button)findViewById(R.id.buttonRegistrarse);
        buttonIniciarSesion= (Button)findViewById(R.id.buttonIniciarSesion);


        // Botón de registrarse
        buttonRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Método para guardar el usuario y la contraseña en la BD remota
                guardarUsuario(v);
            }
        });


        // Botón de inicio de sesión
        buttonIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigimos a la clase Login
                Intent intent = new Intent (v.getContext(), Login.class);
                startActivityForResult(intent, 0);
            }
        });
    }


    /**Basado en el código extraído de youtube.com
     Pregunta: https://www.youtube.com/watch?v=vXK3hsXpt2I
     Autor: El Estudio de ANDROFAST
     Modificado por Alba Arsuaga, se ha mantenido la estructura pero se
     ha modificado el contenido.
     **/
    // Método para guardar el usuario en la BD remota
    public void guardarUsuario(View view){
        // Guardamos los editText de la interfaz en variables
        nombreUsu = editTextNombreUsuario.getText().toString().trim();
        contra = editTextContrasena.getText().toString().trim();
        contra2 = editTextContrasena2.getText().toString().trim();

        nombreReal = editTextNombreReal.getText().toString().trim();
        apellidos = editTextApellidos.getText().toString().trim();

        // Si las contraseñas introducidas por el usuario son diferentes
        if(!contra.equals(contra2)){
            // Se le hace saber mediante un mensaje
            Toast.makeText(Registro.this, "Las contraseñas no coinciden.", Toast.LENGTH_LONG).show();

            // Si todos los campos están completos
        }else if(!nombreUsu.equals("") && !contra.equals("") && !contra2.equals("")){
            // Creamos la petición POST
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // Si recibimos "success" como respuesta, la petición ha funcionado correctamente y nos hemos registrado
                    if (response.equals("success")) {

                        Toast.makeText(Registro.this, "Resgistro completo.", Toast.LENGTH_LONG).show();

                    }else{
                        // Si no, ha ocurrido un error
                        Toast.makeText(Registro.this, "Ha ocurrido un error.", Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Toast.makeText(Resgistro.this, error.toString().trim(), Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    // Creamos un map con el nombre de usuario y contraseña y lo devolvemos
                    Map<String, String> data = new HashMap<>();
                    data.put("usuario", nombreUsu);
                    data.put("contrasena", contra);

                    data.put("nombre", nombreReal);
                    data.put("apellidos", apellidos);

                    // Modificar después
                    data.put("token", token);

                    return data;
                }
            };
            // Creamos la requestQueue y agregar la solicitud a la cola
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }


    // Método para la obtención del token
    public void getToken(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        token = task.getResult().getToken();
                    }
                });
    }

    // Al pulsar el botón de volver atrás, volvemos a MainActivity
    public void onBackPressed() {
        Intent i = new Intent(Registro.this, MainActivity.class);
        startActivity(i);
        finish();
    }

}