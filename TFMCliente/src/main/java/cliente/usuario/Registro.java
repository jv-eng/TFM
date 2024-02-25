package cliente.usuario;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Registro {
	public static void crearUsuario() {
		try {
			
			String server_ip = "localhost";
			String server_port = "12345";
			
			//crear socket
			Socket sock = new Socket(server_ip, Integer.parseInt(server_port));
			
			//streams
			OutputStream output_stream = sock.getOutputStream();
			InputStream input_stream = sock.getInputStream();
			//flujos para comunicar
			DataInputStream flujo_in = new DataInputStream(input_stream);
			DataOutputStream flujo_out = new DataOutputStream(output_stream);
			
			
			//data
			int op = 0;
			byte [] nombre = "miUsuario_123".getBytes();
			byte [] pass = "MiContrasena123".getBytes();
			byte [] mail = "pepe@mail.com".getBytes();
			
			
			//enviar cod
			flujo_out.writeInt(op);
			//enviar nombre
			flujo_out.writeInt(nombre.length);
			flujo_out.write(nombre);
			//enviar contraseña
			flujo_out.writeInt(pass.length);
			flujo_out.write(pass);
			//enviar correo
			flujo_out.writeInt(mail.length);
			flujo_out.write(mail);
			//recibir y procesar respuesta
			int res = flujo_in.readInt();
			System.out.println("Resultado de la operación: " + res);
			
			//cerrar
			flujo_in.close();
			flujo_out.close();
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String [] args) {
		crearUsuario();
	}
}
