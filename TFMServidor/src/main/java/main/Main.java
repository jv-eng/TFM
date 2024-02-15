package main;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
	public static void main (String [] args) {
		ServerSocket socket_servidor;
		try {
			socket_servidor = new ServerSocket(12345);
			while (true) {
				System.out.println("Esperando conexiones en puerto 9999");
				
				//aceptar conexión
				Socket socket_sr = socket_servidor.accept();
				
				//recibir operador
				int op = (new DataInputStream(socket_sr.getInputStream())).readInt();
				
				//revisar operador recibido
				switch (op) {
					
				}
				
				//siguiente petición
				System.out.println();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
