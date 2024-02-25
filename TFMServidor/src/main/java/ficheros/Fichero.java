package ficheros;

import java.net.Socket;

import jakarta.persistence.EntityManagerFactory;

//gestionar el tema de almacenamiento en esta clase o en otras
public class Fichero {
	
	private EntityManagerFactory manager;
	
	public Fichero(EntityManagerFactory manager, Socket socket_sr) {
		this.manager = manager;
	}
	
	public void run() {
		
	}

	public void enviarFichero() {
		// TODO Auto-generated method stub
		
	}

	public void recibirFichero() {
		// TODO Auto-generated method stub
		
	}

	public void descargarFichero() {
		// TODO Auto-generated method stub
		
	}

}
