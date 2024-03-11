package usuario;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class CerrarSesion {
	public static void main(String[] args) {
		InicioSesion.iniciarSesion("miUsuario_123", "MiContrasena123");
		
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
			int op = 2;
			byte [] nombre = "pepe@mail.com".getBytes();
			
			//enviar
			//enviar cod
			flujo_out.writeInt(op);
			//enviar nombre
			flujo_out.writeInt(nombre.length);
			flujo_out.write(nombre);
			
			//recibir y procesar respuesta
			int res = flujo_in.readInt();
			System.out.println("Resultado de la operación: " + res);
			
			
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		} 
	}
}
