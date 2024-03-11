package canal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import usuario.InicioSesion;
import usuario.Registro;

public class Canal {
	public static void main(String [] args) {
		Registro.crearUsuario("miUsuario_123", "MiContrasena123", "miUsuario_123@mail.com");
		InicioSesion.iniciarSesion("miUsuario_123@mail.com", "MiContrasena123");
		crearCanal("canal de prueba");
		
		Registro.crearUsuario("miUsuario_1234", "MiContrasena1234", "miUsuario_1234@mail.com");
		InicioSesion.iniciarSesion("miUsuario_1234@mail.com", "MiContrasena1234");
		subscribirse("canal de prueba");
		//desubscribirse("canal de prueba");
	}
	
	public static void crearCanal(String canal) {
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
			
			//datos
			int op = 3;
			byte [] nombre = "miUsuario_123@mail.com".getBytes();
			byte [] pass = canal.getBytes();
			
			//enviar
			//enviar cod
			flujo_out.writeInt(op);
			//enviar nombre
			flujo_out.writeInt(nombre.length);
			flujo_out.write(nombre);
			//enviar canal
			flujo_out.writeInt(pass.length);
			flujo_out.write(pass);

			//recibir y procesar respuesta
			int res = flujo_in.readInt();
			System.out.println("Resultado de la operación 'crear canal': " + res);
			
			
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void subscribirse(String canal) {
		//Registro.crearUsuario("miUsuario_1234", "MiContrasena123", "pepe2@mail.com");
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
			
			//datos
			int op = 4;
			byte [] nombre = "miUsuario_1234@mail.com".getBytes();
			byte [] pass = canal.getBytes();
			
			//enviar
			//enviar cod
			flujo_out.writeInt(op);
			//enviar nombre
			flujo_out.writeInt(nombre.length);
			flujo_out.write(nombre);
			//enviar canal
			flujo_out.writeInt(pass.length);
			flujo_out.write(pass);

			//recibir y procesar respuesta
			int res = flujo_in.readInt();
			System.out.println("Resultado de la operación 'suscribir a canal': " + res);
			
			
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void desubscribirse(String canal) {
		//Registro.crearUsuario("miUsuario_1234", "MiContrasena123", "pepe2@mail.com");
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
			
			//datos
			int op = 5;
			byte [] nombre = "miUsuario_1234@mail.com".getBytes();
			byte [] pass = canal.getBytes();
			
			//enviar
			//enviar cod
			flujo_out.writeInt(op);
			//enviar nombre
			flujo_out.writeInt(nombre.length);
			flujo_out.write(nombre);
			//enviar canal
			flujo_out.writeInt(pass.length);
			flujo_out.write(pass);

			//recibir y procesar respuesta
			int res = flujo_in.readInt();
			System.out.println("Resultado de la operación 'desuscribir a canal': " + res);
			
			
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
	}
}
