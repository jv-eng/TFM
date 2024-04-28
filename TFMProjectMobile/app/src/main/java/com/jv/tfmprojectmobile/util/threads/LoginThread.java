package com.jv.tfmprojectmobile.util.threads;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.jv.tfmprojectmobile.R;
import com.jv.tfmprojectmobile.activities.LoginActivity;
import com.jv.tfmprojectmobile.models.UserModel;
import com.jv.tfmprojectmobile.util.AuxiliarUtil;
import com.jv.tfmprojectmobile.util.ClavesUtil;
import com.jv.tfmprojectmobile.util.storage.PreferencesManage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLSocket;

public class LoginThread implements Runnable {

    private Context ctx;
    private UserModel userModel;
    private String msgRes;
    private KeyPair claves;
    private SSLSocket sock;

    public LoginThread(Context ctx, UserModel user) {this.ctx = ctx; this.userModel = user;}

    @Override
    public void run() {
        ((Activity)ctx).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((LoginActivity)ctx).prepareUIForDownload();
            }
        });

        //preparar datos
        String nombreUsuario = this.userModel.getUserName();
        String password = this.userModel.getPassword();
        String email = this.userModel.getEmail();

        //envio de datos
        try {
            sock = AuxiliarUtil.createSocket(ctx);

            //generar clave
            claves = ClavesUtil.generarClave();

            //data
            int op = 1;
            byte [] pass = password.getBytes();
            byte [] mail = email.getBytes();
            byte [] clave = ClavesUtil.claveString(claves.getPublic()).getBytes();

            //crear flujos
            DataInputStream flujo_in = new DataInputStream(sock.getInputStream());
            DataOutputStream flujo_out = new DataOutputStream(sock.getOutputStream());

            //enviar cod
            flujo_out.writeInt(op);
            //enviar correo
            flujo_out.writeInt(mail.length);
            flujo_out.write(mail);
            //enviar contraseña
            flujo_out.writeInt(pass.length);
            flujo_out.write(pass);
            //enviar clave
            flujo_out.writeInt(clave.length);
            flujo_out.write(clave);

            //ver resultado
            int res = flujo_in.readInt();

            if (res > 0) {
                //recibimos nombre
                byte [] buff = new byte[res];
                flujo_in.read(buff);
                String usuario= new String(buff, 0, res, "UTF-8");
                userModel.setUserName(usuario);
                PreferencesManage.storeUserName(ctx, usuario);
                flujo_in.readInt();

                msgRes = this.ctx.getResources().getString(R.string.login_msg_ok);
                ((Activity)ctx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((LoginActivity)ctx).checkResults(msgRes, userModel, claves);
                    }
                });
            }
            else if (res == -1) msgRes = this.ctx.getResources().getString(R.string.login_msg_1);



        } catch (IOException e) {
            Log.e("Login", "There was an error when login in an user");
        } catch (Exception e) {
            Log.e("Login", "Error al generar la clave");
        } finally {
            if (sock != null) {
                try {
                    sock.close();
                    ((Activity)ctx).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((LoginActivity)ctx).aShortToast("socket cerrado");
                        }
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        ((Activity)ctx).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((LoginActivity)ctx).prepareUIAfterDownload();
            }
        });
    }
}
