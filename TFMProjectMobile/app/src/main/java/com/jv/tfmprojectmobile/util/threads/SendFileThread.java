package com.jv.tfmprojectmobile.util.threads;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.jv.tfmprojectmobile.R;
import com.jv.tfmprojectmobile.activities.CreateChannelActivity;
import com.jv.tfmprojectmobile.activities.SendFileActivity;
import com.jv.tfmprojectmobile.util.AuxiliarUtil;
import com.jv.tfmprojectmobile.util.storage.PreferencesManage;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Objects;

public class SendFileThread implements Runnable {

    private Context ctx;
    private Uri selectedFileUri;
    private String channel;

    public SendFileThread(Context ctx, Uri selectedFileUri, String channel) {
        this.ctx = ctx;
        this.selectedFileUri = selectedFileUri;
        this.channel = channel;
    }

    @Override
    public void run() {

        ((Activity)ctx).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((SendFileActivity)ctx).prepareUIForDownload();
            }
        });

        Socket sock = null;
        DataInputStream flujo_in = null;
        DataOutputStream flujo_out = null;

        try {
            sock = new Socket(this.ctx.getResources().getString(R.string.ip), this.ctx.getResources().getInteger(R.integer.puerto));

            flujo_in = new DataInputStream(sock.getInputStream());
            flujo_out = new DataOutputStream(sock.getOutputStream());

            //1-codigo
            flujo_out.writeInt(6);

            //2-datos usuario
            String mailUsuario = PreferencesManage.userMail(ctx);
            byte [] mail = mailUsuario.getBytes();
            flujo_out.writeInt(mail.length);
            flujo_out.write(mail);

            //3-nombre canal
            byte [] canal = channel.getBytes();
            flujo_out.writeInt(canal.length);
            flujo_out.write(canal);

            // Obtener el nombre del archivo
            String fileName = AuxiliarUtil.getFileName(ctx, selectedFileUri);

            //4- Enviar el nombre del archivo
            byte[] fileNameBytes = fileName.getBytes();
            flujo_out.writeInt(fileNameBytes.length);
            flujo_out.write(fileNameBytes);

            // Obtener el contenido del archivo
            InputStream fileInputStream = ctx.getContentResolver().openInputStream(selectedFileUri);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);


            //File f = new File(selectedFileUri.getPath());
            long longitud_mensaje = fileInputStream.available();
            flujo_out.writeLong(longitud_mensaje);

            byte [] buffer;
            if (longitud_mensaje < 64000) buffer = new byte[(int)longitud_mensaje];
            else buffer = new byte[64000];

            //5- enviar fichero
            long num_total = 0;
            int bytes_leidos = 0;
            do {
                bytes_leidos = bufferedInputStream.read(buffer);
                num_total += bytes_leidos;
                flujo_out.write(buffer,0, bytes_leidos);
            } while (num_total < longitud_mensaje);

            // Cerrar el flujo de entrada del archivo
            bufferedInputStream.close();

            ((Activity)ctx).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((SendFileActivity)ctx).aShortToast(String.valueOf(longitud_mensaje));
                    ((SendFileActivity)ctx).prepareUIAfterDownload();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        } /*finally {
            try {
                // Cerrar los flujos y la conexión
                if (flujo_out != null) flujo_out.close();
                if (flujo_in != null) flujo_in.close();
                if (sock != null) sock.close();
            } catch (IOException e) {
                Log.e("recibir fichero","error recibiendo el fichero");
            }
        }*/
    }
}