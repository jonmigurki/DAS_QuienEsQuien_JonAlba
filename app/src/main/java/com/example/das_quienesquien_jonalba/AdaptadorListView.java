package com.example.das_quienesquien_jonalba;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AdaptadorListView extends BaseAdapter {
    // Para la ListView personalizada se necesita una clase que extienda de BaseAdapter

    private Context contexto;
    private LayoutInflater inflater;

    //Creamos la lista de ItemListViewPers para la ListView personalizada
    List<ItemsMenuView> listaPers;

    // Constructor de la clase AdaptadorListView
    public AdaptadorListView(Context contexto, List<ItemsMenuView> listaPers) {
        this.contexto = contexto;
        this.listaPers = listaPers;
        inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        // El número de elementos de la lista
        return listaPers.size();
    }

    @Override
    public Object getItem(int i) {
        // La posición
        return i;
    }

    @Override
    public long getItemId(int i) {
        // El identificador del elemento i
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView ImageViewItem;
        TextView TextViewNombre;
        TextView TextViewDes;

        // Obtener de la lista 1 entidad, para llenar los datos en el ListView
        ItemsMenuView item = listaPers.get(i);

        if(view==null){
            // Se indica el xml con el layout para cada elemento
            view = inflater.inflate(R.layout.menuview, null);
        }

        // Asignamos los id a las variables
        ImageViewItem = view.findViewById(R.id.imageViewContacto);
        TextViewNombre = view.findViewById(R.id.textViewNombre);
        TextViewDes = view.findViewById(R.id.textViewDes);

        // Añadimos los datos a los elementos
        ImageViewItem.setImageResource(item.imagen);
        TextViewNombre.setText(item.nombre);
        TextViewDes.setText(item.des);

        // Devolvemos el view
        return view;
    }
}

