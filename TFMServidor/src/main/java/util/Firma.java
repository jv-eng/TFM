package util;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Firma {
	
	public static String claveString(PublicKey key) {
		byte[] keyBytes = key.getEncoded();
        return Base64.getEncoder().encodeToString(keyBytes);
	}
	
	public static PublicKey stringClave(String str) {
		byte[] keyBytes = Base64.getDecoder().decode(str);

		try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
        } catch (Exception e) {
            e.printStackTrace(); 
            return null;
        }
	}

}
