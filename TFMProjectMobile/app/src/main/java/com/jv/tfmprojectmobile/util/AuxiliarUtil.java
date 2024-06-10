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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.UUID;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
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

    private static SSLSocketFactory sslSF = null;
    private static boolean certDownloaded = false;
    private static void guardarCertServer(Context ctx, SSLSession sslSession, String fileName) throws SSLPeerUnverifiedException {
        X509Certificate serverCert = (X509Certificate) sslSession.getPeerCertificates()[0];
        File file = new File(ctx.getExternalFilesDir(null), fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            byte[] certificateData = serverCert.getEncoded();
            fos.write(certificateData);
            fos.flush();
        } catch (Exception e) {
            Log.e("guardar cert", "error");
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static SSLSocket createSocket(Context ctx) {
        SSLSocket socket = null;
        try {
            if (sslSF == null) sslSF = createSocketContext(ctx);
            Socket sock = new Socket(ctx.getResources().getString(R.string.ip), ctx.getResources().getInteger(R.integer.puerto));
            socket = (SSLSocket) sslSF.createSocket(sock, null, sock.getPort(), false);
            socket.setUseClientMode(true);
            socket.setEnabledProtocols(new String[]{"TLSv1.3"});
            if (!certDownloaded) {
                guardarCertServer(ctx, socket.getSession(), ctx.getResources().getString(R.string.certSRName));
                certDownloaded = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return socket;
    }
    private static SSLSocketFactory createSocketContext(Context ctx) throws Exception {
        SSLSocket socket = null;
        String pass = ctx.getResources().getString(R.string.passCA);
        char [] fraseclave = pass.toCharArray();

        //mirar el codigo del visual studio
        SSLContext sslContext;
        KeyManagerFactory kmf;
        KeyStore ks, keyStore;

        sslContext = SSLContext.getInstance("TLSv1.3");

        //cargar el cer
        keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

        InputStream certificateStream = ctx.getResources().openRawResource(R.raw.ca); //el pem o cer

        CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
        Certificate chain = certificateFactory.generateCertificate(certificateStream);
        certificateStream.close();

        keyStore.load(null, null);
        keyStore.setEntry(ctx.getResources().getString(R.string.aliasCL), new KeyStore.TrustedCertificateEntry(chain), null);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);

        //ahora el p12
        ks = KeyStore.getInstance("PKCS12");
        ks.load(ctx.getResources().openRawResource(R.raw.almacen), fraseclave);

        kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, fraseclave);

        //contexto
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);


        return sslContext.getSocketFactory();
    }
}
