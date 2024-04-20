package fichero;

import usuario.InicioSesion;
import usuario.Registro;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

import canal.Canal;

public class Fichero2 {
	public static void main(String [] args) {
		//crear usuario
		Registro.crearUsuario("miUsuario_12345", "MiContrasena12345", "miUsuario_12345@mail.com");
		//iniciar sesion
		InicioSesion.iniciarSesion("miUsuario_12345@mail.com", "MiContrasena12345");
		Canal.subscribirse("miUsuario_12345@mail.com", "canal de prueba");
		Scanner scanner = new Scanner(System.in);
		char caracter = scanner.next().charAt(0);
		scanner.close();
		//descargar fichero
				descargarrFichero("hibernate-tutorials.zip");
	}
	
	public static void enviarFichero(String fichero) {
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
			int op = 6;
			byte [] nombre = "miUsuario_123@mail.com".getBytes();
			byte [] canal = "canal de prueba".getBytes();
			byte [] fich = fichero.getBytes();
			
			File f = new File("C:\\Users\\Juan_\\Desktop\\"+fichero);
			long longitud_mensaje = f.length();
			
			//enviar
			//enviar cod
			flujo_out.writeInt(op);
			//enviar nombre
			flujo_out.writeInt(nombre.length);
			flujo_out.write(nombre);
			//enviar canal
			flujo_out.writeInt(canal.length);
			flujo_out.write(canal);
			//enviar nombre del fichero
			flujo_out.writeInt(fich.length);
			flujo_out.write(fich);
			//enviar tama�o
			flujo_out.writeLong(longitud_mensaje);
			
			//enviar fichero
			byte [] envio = new byte[64000];
			FileInputStream fich_stream = new FileInputStream("C:\\Users\\Juan_\\Downloads\\"+fichero);
			
			long num_total = 0;
			int bytes_leidos = 0;
			
			do {
				bytes_leidos = fich_stream.read(envio);
				num_total += bytes_leidos;
				flujo_out.write(envio,0, bytes_leidos);
				System.out.println("NumBytesLeidos "+ bytes_leidos );
				System.out.println("longitud enviada fich: "+ num_total );
			} while(num_total < longitud_mensaje);
			
			//cerrar
			fich_stream.close();

			//recibir y procesar respuesta
			int res = flujo_in.readInt();
			System.out.println("Resultado de la operaci�n 'enviar fichero': " + res);
			
			
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void descargarrFichero(String fichero) {
		String server_ip = "localhost";
		String server_port = "12345";
		byte [] buff;
		try {
			//crear socket
			Socket sock = new Socket(server_ip, Integer.parseInt(server_port));
			
			//streams
			OutputStream output_stream = sock.getOutputStream();
			InputStream input_stream = sock.getInputStream();
			//flujos para comunicar
			DataInputStream flujo_in = new DataInputStream(input_stream);
			DataOutputStream flujo_out = new DataOutputStream(output_stream);
			System.out.println("iniciamos descarga del fichero");
			//datos
			int op = 8;
			byte [] nombre = "miUsuario_1234@mail.com".getBytes();
			byte [] canal = "canal de prueba".getBytes();
			byte [] fich = fichero.getBytes();

			
			//enviar
			//enviar cod
			flujo_out.writeInt(op);
			//enviar nombre
			flujo_out.writeInt(nombre.length);
			flujo_out.write(nombre);
			//enviar canal
			flujo_out.writeInt(canal.length);
			flujo_out.write(canal);
			//enviar nombre del fichero
			flujo_out.writeInt(fich.length);
			flujo_out.write(fich);
			System.out.println("datos enviados");
			//recibir longitud
			long num_recibido = flujo_in.readLong();
			System.out.println("Tama�o fichero: " + num_recibido);
			
			//recibir mensaje
			if (num_recibido > 64000) buff = new byte[64000];
			else buff = new byte[(int) num_recibido];
			System.out.println("tama�o: " + num_recibido);
			System.out.println("nomrbe: " + fichero);
			
			FileOutputStream fich2 = new FileOutputStream("C:\\Users\\Juan_\\OneDrive\\Escritorio"+fichero);
			int bytes_leidos = 0;
			long bytes_acumulados = 0;
			
			do {
				bytes_leidos = flujo_in.read(buff);
				bytes_acumulados += bytes_leidos;
				fich2.write(buff,0,bytes_leidos);
				System.out.println("Recibiendo Fichero ...");
				System.out.println("longitud enviada fich: " + bytes_acumulados);
				System.out.println("NumBytesLeidos "+ bytes_leidos);
			} while(bytes_acumulados < num_recibido);
			
			fich2.close();

			//recibir y procesar respuesta
			int res = flujo_in.readInt();
			System.out.println("Resultado de la operaci�n 'cliente descarga fichero': " + res);
			
			
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
	}
}
