package com.example.das_quienesquien_jonalba;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Login extends AppCompatActivity {

    EditText usuario, contrasena;
    Button identificarse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usuario = (EditText) findViewById(R.id.txtUsuario);
        contrasena = (EditText) findViewById(R.id.txtContrasena);
        identificarse = (Button) findViewById(R.id.btnIdentificarse);


        identificarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://ec2-54-167-31-169.compute-1.amazonaws.com/aarsuaga010/WEB/QuienEsQuien/identificar.php?usuario="
                        + usuario.getText().toString() + "&contrasena=" + contrasena.getText().toString();

                new Conexion().execute(url);

            }
        });

    }


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


        //Una vez finalizada la conexión, recogemos el resultado y hacemos las llamadas
        //a las actividades desde donde provenía la llamada para finalizar con las ejecuciones
        protected void onPostExecute(String result) {

            pd.dismiss();

            Log.d("Resultado", result.toString());

            if(result.equals("OK")){
                Intent i = new Intent(Login.this, MenuPrincipal.class);
                i.putExtra("usuario", usuario.getText().toString());
                startActivity(i);
                finish();
            }else if(result.equals("Error")){
                Toast.makeText(Login.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(Login.this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
            }

        }
    }
}