package com.jv.tfmprojectmobile.util.threads;

import android.app.Activity;
import android.content.Context;

import com.jv.tfmprojectmobile.R;
import com.jv.tfmprojectmobile.activities.FicherosCanalActivity;
import com.jv.tfmprojectmobile.activities.SuscriptionesActivity;
import com.jv.tfmprojectmobile.util.AuxiliarUtil;
import com.jv.tfmprojectmobile.util.storage.PreferencesManage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class DesuscribirThread implements Runnable {

    private Context ctx;
    private String canal;
    private int res;

    public DesuscribirThread(Context ctx, String canal) {
        this.ctx = ctx;
        this.canal = canal;
    }

    @Override
    public void run() {
        Socket sock = AuxiliarUtil.createSocket(ctx);
        try {
            DataInputStream flujo_in = new DataInputStream(sock.getInputStream());
            DataOutputStream flujo_out = new DataOutputStream(sock.getOutputStream());

            //data
            int op = 5;
            byte [] mail = PreferencesManage.userMail(ctx).getBytes();
            byte [] channel = this.canal.getBytes();

            //enviar cod
            flujo_out.writeInt(op);
            //enviar correo
            flujo_out.writeInt(mail.length);
            flujo_out.write(mail);
            //enviar canal
            flujo_out.writeInt(channel.length);
            flujo_out.write(channel);

            //ver resultado
            res = flujo_in.readInt();

            ((Activity)ctx).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((SuscriptionesActivity)ctx).operationFinnished(res, canal);
                }
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
