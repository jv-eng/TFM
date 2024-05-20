package main;

import static jakarta.persistence.Persistence.createEntityManagerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
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
	public static Map<String, List<Socket>> mapa = new HashMap<String, List<Socket>>();
	private static boolean certCLDownloaded = false;
	
	public static void main (String [] args) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException {		
		
		//configurar manejador base de datos hibernate
		EntityManagerFactory entityManagerFactoryCredenciales = createEntityManagerFactory("org.hibernate.tfm.credenciales");
		EntityManagerFactory entityManagerFactoryApp = createEntityManagerFactory("org.hibernate.tfm.servidor");
		
		//configurar seguridad
		System.setProperty("javax.net.ssl.trustStore", Configuration.obtenerConfiguracion("almacenTrust"));
		System.setProperty("javax.net.ssl.trustStorePassword", Configuration.obtenerConfiguracion("claveAlmacenTrust"));
		System.setProperty("javax.net.ssl.keyStore", Configuration.obtenerConfiguracion("almacenSR"));
		System.setProperty("javax.net.ssl.keyStorePassword", Configuration.obtenerConfiguracion("claveAlmacenSR"));
		/*SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		SSLServerSocket socket_servidor = null;
		SSLSocket socket_sr = null;*/
		ServerSocket socket_servidor = null;
		Socket socket_sr = null;
		
		//bucle de servidor
		//ServerSocket socket_servidor;
		try {
			socket_servidor = new ServerSocket(Integer.parseInt(Configuration.obtenerConfiguracion("puerto")));
			//socket_servidor = (SSLServerSocket) factory.createServerSocket(Integer.parseInt(Configuration.obtenerConfiguracion("puerto")));
			//socket_servidor.setNeedClientAuth(true);
			//socket_servidor.setEnabledProtocols(new String[]{"TLSv1.3"});
			
			while (true) {
				System.out.println(); System.out.println();
				System.out.println("Esperando conexiones en puerto 12345");
				logg.info("Esperando conexiones en puerto 12345");
				System.out.println(); System.out.println();
				//aceptar conexión
				//Socket socket_sr = socket_servidor.accept();
				/*socket_sr = (SSLSocket) socket_servidor.accept();
				if (!certCLDownloaded) {
					almacenarCertificadoCliente(socket_sr.getSession(), Configuration.obtenerConfiguracion("almacenCL"), Configuration.obtenerConfiguracion("claveAlmacenCL"), "CertificadoCL");
					certCLDownloaded = true;
				}*/
				socket_sr = socket_servidor.accept();
				
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("fin");
	}

	private static void almacenarCertificadoCliente(SSLSession sslSession, String keystorePath, String keystorePassword, String alias) throws Exception {
        // Obtén el certificado del cliente de la sesión SSL
        X509Certificate[] clientCertificates = (X509Certificate[]) sslSession.getPeerCertificates();

        if (clientCertificates != null && clientCertificates.length > 0) {
            // Obtén el primer certificado del cliente
            X509Certificate clientCertificate = clientCertificates[0];

            // Carga el KeyStore existente o crea uno nuevo si no existe
            KeyStore keyStore = KeyStore.getInstance("JKS");
            try (FileInputStream fis = new FileInputStream(keystorePath)) {
                keyStore.load(fis, keystorePassword.toCharArray());
            } catch (IOException e) {
                // Si el archivo no existe, inicializa un KeyStore vacío
                keyStore.load(null, keystorePassword.toCharArray());
            }

            // Almacena el certificado en el KeyStore con el alias proporcionado
            keyStore.setCertificateEntry(alias, clientCertificate);

            // Guarda el KeyStore en el archivo
            try (FileOutputStream fos = new FileOutputStream(keystorePath)) {
                keyStore.store(fos, keystorePassword.toCharArray());
            }

            System.out.println("Certificado del cliente almacenado en " + keystorePath + " con alias " + alias);
        } else {
            System.out.println("No se encontraron certificados del cliente en la sesión SSL.");
        }
    }
}
