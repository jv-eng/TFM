package main;

import static jakarta.persistence.Persistence.createEntityManagerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import jakarta.persistence.EntityManagerFactory;
import manejadores.ManejadorFichero;
import manejadores.ManejadorCanal;
import manejadores.ManejadorSesion;
import manejadores.ManejadorUsuarioCredenciales;
import util.Configuration;

public class Main {
	
	//logger
	private static final Logger logg = (Logger) LogManager.getLogger("com.tfm.app");
	private static boolean certCLDownloaded = false;
	private static Certificate certCL = null;
	private static Map<String, List<Socket>> mapa = new HashMap<String, List<Socket>>();
	
	public static void main (String [] args) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException {		
		
		//configurar manejador base de datos hibernate
		EntityManagerFactory entityManagerFactoryCredenciales = createEntityManagerFactory("org.hibernate.tfm.credenciales");
		EntityManagerFactory entityManagerFactoryApp = createEntityManagerFactory("org.hibernate.tfm.servidor");
		
		//configurar seguridad
		System.setProperty("javax.net.ssl.trustStore", Configuration.obtenerConfiguracion("almacenTrust"));
		System.setProperty("javax.net.ssl.trustStorePassword", Configuration.obtenerConfiguracion("claveAlmacenTrust"));
		System.setProperty("javax.net.ssl.keyStore", Configuration.obtenerConfiguracion("almacenSR"));
		System.setProperty("javax.net.ssl.keyStorePassword", Configuration.obtenerConfiguracion("claveAlmacenSR"));
				
		SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		SSLServerSocket socket_servidor = null;
		SSLSocket socket_sr = null;
		
		//bucle de servidor
		try {
			socket_servidor = (SSLServerSocket) factory.createServerSocket(Integer.parseInt(Configuration.obtenerConfiguracion("puerto")));
			socket_servidor.setNeedClientAuth(true);
			socket_servidor.setEnabledProtocols(new String[]{"TLSv1.3"});
			socket_servidor.setEnabledCipherSuites(new String[] {"TLS_AES_256_GCM_SHA384"});
			
			while (true) {
				System.out.println(); System.out.println();
				System.out.println("Esperando conexiones en puerto 12345");
				logg.info("Esperando conexiones en puerto 12345");
				System.out.println(); System.out.println();
				
				//aceptar conexión
				socket_sr = (SSLSocket) socket_servidor.accept();
				if (!certCLDownloaded) {
					certCLDownloaded = true;
					certCL = socket_sr.getSession().getPeerCertificates()[0];
				}

				//recibir operador
				int op = (new DataInputStream(socket_sr.getInputStream())).readInt();
				int res = 0;
				
				//revisar operador recibido
				switch (op) {
		            case 0: //crear usuario
		            	ManejadorUsuarioCredenciales u = new ManejadorUsuarioCredenciales(entityManagerFactoryCredenciales, entityManagerFactoryApp, socket_sr);
		            	res = u.crearUsuario();
		            	break;
		            case 1: //iniciar sesión
		            	System.out.println("iniciamos sesion");
		            	ManejadorSesion sesion = new ManejadorSesion(entityManagerFactoryCredenciales, socket_sr);
		            	res = sesion.iniciarSesion();
		            	break;
		            case 2: //cerrar sesión
		            	ManejadorSesion sesionCerrar = new ManejadorSesion(entityManagerFactoryCredenciales, socket_sr);
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
		            	ManejadorFichero fichEnviar = new ManejadorFichero(entityManagerFactoryApp, entityManagerFactoryCredenciales, socket_sr);
		            	res = fichEnviar.enviarFichero();
		            	break;
		            case 7:
		            	break;
		            case 8: //descargar fichero
		            	ManejadorFichero fichDescargar = new ManejadorFichero(entityManagerFactoryApp, entityManagerFactoryCredenciales, socket_sr);
		            	res = fichDescargar.descargarFichero();
		            	break;
		            default:
		            	logg.error("Error, código de operación no válido.");
		                System.out.println("La opción está fuera del rango de 0 a 8.");
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("fin");
	}

	public static Certificate getCertCL() {
		return certCL;
	}

	public static void setCertCL(Certificate certCL) {
		Main.certCL = certCL;
	}

	public static Map<String, List<Socket>> getMapa() {
		return mapa;
	}

	public static void setMapa(Map<String, List<Socket>> mapa) {
		Main.mapa = mapa;
	}
    
}
