package com.jv.tfmprojectmobile.util;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.OpenableColumns;
import android.util.Log;

import com.jv.tfmprojectmobile.R;
import com.jv.tfmprojectmobile.activities.LoginActivity;

import java.io.InputStream;
import java.net.Socket;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.UUID;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class AuxiliarUtil {
    public static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri != null) {
            if ("content".equals(uri.getScheme())) {
                if (DocumentsContract.isDocumentUri(context, uri)) {
                    // Si la URI es de tipo "content" y es un documento de medios, intentamos obtener el nombre del archivo del proveedor de contenido.
                    try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                        if (cursor != null && cursor.moveToFirst()) {
                            // Verificar si la columna DISPLAY_NAME existe en el cursor
                            int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                            if (displayNameIndex != -1) {
                                result = cursor.getString(displayNameIndex);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // Si la URI es de tipo "content" pero no es un documento de medios, intentamos obtener el nombre del archivo de la URI.
                    result = uri.getLastPathSegment();
                }
            } else {
                // Si la URI no es de tipo "content", intentamos obtener el nombre del archivo de la URI.
                result = uri.getLastPathSegment();
            }
        }
        return result;
    }






    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public static String dateString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    public static Date stringDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date res = null;
        try {
            res = sdf.parse(date);
        } catch (Exception e) {
            Log.e("parse", "error al parsear fecha");
        }
        return res;
    }

    /*public static Socket createSocket(Context ctx) throws IOException {
        return new Socket(ctx.getResources().getString(R.string.ip), ctx.getResources().getInteger(R.integer.puerto));
    }*/
    private static SSLSocketFactory sslSF = null;
    public static SSLSocket createSocket(Context ctx) {
        SSLSocket socket = null;
        try {
            if (sslSF == null) sslSF = createSocketContext(ctx);
            /*Socket sock = new Socket(ctx.getResources().getString(R.string.ip), ctx.getResources().getInteger(R.integer.puerto));
            socket = (SSLSocket) sslSF.createSocket(sock, null, sock.getPort(), false);
            socket.setUseClientMode(true);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return socket;
    }
    private static SSLSocketFactory createSocketContext(Context ctx) throws Exception {
        SSLSocket socket = null;
        String pass = ctx.getResources().getString(R.string.passCA);
        Log.e("pass", pass);
        char [] fraseclave = pass.toCharArray();

        //mirar el codigo del visual studio
        SSLContext sslContext;
        KeyManagerFactory kmf;
        KeyStore ks, keyStore;

        sslContext = SSLContext.getInstance("TLS");

        //cargar el cer
        keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

        InputStream certificateStream = ctx.getResources().openRawResource(R.raw.ca); //el pem o cer

        CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
        java.security.cert.Certificate chain;
        chain = certificateFactory.generateCertificate(certificateStream);
        certificateStream.close();
Log.e("cer",chain.toString());
        keyStore.load(null, null);
        keyStore.setEntry("cliente", new KeyStore.TrustedCertificateEntry(chain), null);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);

        //ahora el p12
        ks = KeyStore.getInstance("PKCS12");
        ks.load(ctx.getResources().openRawResource(R.raw.almacen), fraseclave);

        Enumeration<String> enumeration = ks.aliases();
        X509Certificate certificate = null;
        while (enumeration.hasMoreElements()) {
            System.out.println("\n\n");
            String alias = (String) enumeration.nextElement();
            System.out.println("alias name " + ":  " + alias);


            certificate = (X509Certificate) ks.getCertificate(alias);
            System.out.println(certificate.toString());
            System.out.println("\n\n");
        }

        //Key key = ks.getKey("cliente", fraseclave); //tenemos que meter la clave de la clave privada
        //PrivateKey privateKey = (PrivateKey) key;

        kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, fraseclave);

        //contexto y socket
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        return sslContext.getSocketFactory();
    }

    public static PrivateKey getCLPrivKey() {
        return null;
    }
    public static PublicKey getCLPuKey() {
        return null;
    }
    public static PublicKey getSRPuKey() {
        return null;
    }
}
