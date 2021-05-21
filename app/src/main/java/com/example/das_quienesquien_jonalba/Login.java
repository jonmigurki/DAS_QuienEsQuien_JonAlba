package com.example.das_quienesquien_jonalba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Login extends AppCompatActivity {

    //Instancias de los elementos del layout
    EditText usuario, contrasena;
    Button identificarse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Inicializamos los elementos del layout
        usuario = (EditText) findViewById(R.id.txtUsuario);
        contrasena = (EditText) findViewById(R.id.txtContrasena);
        identificarse = (Button) findViewById(R.id.btnIdentificarse);

        //Cuando pulsamos el botón "Identificarse"
        identificarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://ec2-54-242-79-204.compute-1.amazonaws.com/aarsuaga010/WEB/QuienEsQuien/identificar.php?usuario="
                        + usuario.getText().toString() + "&contrasena=" + contrasena.getText().toString();

                new Conexion().execute(url);

            }
        });

    }


    //Clase que se encarga de realizar la conexión de manera asíncrona
    public class Conexion extends AsyncTask<String, String, String> {

        ProgressDialog pd = new ProgressDialog(Login.this);
        HttpURLConnection conn;
        URL url = null;

        //Método que se ejecuta mientras está realizándose la conexión
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Cargando...");
            pd.setCancelable(false);
            pd.show();
        }


        //Conexión a la BD
        @Override
        protected String doInBackground(String... params) {

            try {
                url = new URL(params[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            Log.d("URL", url.toString());

            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(10000);

                conn.setRequestMethod("GET");

                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.connect();


            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Error", e.toString());
                return "exception 2";
            }

            try {
                int response_code = conn.getResponseCode();

                if (response_code == HttpURLConnection.HTTP_OK) {

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String message = in.readLine();
                    in.close();

                    return message;


                } else {
                    return ("unsuccessfull");
                }


            } catch (IOException e) {
                e.printStackTrace();
                return "exception 1";
            } finally {
                conn.disconnect();
            }
        }


        //Una vez finalizada la conexión, recogemos el resultado y permitimos la entrada del usuario o no
        protected void onPostExecute(String result) {

            pd.dismiss();

            Log.d("Resultado", result.toString());

            if(result.equals("OK")){
                //Si el resultado es "OK", el usuario y contraseña son correctos y permitimos la entrada
                Intent i = new Intent(Login.this, MenuPrincipal.class);
                i.putExtra("usuario", usuario.getText().toString());
                startActivity(i);
                finish();
            }else if(result.equals("Error")){
                //Si el resultado es "Error", el usuario o contraseña son incorrectos
                Toast.makeText(Login.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }else{
                //Si el resultado es otro, ha habido un problema con la conexión
                Toast.makeText(Login.this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
            }

        }
    }


    // Al pulsar el botón de volver atrás, volvemos donde el usuario elija
    public void onBackPressed() {

        String[] opciones = {"Pantalla Principal", "Pantalla de Registro"};

        AlertDialog.Builder alert = new AlertDialog.Builder(Login.this);
        alert
                .setTitle("¿A qué pantalla deseas ir?")
                .setSingleChoiceItems(opciones, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        // Obtenemos la categoría seleccionada por el usuario
                        String c = opciones[i];
                        switch(i){
                            case 0:
                                Intent intent1 = new Intent(Login.this, MainActivity.class);
                                startActivity(intent1);
                                finish();
                                break;
                            case 1:
                                Intent intent2 = new Intent(Login.this, Registro.class);
                                startActivity(intent2);
                                finish();
                                break;
                        }
                        // Cuando ya se ha selecionado
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDia = alert.create();
        // Mostrar el alert
        alertDia.show();
    }

}