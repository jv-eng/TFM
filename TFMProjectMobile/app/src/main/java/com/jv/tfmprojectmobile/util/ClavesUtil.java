package com.jv.tfmprojectmobile.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.jv.tfmprojectmobile.R;
import com.jv.tfmprojectmobile.activities.LoginActivity;

import java.io.InputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

public class ClavesUtil {

    /*Generacion de claves*/
    public static KeyPair generarClave() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048); // Tamaño de clave, ajusta según tus requisitos
        return keyPairGenerator.generateKeyPair();
    }


    /*Serializacion de claves*/
    public static String claveString(PublicKey key) {
        byte[] keyBytes = key.getEncoded();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(keyBytes);
        }
        return null;
    }

    public static String claveString(PrivateKey key) {
        byte[] keyBytes = key.getEncoded();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(keyBytes);
        }
        return null;
    }

    public static PublicKey stringClave(String str) {
        byte[] keyBytes = new byte[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            keyBytes = Base64.getDecoder().decode(str);
        }

        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static PrivateKey stringClavePriv(String str) {
        byte[] keyBytes = new byte[0];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            keyBytes = Base64.getDecoder().decode(str);
        }
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    /*Manejo de claves*/
    private static PrivateKey getCLPrivKey(Context ctx) {
        PrivateKey privateKey = null;
        try {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(ctx.getResources().openRawResource(R.raw.almacen), ctx.getResources().getString(R.string.passCA).toCharArray());
            Key key = ks.getKey(ctx.getResources().getString(R.string.aliasCL), ctx.getResources().getString(R.string.passCA).toCharArray());

            privateKey = (PrivateKey) key;
        } catch (Exception e) {
            Log.e("getCLPrivKey","");
        }

        return privateKey;
    }
    public static String encryptPubKey(Context ctx, String str) {
        byte[] decryptedBytes = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, getCLPrivKey(ctx));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(str));
            }
        } catch (Exception e) {
            Log.e("encryptPubKey","");
        }
        return new String(decryptedBytes);
    }

    private static PublicKey getCLPubKey(Context ctx) {
        PublicKey publicKey = null;
        try {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(ctx.getResources().openRawResource(R.raw.almacen), ctx.getResources().getString(R.string.passCA).toCharArray());
            Certificate cert = ks.getCertificate(ctx.getResources().getString(R.string.aliasCL));
            publicKey = cert.getPublicKey();

        } catch (Exception e) {
            Log.e("getCLPuKey","");
        }

        return publicKey;
    }
    public static String decryptPubKey(Context ctx, String str) {
        byte[] encryptedBytes = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, getCLPubKey(ctx));
            encryptedBytes = cipher.doFinal(str.getBytes());
        } catch (Exception e) {
            Log.e("decryptPubKey","");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } else return null;
    }

    public static PublicKey getSRPuKey(Context ctx) {
        PublicKey key = null;
        try {
            InputStream certificateStream = ctx.getResources().openRawResource(R.raw.certsr);
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
            X509Certificate cert = (X509Certificate) certificateFactory.generateCertificate(certificateStream);
            certificateStream.close();

            key = (PublicKey) cert.getPublicKey();
        } catch (CertificateException e) {
            e.printStackTrace();
        }catch (Exception e) {
            Log.e("getSRPuKey","");
        }
        Log.e("cert",key.toString());
        return key;
    }

    //cifrar el string de la clave publica
    public static byte [] encryptPubKey(Context ctx, PublicKey key) {
        byte [] res = null;
        PublicKey srKey = getSRPuKey(ctx);
        Log.e("aqui", srKey.toString());
        String str = claveString(key);

        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, srKey);
            byte[] encryptedBytes = cipher.doFinal(str.getBytes());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                res = Base64.getEncoder().encodeToString(encryptedBytes).getBytes();
            }

        } catch (Exception e) {
            Log.e("encryptPubKey", "");
        }
Log.e("aqui","clave cifrada");
        return res;
    }

}
