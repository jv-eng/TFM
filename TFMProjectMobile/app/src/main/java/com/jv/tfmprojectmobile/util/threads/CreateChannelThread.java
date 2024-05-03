package com.jv.tfmprojectmobile.util.threads;

import android.app.Activity;
import android.content.Context;

import com.jv.tfmprojectmobile.R;
import com.jv.tfmprojectmobile.activities.CreateChannelActivity;
import com.jv.tfmprojectmobile.util.AuxiliarUtil;
import com.jv.tfmprojectmobile.util.storage.PreferencesManage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class CreateChannelThread implements Runnable {

    private Context ctx;
    private String channel;
    private String msgRes;

    public CreateChannelThread(Context ctx, String channel) {
        this.ctx = ctx;
        this.channel = channel;
    }

    @Override
    public void run() {
        ((Activity)ctx).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((CreateChannelActivity)ctx).prepareUIForDownload();
            }
        });

        try {
            Socket sock = AuxiliarUtil.createSocket(ctx);

            //data
            int op = 3;
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
            if (res == 0) msgRes = this.ctx.getResources().getString(R.string.create_channel_msg_channel_created);
            else if (res == 1) msgRes = this.ctx.getResources().getString(R.string.login_msg_1);

            ((Activity)ctx).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((CreateChannelActivity)ctx).prepareUIAfterDownload();
                    ((CreateChannelActivity)ctx).aShortToast(msgRes);
                }
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
