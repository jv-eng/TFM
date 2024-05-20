package com.jv.tfmprojectmobile.util.threads;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.jv.tfmprojectmobile.R;
import com.jv.tfmprojectmobile.activities.FicherosCanalActivity;
import com.jv.tfmprojectmobile.activities.SuscriptionesActivity;
import com.jv.tfmprojectmobile.models.FileStoreModel;
import com.jv.tfmprojectmobile.util.AuxiliarUtil;
import com.jv.tfmprojectmobile.util.storage.FileStoreDB;
import com.jv.tfmprojectmobile.util.storage.FileStoreHelper;
import com.jv.tfmprojectmobile.util.storage.PreferencesManage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class SuscribirThread implements Runnable {

    private Context ctx;
    private String channel;
    private String msgRes;
    private Socket sock;
    private int timeout = 60000; //milisegundos, 1h
    private boolean isRunning = true;

    public SuscribirThread(Context ctx, String channel) {
        this.ctx = ctx;
        this.channel = channel;
    }

    @Override
    public void run() {
        //crear flujos
        try {
            sock = AuxiliarUtil.createSocket(ctx);
            DataInputStream flujo_in = new DataInputStream(sock.getInputStream());
            DataOutputStream flujo_out = new DataOutputStream(sock.getOutputStream());

            //eliminar ficheros ya descargados
            FileStoreHelper helper = new FileStoreHelper(ctx);
            FileStoreDB fileStoreDB = new FileStoreDB(helper);
            fileStoreDB.eliminarPorNombre(channel);

            if (suscribirCanal(flujo_in, flujo_out)) {
                //temporizador para parar el thread
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        stopListening();
                    }
                }, timeout);

                //esperar por ficheros
                esperarFicheros(flujo_in);

                timer.cancel();
            } else {
                ((Activity)ctx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((FicherosCanalActivity)ctx).aShortToast(ctx.getString(R.string.discover_channel_msg_error_subscribe));
                    }
                });
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean suscribirCanal(DataInputStream flujo_in, DataOutputStream flujo_out) {
        try {

            //data
            int op = 4;
            byte [] mail = PreferencesManage.userMail(ctx).getBytes();
            byte [] channel = this.channel.getBytes();

            //enviar cod
            flujo_out.writeInt(op);
            //enviar correo
            flujo_out.writeInt(mail.length);
            flujo_out.write(mail);
            //enviar canal
            flujo_out.writeInt(channel.length);
            flujo_out.write(channel);

            //ver resultado
            int res = flujo_in.readInt();
            if (res > 0) {
                //registrar en base de datos local
                FileStoreHelper helper = new FileStoreHelper(ctx);
                FileStoreDB fileStoreDB = new FileStoreDB(helper);
                fileStoreDB.insertChannel(this.channel);

                recibirFicherosEnviados(res, flujo_in); //recibir ficheros ya enviados a ese canal
                msgRes = ctx.getString(R.string.discover_channel_msg_subscription_success);
            }
            else if (res == -1) msgRes = this.ctx.getResources().getString(R.string.login_msg_1);

            //resultado operacion
            flujo_in.readInt();
            ((Activity)ctx).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((FicherosCanalActivity)ctx).aShortToast(msgRes);
                }
            });

        } catch (IOException e) {
            Log.e("suscribir", "error");
        }
        return true;
    }

    private void recibirFicherosEnviados(int tam, DataInputStream flujo_in) {
        for (int i = 0; i < tam; i++) {
            descargarFichero(flujo_in);
        }
    }

    private void descargarFichero(DataInputStream flujo_e) {
        try {
            //esperar nombre fich, almacenar en base de datos
            int tam = flujo_e.readInt();
            byte[] buff = new byte[tam];
            flujo_e.read(buff);
            String fich = new String(buff, 0, tam, "UTF-8");

            //guardar fichero
            FileStoreHelper helper = new FileStoreHelper(ctx);
            FileStoreDB fileStoreDB = new FileStoreDB(helper);
            if (!fileStoreDB.checkFileExists(fich)) { //fichero no existe, guardamos
                FileStoreModel model = new FileStoreModel(
                        AuxiliarUtil.generateUUID(), fich, 0, "", channel
                );

                fileStoreDB.save(model);
            }
        } catch (IOException e) {
            Log.e("suscribir", "error");
        }
    }

    private void esperarFicheros(DataInputStream flujo_e) {
        ((Activity)ctx).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((FicherosCanalActivity)ctx).aShortToast(ctx.getString(R.string.discover_channel_msg_waiting) + channel);
            }
        });

        //esperar datos
        while (isRunning) {
           //descargar fichero
            descargarFichero(flujo_e);

            //lanzar notificacion
        }

    }

    private void stopListening() {
        isRunning = false;
        try {
            if (sock != null && !sock.isClosed()) {
                sock.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
