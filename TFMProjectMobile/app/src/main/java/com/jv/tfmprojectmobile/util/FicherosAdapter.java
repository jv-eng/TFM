package com.jv.tfmprojectmobile.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jv.tfmprojectmobile.R;
import com.jv.tfmprojectmobile.models.FileStoreModel;

import java.util.LinkedList;
import java.util.List;

public class FicherosAdapter extends BaseAdapter {

    private List<FileStoreModel> datos = new LinkedList<>();
    private Context ctx;

    public FicherosAdapter(Context ctx, List<FileStoreModel> data) {
        this.datos = data;
        this.ctx = ctx;
    }

    @Override
    public int getCount() {
        return datos.size();
    }

    @Override
    public Object getItem(int position) {
        return datos.get(position);
    }

    @Override
    public long getItemId(int position) {
        int hashCode = datos.get(position).getId().hashCode();
        return (long) hashCode & 0xffffffffL;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(ctx).inflate(R.layout.list_view_ficheros_layout, null);
        }

        String msg = (datos.get(position).getDescargado() == 0)?"descargar":"descargado";

        ((TextView)view.findViewById(R.id.card_name)).setText(datos.get(position).getName());
        ((TextView)view.findViewById(R.id.card_canal)).setText(datos.get(position).getCanal());
        ((TextView)view.findViewById(R.id.card_status)).setText(msg);
        return view;
    }
}
