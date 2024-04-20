package main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import canales.ManejadorCanal;
import ficheros.Fichero;
import usuarios.Sesion;
import usuarios.UsuarioCredenciales;

//iniciar cosas tls, iniciar hibernate

public class Main {
	
	//logger
	private static final Logger logg = (Logger) LogManager.getLogger("com.tfm.app");
	public static Map<String, List<Socket>> mapa = new HashMap<String, List<Socket>>();
	
	public static void main (String [] args) {		
		
		//configurar seguridad
		
		
		//bucle de servidor
		ServerSocket socket_servidor;
		try {
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nombre_base_de_datos", "usuario_test", "123");
			socket_servidor = new ServerSocket(12345);
			while (true) {
				System.out.println("Esperando conexiones en puerto 12345");
				logg.info("Esperando conexiones en puerto 12345");
				System.out.println(); System.out.println();
				//aceptar conexión
				Socket socket_sr = socket_servidor.accept();
				
				//recibir operador
				int op = (new DataInputStream(socket_sr.getInputStream())).readInt();
				int res = 0;
				
				//revisar operador recibido
				switch (op) {
		            case 0: //crear usuario
		            	UsuarioCredenciales u = new UsuarioCredenciales(conn, socket_sr);
		            	res = u.crearUsuario();
		            	break;
		            case 1: //iniciar sesión
		            	System.out.println("iniciamos sesion");
		            	Sesion sesion = new Sesion(conn, socket_sr);
		            	res = sesion.iniciarSesion();
		            	break;
		            case 2: //cerrar sesión
		            	Sesion sesionCerrar = new Sesion(conn, socket_sr);
		            	res = sesionCerrar.cerrarrSesion();
		            	break;
		            case 3: //crear canal
		            	ManejadorCanal crearCanal = new ManejadorCanal(conn, socket_sr);
		            	res = crearCanal.crearCanal();
		            	break;
		            case 4: //subscribirse
		            	ManejadorCanal subscribirse = new ManejadorCanal(conn, socket_sr);
		            	res = subscribirse.suscribirse();
		            	break;
		            case 5: //desubscribirse
		            	ManejadorCanal desubscribirse = new ManejadorCanal(conn, socket_sr);
		            	res = desubscribirse.desuscribirse();
		            	break;
		            case 6: //enviar fichero
		            	Fichero fichEnviar = new Fichero(conn, socket_sr);
		            	res = fichEnviar.enviarFichero();
		            	break;
		            case 7: //recibir fichero
		            	Fichero fichRecibir = new Fichero(conn, socket_sr);
		            	res = fichRecibir.recibirFichero();
		            	break;
		            case 8: //descargar fichero
		            	Fichero fichDescargar = new Fichero(conn, socket_sr);
		            	res = fichDescargar.descargarFichero();
		            	break;
		            default:
		            	logg.error("Error, código de operación no válido.");
		                System.out.println("La opción está fuera del rango de 0 a 10.");
		                res = -1;
				}
				
				//responder al cliente
				(new DataOutputStream(socket_sr.getOutputStream())).writeInt(res);
				//socket_sr.close();
				
				//siguiente petición
				System.out.println("Fin tratamiento");
			}
		} catch (IOException e) {
			logg.error("Error recibiendo el código de operación.");
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("fin");
	}
	
}
