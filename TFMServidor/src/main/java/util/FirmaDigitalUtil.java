package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import jakarta.persistence.EntityManagerFactory;

public class FirmaDigitalUtil {
	
	public static PrivateKey getFirmaCertServidor() throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		// Ruta al archivo del almacén de claves
        String keystoreFile = Configuration.obtenerConfiguracion("almacenSR");
        // Contraseña del almacén de claves
        char[] keystorePassword = Configuration.obtenerConfiguracion("claveAlmacenSR").toCharArray();
        // Alias del certificado
        String alias = "CertificadoSR";
        // Contraseña del certificado
        char[] keyPassword = Configuration.obtenerConfiguracion("claveAlmacenSR").toCharArray();

        // Cargar el almacén de claves
        FileInputStream fis = new FileInputStream(keystoreFile);
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(fis, keystorePassword);

        // Obtener la clave privada y el certificado
        Key key = keyStore.getKey(alias, keyPassword);
		return (PrivateKey) key;
	}
	
	public static String decryptClavePubCL(String str) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, getFirmaCertServidor());
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(str));
        return new String(decryptedBytes);
	}
		
}
