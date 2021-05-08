package com.example.das_quienesquien_jonalba;

public class Mensaje {

    private String usuario = "";
    private String mensaje = "";

    public Mensaje(String pUsuario, String pMensaje){
        usuario = pUsuario;
        mensaje = pMensaje;
    }


    public Mensaje(){

    }

    public String getUsuario(){
        return usuario;
    }

    public String getMensaje(){
        return mensaje;
    }
}
