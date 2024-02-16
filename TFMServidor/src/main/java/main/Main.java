package main;

import static jakarta.persistence.Persistence.createEntityManagerFactory;
import static java.time.LocalDateTime.now;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import util.db.modelos.*;

//iniciar cosas tls, iniciar hibernate

public class Main {
	public static void main (String [] args) {
		
		//configurar seguridad
		//configurar manejador base de datos hibernate
		EntityManagerFactory entityManagerFactoryCredenciales = createEntityManagerFactory("org.hibernate.tfm.credenciales");
		EntityManagerFactory entityManagerFactoryApp = createEntityManagerFactory("org.hibernate.tfm.servidor");
		
		
		//bucle de servidor
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
		            case 0:
		            case 1:
		            case 2:
		            case 3:
		            case 4:
		            case 5:
		            case 6:
		            case 7:
		            case 8:
		            case 9:
		            case 10:
		                System.out.println("La opción está entre 0 y 10.");
		                break;
		            default:
		                System.out.println("La opción está fuera del rango de 0 a 10.");
	        }
				
				//siguiente petición
				System.out.println();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static void inTransaction(Consumer<EntityManager> work, EntityManagerFactory entityManagerFactory) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		try {
			transaction.begin();
			work.accept(entityManager);
			transaction.commit();
		}
		catch (Exception e) {
			if (transaction.isActive()) {
				transaction.rollback();
			}
			throw e;
		}
		finally {
			entityManager.close();
		}
	}
}
