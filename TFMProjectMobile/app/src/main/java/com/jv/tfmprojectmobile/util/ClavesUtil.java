package com.jv.tfmprojectmobile.util;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.jv.tfmprojectmobile.R;

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
        keyPairGenerator.initialize(2048);
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
    private static final String TAG = "CryptoUtils";

    public static PrivateKey getCLPrivKey(Context ctx) {
        PrivateKey privateKey = null;
        try {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(ctx.getResources().openRawResource(R.raw.almacen), ctx.getResources().getString(R.string.passCA).toCharArray());
            Key key = ks.getKey(ctx.getResources().getString(R.string.aliasCL), ctx.getResources().getString(R.string.passCA).toCharArray());

            if (key instanceof PrivateKey) {
                privateKey = (PrivateKey) key;
                Log.e(TAG, "Clave privada obtenida: " + privateKey.toString());
            } else {
                Log.e(TAG, "El alias no corresponde a una clave privada");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error obteniendo la clave privada", e);
        }

        return privateKey;
    }

    public static PublicKey getCLPubKey(Context ctx) {
        PublicKey publicKey = null;
        try {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(ctx.getResources().openRawResource(R.raw.almacen), ctx.getResources().getString(R.string.passCA).toCharArray());
            Certificate cert = ks.getCertificate(ctx.getResources().getString(R.string.aliasCL));

            if (cert != null) {
                publicKey = cert.getPublicKey();
                Log.e(TAG, "Clave pública obtenida: " + publicKey.toString());
            } else {
                Log.e(TAG, "No se encontró el certificado para el alias proporcionado");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error obteniendo la clave pública", e);
        }

        return publicKey;
    }

    public static String encryptPubKey(Context ctx, String str) {
        byte[] encryptedBytes = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            PublicKey publicKey = getCLPubKey(ctx);

            if (publicKey != null) {
                cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                encryptedBytes = cipher.doFinal(str.getBytes());
                return Base64.getEncoder().encodeToString(encryptedBytes);
            } else {
                Log.e(TAG, "Clave pública es nula");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error cifrando con clave pública", e);
        }
        return null;
    }

    public static String decryptPrivKey(Context ctx, String str) {
        byte[] decryptedBytes = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            PrivateKey privateKey = getCLPrivKey(ctx);

            if (privateKey != null) {
                cipher.init(Cipher.DECRYPT_MODE, privateKey);
                byte[] encryptedBytes = Base64.getDecoder().decode(str);
                decryptedBytes = cipher.doFinal(encryptedBytes);
                return new String(decryptedBytes);
            } else {
                Log.e(TAG, "Clave privada es nula");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error descifrando con clave privada", e);
        }
        return null;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    public static PublicKey getSRPuKey(Context ctx) {
        PublicKey key = null;
        try {
            InputStream certificateStream = ctx.getResources().openRawResource(R.raw.certsr);
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
            X509Certificate cert = (X509Certificate) certificateFactory.generateCertificate(certificateStream);
            certificateStream.close();
            Log.e("aqui", cert.toString());

            key = (PublicKey) cert.getPublicKey();
        } catch (CertificateException e) {
            e.printStackTrace();
        }catch (Exception e) {
            Log.e("getSRPuKey","");
        }
        Log.e("cert",key.toString());
        return key;
    }

    public static String encryptPrivKey(Context ctx, String str, PublicKey key) {
        byte [] res = null;

        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(str.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            Log.e("encryptPubKey", "");
        }

        return null;
    }

}
