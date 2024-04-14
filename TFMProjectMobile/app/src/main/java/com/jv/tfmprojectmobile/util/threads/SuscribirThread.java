package com.jv.tfmprojectmobile.util.threads;

import android.app.Activity;
import android.content.Context;

import com.jv.tfmprojectmobile.R;
import com.jv.tfmprojectmobile.activities.DescubrirCanalesActivity;
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
        if (suscribirCanal()) {
            //temporizador para parar el thread
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    stopListening();
                }
            }, timeout);

            //esperar por ficheros
            esperarFicheros();

            timer.cancel();
        } else {
            ((Activity)ctx).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((DescubrirCanalesActivity)ctx).aShortToast("Error al suscribir");
                }
            });
        }
    }

    private boolean suscribirCanal() {
        try {
            sock = new Socket(this.ctx.getResources().getString(R.string.ip), this.ctx.getResources().getInteger(R.integer.puerto));

            //data
            int op = 4;
            byte [] mail = PreferencesManage.userMail(ctx).getBytes();
            byte [] channel = this.channel.getBytes();

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

            //ver resultado
            int res = flujo_in.readInt();
            if (res == 0) msgRes = "canal suscrito correctamente";
            else if (res == 1) msgRes = this.ctx.getResources().getString(R.string.login_msg_1);

            ((Activity)ctx).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((DescubrirCanalesActivity)ctx).aShortToast(msgRes);
                }
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    private void esperarFicheros() {
        ((Activity)ctx).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((DescubrirCanalesActivity)ctx).aShortToast("esperando para el canal " + channel);
            }
        });

        //esperamos conexiones
        try {
            //leer datos
            DataInputStream flujo_e = new DataInputStream(sock.getInputStream());

            //esperar datos
            while (isRunning) {
                //esperar msg, almacenar en base de datos
                int tam = flujo_e.readInt();
                byte [] buff = new byte[tam];
                flujo_e.read(buff);
                String canal = new String(buff, 0, tam, "UTF-8");

                //nombre del fichero
                tam = flujo_e.readInt();
                buff = new byte[tam];
                flujo_e.read(buff);
                String fich = new String(buff, 0, tam, "UTF-8");

                //guardar fichero
                FileStoreHelper helper = new FileStoreHelper(ctx);
                FileStoreDB fileStoreDB = new FileStoreDB(helper);
                FileStoreModel model = new FileStoreModel(
                        AuxiliarUtil.generateUUID(), fich, 0, "", canal
                );
                fileStoreDB.save(model);
            }

            flujo_e.close();
            sock.close();
            
        } catch (IOException e) {
            throw new RuntimeException(e);
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
