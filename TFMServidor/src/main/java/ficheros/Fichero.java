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

	public int enviarFichero() {
		return 0;
		// TODO Auto-generated method stub
		
	}

	public int recibirFichero() {
		return 0;
		// TODO Auto-generated method stub
		
	}

	public int descargarFichero() {
		return 0;
		// TODO Auto-generated method stub
		
	}

}
