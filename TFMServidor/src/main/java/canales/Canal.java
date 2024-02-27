package canales;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import jakarta.persistence.EntityManagerFactory;
import util.db.manejadoresDAO.interfaces.CanalDAO;
import util.db.manejadoresDAO.interfaces.UsuarioDAO;
import util.db.manejadoresDAO.manejadores.HibernateCanalDAO;
import util.db.manejadoresDAO.manejadores.HibernateUsuarioDAO;
import util.db.modelos.Usuario;

public class Canal {
	
	//logger
	private static final Logger logg = (Logger) LogManager.getLogger("com.tfm.app");
	
	private EntityManagerFactory managerApp;
	private Socket socket;

	public Canal(EntityManagerFactory entityManagerFactoryApp, Socket socket_sr) {
		this.managerApp = entityManagerFactoryApp;
		this.socket = socket_sr;
	}

	public int crearCanal() {
		int res = 0;
		
		try {
			DataInputStream flujo_e = new DataInputStream(this.socket.getInputStream());
			
			//obtener correo
			int tam = flujo_e.readInt();
			byte [] buff = new byte[tam];
			flujo_e.read(buff);
			String correo = new String(buff, 0, tam, "UTF-8");
			System.out.println("mail: " + correo);
			
			//nombre del canal
			tam = flujo_e.readInt();
			buff = new byte[tam];
			flujo_e.read(buff);
			String canal = new String(buff, 0, tam, "UTF-8");
			System.out.println("canal: " + canal);
			
			UsuarioDAO usuarioDAO = new HibernateUsuarioDAO(this.managerApp);
			CanalDAO canalDAO = new HibernateCanalDAO(this.managerApp);
			
			//comprobar si existe el usuario
			if (usuarioDAO.comprobarUsuario(correo)) {
				//comprobar si existe el canal
				if (canalDAO.comprobarCanal(canal)) {
					//crear canal
					Usuario usuario = usuarioDAO.getUsuario(correo);
					canalDAO.crearCanal(usuario, canal);
					logg.info("El canal \"" + canal + "\" ha sido creado por el usuario \"" + usuario.getNombreUsuario() + "\".");
					System.out.println("El canal \"" + canal + "\" ha sido creado por el usuario \"" + usuario.getNombreUsuario() + "\".");
				} else {
					logg.error("Error, el canal \"" + canal + "\" ya existe.");
					res = 3;
				}
			} else {
				logg.error("Error, el usuario con correo \"" + correo + "\" no existe.");
				res = 2;
			}
			
		} catch (IOException e) {
			logg.error("Error al recibir los datos.");
		}
		
		return res;

	}

	public int subscribirse() {
		return 0;
		// TODO Auto-generated method stub
		
	}
	
	public int desubscribirse() {
		return 0;
	}

}
