package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Base64;

import javax.crypto.Cipher;


public class FirmaDigitalUtil {
	
	public static PrivateKey getFirmaCertServidor() throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		// Ruta al archivo del almacén de claves
        String keystoreFile = Configuration.obtenerConfiguracion("almacenSR").toString();
        // Contraseña del almacén de claves
        char[] keystorePassword = Configuration.obtenerConfiguracion("claveAlmacenSR").toString().toCharArray();
        // Alias del certificado
        String alias = "CertificadoSR";

        // Cargar el almacén de claves
        FileInputStream fis = new FileInputStream(keystoreFile);
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(fis, keystorePassword);

        // Obtener la clave privada y el certificado
        Key key = keyStore.getKey(alias, keystorePassword);
        
		return (PrivateKey) key;
	}
	
	public static String decryptClavePubCL(String str) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, clientKey());
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(str));
        return new String(decryptedBytes);
	}
	private static PublicKey clientKey() throws Exception {
		String keystoreFile = Configuration.obtenerConfiguracion("almacenCL"), claveKeystore = Configuration.obtenerConfiguracion("claveAlmacenCL");
		FileInputStream fis = new FileInputStream(keystoreFile);
		KeyStore keystore = KeyStore.getInstance("JKS");
		keystore.load(fis, claveKeystore.toCharArray());
		Certificate cert = keystore.getCertificate("CertificadoCL");
		PublicKey publicKey = cert.getPublicKey();
		fis.close();
		return publicKey;
	}
}
