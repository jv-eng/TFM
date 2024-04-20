package com.jv.tfmprojectmobile.util.threads;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.jv.tfmprojectmobile.R;
import com.jv.tfmprojectmobile.activities.FicherosCanalActivity;
import com.jv.tfmprojectmobile.util.storage.PreferencesManage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class DescargarFicheroThread implements Runnable {

    private Context ctx;
    private String canal, fich, ruta;

    public DescargarFicheroThread(Context ctx, String canal, String fich) {
        this.ctx = ctx;
        this.canal = canal;
        this.fich = fich;
    }

    @Override
    public void run() {
        ((Activity)ctx).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((FicherosCanalActivity)ctx).prepareUIForDownload();
                ((FicherosCanalActivity)ctx).aShortToast("empezamos descarga");
            }
        });


        try {
            Socket sock = new Socket(this.ctx.getResources().getString(R.string.ip), this.ctx.getResources().getInteger(R.integer.puerto));

            //data
            int op = 8;
            byte [] mail = PreferencesManage.userMail(ctx).getBytes();
            byte [] channel = this.canal.getBytes();
            byte [] fichero = this.fich.getBytes();

            //crear flujos
            DataInputStream flujo_in = new DataInputStream(sock.getInputStream());
            DataOutputStream flujo_out = new DataOutputStream(sock.getOutputStream());

            //enviar cod
            flujo_out.writeInt(op);
            //enviar correo
            flujo_out.writeInt(mail.length);
            flujo_out.write(mail);
            //enviar canal
            flujo_out.writeInt(channel.length);
            flujo_out.write(channel);
            //enviar nombre del fichero
            flujo_out.writeInt(fichero.length);
            flujo_out.write(fichero);

            /*recibir mensaje*/
            //recibir longitud
            long num_recibido = flujo_in.readLong();
            Log.e("fichero","Tamaño fichero: " + num_recibido);

            //recibir mensaje
            byte [] buff;
            if (num_recibido > 64000) buff = new byte[64000];
            else buff = new byte[(int) num_recibido];

            //descargar y almacenar fichero
            File file = new File(ctx.getFilesDir(), this.fich);
            FileOutputStream fos = ctx.openFileOutput(this.fich, Context.MODE_PRIVATE);

            int bytes_leidos = 0;
            long bytes_acumulados = 0;
            do {
                bytes_leidos = flujo_in.read(buff);
                bytes_acumulados += bytes_leidos;
                fos.write(buff,0,bytes_leidos);
            } while (bytes_acumulados < num_recibido);

            fos.close();

            ruta = file.getAbsolutePath();

        } catch (IOException e) {
            Log.e("error al recibir", "error al recibir fichero");
        }

        ((Activity)ctx).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((FicherosCanalActivity)ctx).prepareUIAfterDownload(ruta);
            }
        });
    }
}
