package com.example.das_quienesquien_jonalba;

public class ItemsMenuView {

    // Instanciamos las variables que vamos a usar en el ListView
    public int id;
    public int imagen;
    public String nombre;
    public String des;

    // Constructor de la clase ItemListViewPers
    public ItemsMenuView(int id, int imagen, String nombre, String des) {
        this.id = id;
        this.imagen = imagen;
        this.nombre = nombre;
        this.des = des;
    }

    // -- Getters y Setters --
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImagen() {
        return imagen;
    }

    public void setImagen(int imagen) {
        this.imagen = imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }
}
