package usuario;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Base64;

public class InicioSesion {
	
	public static void iniciarSesion(String mail, String password) {
		//Registro.crearUsuario("miUsuario_123", "MiContrasena123", "pepe@mail.com");
		
		String server_ip = "localhost";
		String server_port = "12345";

		try {
			//crear socket
			Socket sock = new Socket(server_ip, Integer.parseInt(server_port));
			
			//streams
			OutputStream output_stream = sock.getOutputStream();
			InputStream input_stream = sock.getInputStream();
			//flujos para comunicar
			DataInputStream flujo_in = new DataInputStream(input_stream);
			DataOutputStream flujo_out = new DataOutputStream(output_stream);
			
			//generar claves
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
	        keyPairGenerator.initialize(2048); // Tama�o de clave, ajusta seg�n tus requisitos
	        KeyPair claves = keyPairGenerator.generateKeyPair();
	        //System.out.println("clave p�blica: " + claves.getPublic());
	        //System.out.println("clave privada: " + claves.getPrivate());
			
			//datos
			int op = 1;
			byte [] nombre = mail.getBytes();
			byte [] pass = password.getBytes();
			byte [] clave = claveString(claves.getPublic()).getBytes();
			
			//enviar
			//enviar cod
			flujo_out.writeInt(op);
			//enviar nombre
			flujo_out.writeInt(nombre.length);
			flujo_out.write(nombre);
			//enviar contrase�a
			flujo_out.writeInt(pass.length);
			flujo_out.write(pass);
			//enviar correo
			flujo_out.writeInt(clave.length);
			flujo_out.write(clave);
			//recibir y procesar respuesta
			int res = flujo_in.readInt();
			System.out.println("Resultado de la operaci�n iniciar sesi�n: " + res);
			
			
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public static String claveString(PublicKey key) {
		byte[] keyBytes = key.getEncoded();
        return Base64.getEncoder().encodeToString(keyBytes);
	}
	
	public static void main(String [] args) {
		iniciarSesion("miUsuario_123", "MiContrasena123");
	}
}
