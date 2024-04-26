package com.jv.tfmprojectmobile.util.threads;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.jv.tfmprojectmobile.R;
import com.jv.tfmprojectmobile.activities.RegisterActivity;
import com.jv.tfmprojectmobile.models.UserModel;
import com.jv.tfmprojectmobile.util.AuxiliarUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class RegisterThread implements Runnable {

    private Context ctx;
    private UserModel userModel;
    private String msgRes = "";


    public RegisterThread(Context ctx, UserModel user) {
        this.ctx = ctx;
        this.userModel = user;
    }

    @Override
    public void run() {

        ((Activity)ctx).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((RegisterActivity)ctx).prepareUIForDownload();
            }
        });

        //preparar datos
        String nombreUsuario = this.userModel.getUserName();
        String password = this.userModel.getPassword();
        String email = this.userModel.getEmail();

        //envio de datos
        try {
            Socket sock = AuxiliarUtil.createSocket(ctx);

            //data
            int op = 0;
            byte [] nombre = nombreUsuario.getBytes();
            byte [] pass = password.getBytes();
            byte [] mail = email.getBytes();

            //crear flujos
            DataInputStream flujo_in = new DataInputStream(sock.getInputStream());
            DataOutputStream flujo_out = new DataOutputStream(sock.getOutputStream());

            //enviar cod
            flujo_out.writeInt(op);
            //enviar nombre
            flujo_out.writeInt(nombre.length);
            flujo_out.write(nombre);
            //enviar contraseña
            flujo_out.writeInt(pass.length);
            flujo_out.write(pass);
            //enviar correo
            flujo_out.writeInt(mail.length);
            flujo_out.write(mail);

            //ver resultado
            int res = flujo_in.readInt();

            if (res == 0) msgRes = this.ctx.getResources().getString(R.string.register_msg_ok);
            else if (res == 1) msgRes = this.ctx.getResources().getString(R.string.register_msg_1);
            else msgRes = this.ctx.getResources().getString(R.string.register_msg_2);


        } catch (IOException e) {
            Log.e("Register", "There was an error when creating an user");
        }

        ((Activity)ctx).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((RegisterActivity)ctx).prepareUIAfterDownload(msgRes);
            }
        });
    }
}
