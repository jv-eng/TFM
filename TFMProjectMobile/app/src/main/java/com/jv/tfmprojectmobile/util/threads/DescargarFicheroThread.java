package com.jv.tfmprojectmobile.util.threads;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.jv.tfmprojectmobile.R;
import com.jv.tfmprojectmobile.activities.FicherosCanalActivity;
import com.jv.tfmprojectmobile.util.AuxiliarUtil;
import com.jv.tfmprojectmobile.util.ClavesUtil;
import com.jv.tfmprojectmobile.util.storage.PreferencesManage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import javax.net.ssl.SSLSocket;

public class DescargarFicheroThread implements Runnable {

    private Context ctx;
    private String canal, fich, ruta, msg;

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
            }
        });


        try {
            Socket sock = AuxiliarUtil.createSocket(ctx);

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

            //inicializar firma
            PublicKey publicKey = ((SSLSocket) sock).getSession().getPeerCertificateChain()[0].getPublicKey();
            Signature verifier = Signature.getInstance("SHA512withRSA");
            verifier.initVerify(publicKey);

            //descargar y almacenar fichero
            File file = new File(ctx.getFilesDir(), this.fich);
            FileOutputStream fos = ctx.openFileOutput(this.fich, Context.MODE_PRIVATE);

            int bytes_leidos = 0;
            long bytes_acumulados = 0;
            do {
                bytes_leidos = flujo_in.read(buff, 0, (int)Math.min(buff.length, num_recibido - bytes_acumulados));
                bytes_acumulados += bytes_leidos;
                fos.write(buff,0,bytes_leidos);
                verifier.update(buff, 0, bytes_leidos);
            } while (bytes_acumulados < num_recibido);

            fos.close();

            // Lee la firma del flujo de entrada
            int firmaLength = flujo_in.readInt();
            byte[] firma = new byte[firmaLength];
            flujo_in.read(firma);

            // Verifica la firma
            boolean verificado = verifier.verify(firma);

            if (verificado) {
                msg = ctx.getString(R.string.ficheros_canal_msg_firma_correcta);
                //ruta del fichero
                ruta = file.getAbsolutePath();
            } else {
                File f = new File(ctx.getFilesDir(), this.fich);
                if (f.exists()) f.delete();
                msg = ctx.getString(R.string.ficheros_canal_msg_firma_incorrecta);
                Log.e("Error Signature", msg);
                ruta = null;
            }

            ((Activity)ctx).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((FicherosCanalActivity)ctx).aShortToast(msg);
                }
            });

        } catch (Exception e) {
            ((Activity)ctx).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((FicherosCanalActivity)ctx).aShortToast("Exception");
                }
            });
            throw new RuntimeException(e);
        }

        ((Activity)ctx).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((FicherosCanalActivity)ctx).prepareUIAfterDownload(ruta);
            }
        });
    }
}
