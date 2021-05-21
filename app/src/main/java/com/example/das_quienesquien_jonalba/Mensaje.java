package com.example.das_quienesquien_jonalba;

public class Mensaje {

    //Clase que crea un nuevo mensaje

    private String usuario = "";
    private String mensaje = "";

    //Constructora
    public Mensaje(String pUsuario, String pMensaje){
        usuario = pUsuario;
        mensaje = pMensaje;
    }


    public Mensaje(){

    }

    //Devuelve el usuario que ha enviado el mensaje
    public String getUsuario(){
        return usuario;
    }

    //Devuelve el texto del mensaje
    public String getMensaje(){
        return mensaje;
    }
}
