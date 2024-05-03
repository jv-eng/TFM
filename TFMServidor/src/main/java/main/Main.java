package main;

import static jakarta.persistence.Persistence.createEntityManagerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import canales.ManejadorCanal;
import ficheros.Fichero;
import jakarta.persistence.EntityManagerFactory;
import usuarios.Sesion;
import usuarios.UsuarioCredenciales;
import util.Configuration;

public class Main {
	
	//logger
	private static final Logger logg = (Logger) LogManager.getLogger("com.tfm.app");
	public static Map<String, List<Socket>> mapa = new HashMap<String, List<Socket>>();
	
	public static void main (String [] args) {		
		
		//configurar manejador base de datos hibernate
		EntityManagerFactory entityManagerFactoryCredenciales = createEntityManagerFactory("org.hibernate.tfm.credenciales");
		EntityManagerFactory entityManagerFactoryApp = createEntityManagerFactory("org.hibernate.tfm.servidor");
		
		//configurar seguridad
		System.setProperty("javax.net.ssl.trustStore", "AlmacenSRTrust");
		System.setProperty("javax.net.ssl.trustStorePassword", "N45i2on[!%");
		System.setProperty("javax.net.ssl.keyStore", "AlmacenSR");
		System.setProperty("javax.net.ssl.keyStorePassword", "dW716*h??Y");
		System.setProperty("javax.net.debug","ssl");
		SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		SSLServerSocket socket_servidor = null;
		SSLSocket socket_sr = null;
		
		//bucle de servidor
		//ServerSocket socket_servidor;
		try {
			//socket_servidor = new ServerSocket(Integer.parseInt(Configuration.obtenerConfiguracion("puerto")));
			socket_servidor = (SSLServerSocket) factory.createServerSocket(Integer.parseInt(Configuration.obtenerConfiguracion("puerto")));
			socket_servidor.setNeedClientAuth(true);
			
			while (true) {
				System.out.println(); System.out.println();
				System.out.println("Esperando conexiones en puerto 12345");
				logg.info("Esperando conexiones en puerto 12345");
				System.out.println(); System.out.println();
				//aceptar conexión
				//Socket socket_sr = socket_servidor.accept();
				socket_sr = (SSLSocket) socket_servidor.accept();
				
				//recibir operador
				int op = (new DataInputStream(socket_sr.getInputStream())).readInt();
				int res = 0;
				
				//revisar operador recibido
				switch (op) {
		            case 0: //crear usuario
		            	UsuarioCredenciales u = new UsuarioCredenciales(entityManagerFactoryCredenciales, entityManagerFactoryApp, socket_sr);
		            	res = u.crearUsuario();
		            	break;
		            case 1: //iniciar sesión
		            	System.out.println("iniciamos sesion");
		            	Sesion sesion = new Sesion(entityManagerFactoryCredenciales, socket_sr);
		            	res = sesion.iniciarSesion();
		            	break;
		            case 2: //cerrar sesión
		            	Sesion sesionCerrar = new Sesion(entityManagerFactoryCredenciales, socket_sr);
		            	res = sesionCerrar.cerrarrSesion();
		            	break;
		            case 3: //crear canal
		            	ManejadorCanal crearCanal = new ManejadorCanal(entityManagerFactoryApp, socket_sr);
		            	res = crearCanal.crearCanal();
		            	break;
		            case 4: //subscribirse
		            	ManejadorCanal subscribirse = new ManejadorCanal(entityManagerFactoryApp, socket_sr);
		            	res = subscribirse.suscribirse();
		            	break;
		            case 5: //desubscribirse
		            	ManejadorCanal desubscribirse = new ManejadorCanal(entityManagerFactoryApp, socket_sr);
		            	res = desubscribirse.desuscribirse();
		            	break;
		            case 6: //enviar fichero
		            	Fichero fichEnviar = new Fichero(entityManagerFactoryApp, entityManagerFactoryCredenciales, socket_sr);
		            	res = fichEnviar.enviarFichero();
		            	break;
		            case 7:
		            	break;
		            case 8: //descargar fichero
		            	Fichero fichDescargar = new Fichero(entityManagerFactoryApp, entityManagerFactoryCredenciales, socket_sr);
		            	res = fichDescargar.descargarFichero();
		            	break;
		            default:
		            	logg.error("Error, código de operación no válido.");
		                System.out.println("La opción está fuera del rango de 0 a 10.");
		                res = -1;
				}
				
				//responder al cliente
				(new DataOutputStream(socket_sr.getOutputStream())).writeInt(res);
				
				//siguiente petición
				System.out.println("Fin tratamiento");
			}
		} catch (IOException e) {
			logg.error("Error recibiendo el código de operación.");
			e.printStackTrace();
		}
		System.out.println("fin");
	}
	
}
