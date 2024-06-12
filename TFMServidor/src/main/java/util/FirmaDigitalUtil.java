package util;

import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.Base64;

import javax.crypto.Cipher;


public class FirmaDigitalUtil {
	
	public static PrivateKey getFirmaCertServidor() {
		// Ruta al archivo del almacén de claves
        String keystoreFile = Configuration.obtenerConfiguracion("almacenSR").toString();
        // Contraseña del almacén de claves
        char[] keystorePassword = Configuration.obtenerConfiguracion("claveAlmacenSR").toString().toCharArray();
        // Alias del certificado
        String alias = "CertificadoSR";

        
        Key key = null;
		try {
			// Cargar el almacén de claves
	        FileInputStream fis = new FileInputStream(keystoreFile);
	        KeyStore keyStore = KeyStore.getInstance("JKS");
	        keyStore.load(fis, keystorePassword);

	        // Obtener la clave privada y el certificado
			key = keyStore.getKey(alias, keystorePassword);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        
		return (PrivateKey) key;
	}
	
	public static String decryptClavePubCL(String str) {
		
        byte[] decryptedBytes = null;
		try {
			Cipher cipher = Cipher.getInstance("RSA");
	        cipher.init(Cipher.DECRYPT_MODE, getFirmaCertServidor());
	        byte[] encryptedBytes = Base64.getDecoder().decode(str.getBytes());
            decryptedBytes = cipher.doFinal(encryptedBytes);
			return new String(decryptedBytes);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        return null;
	}
}
