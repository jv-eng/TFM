package util;

import java.net.Socket;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Auxiliar {
	
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
	
	public static String socketString(Socket socket) {
		try {
            // Convierte el objeto Socket a JSON
            return new ObjectMapper().writeValueAsString(socket);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
	}
	
	public static Socket stringSocket(String str) {
		try {
            // Convierte la cadena JSON a un objeto Socket
            return new ObjectMapper().readValue(str, Socket.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
	}

}
