package usuario;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class CerrarSesion {
	public static void main(String[] args) {
		System.setProperty("javax.net.ssl.trustStore", "AlmacenCLTrust");
		System.setProperty("javax.net.ssl.trustStorePassword", "3Sk8z5Q]!");
		System.setProperty("javax.net.ssl.keyStore", "AlmacenCL");
		System.setProperty("javax.net.ssl.keyStorePassword", "7N79:lAe!9");
		
		Registro.crearUsuario("miUsuarioCerrar_123", "MiContrasena123", "pepeCerrar@mail.com");
		InicioSesion.iniciarSesion("pepeCerrar@mail.com", "MiContrasena123");
		
		String server_ip = "localhost";
		String server_port = "12345";

		try {
			//crear socket
			SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket sock = (SSLSocket) sslsocketfactory.createSocket(server_ip, Integer.parseInt(server_port));
			
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
			System.out.println("Resultado de la operaci�n: " + res);
			
			sock.close();
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		} 
	}
}
