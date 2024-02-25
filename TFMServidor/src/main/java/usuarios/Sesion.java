package usuarios;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.security.PublicKey;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import jakarta.persistence.EntityManagerFactory;
import util.Firma;
import util.db.manejadoresDAO.interfaces.UsuarioCredencialesDAO;
import util.db.manejadoresDAO.manejadores.HibernateUsuarioCredencialesDAO;

public class Sesion {
	
	//logger
	private static final Logger logg = (Logger) LogManager.getLogger("com.tfm.sesiones");
	
	private EntityManagerFactory managerUsuario;
	private Socket socket;

	public Sesion(EntityManagerFactory entityManagerFactoryCredenciales, Socket socket_sr) {
		this.managerUsuario = entityManagerFactoryCredenciales;
		this.socket = socket_sr;
	}

	public int iniciarSesion() {
		int res = 0;
		
		try {
			DataInputStream flujo_e = new DataInputStream(this.socket.getInputStream());
			
			//obtener correo
			int tam = flujo_e.readInt();
			byte [] buff = new byte[tam];
			flujo_e.read(buff);
			String correo = new String(buff, 0, tam, "UTF-8");
			System.out.println("mail: " + correo);
			
			//obtener contraseña
			tam = flujo_e.readInt();
			buff = new byte[tam];
			flujo_e.read(buff);
			String pass = new String(buff, 0, tam, "UTF-8");
			System.out.println("pass: " + pass);
			
			//obtener clave publica firma
			ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
	        PublicKey key = (PublicKey) objectInputStream.readObject();
	        System.out.println("clave del servidor:\t" + key);
	        String clave = Firma.claveString(key);
	        
	        //comprobar si el usuario existe y las credenciales son correctas
	        UsuarioCredencialesDAO usuarioCDAO = new HibernateUsuarioCredencialesDAO(managerUsuario);
	        
	        //si hay error, cambiamos el resultado
	        if (!usuarioCDAO.comprobarCredenciales(correo, pass)) {
	        	logg.error("Error cuando el usuario \"" + correo + "\" ha intentado iniciar sesión.");
	        	res = 1;
	        } else {
	        	logg.info("El usuario \"" + correo + "\" ha iniciado sesión correctamente");
	        	//almacenar clave
	        	usuarioCDAO.insertarClave(correo, clave);
	        }
	        
		} catch (IOException e) {
			res = 1;
			logg.error("Error al recibir los datos.");
		} catch (ClassNotFoundException e) {
			res = 1;
			logg.error("Error al recibir la clave.");
		}
		
		return res;
		
	}

	public int cerrarrSesion() {
		return 0;		
	}
	
	

}
