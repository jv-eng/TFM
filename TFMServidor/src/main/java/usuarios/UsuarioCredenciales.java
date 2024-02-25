package usuarios;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import jakarta.persistence.EntityManagerFactory;
import util.db.manejadoresDAO.interfaces.UsuarioCredencialesDAO;
import util.db.manejadoresDAO.interfaces.UsuarioDAO;
import util.db.manejadoresDAO.manejadores.HibernateUsuarioCredencialesDAO;
import util.db.manejadoresDAO.manejadores.HibernateUsuarioDAO;

//se encarga del manejo de usuarios
public class UsuarioCredenciales {
	
	//logger
	private static final Logger logg = (Logger) LogManager.getLogger("com.tfm.creacion_usuarios");
	
	private EntityManagerFactory managerApp;
	private EntityManagerFactory managerUsuario;
	private Socket socket;

	public UsuarioCredenciales(EntityManagerFactory entityManagerFactoryCredenciales, EntityManagerFactory entityManagerFactoryApp, Socket socket_sr) {
		this.managerUsuario = entityManagerFactoryCredenciales;
		this.managerApp = entityManagerFactoryApp;
		this.socket = socket_sr;
	}

	public int crearUsuario() {
		int res = 0;
		
		try {
			DataInputStream flujo_e = new DataInputStream(this.socket.getInputStream());

			//obtener nombre
			int tam = flujo_e.readInt();
			byte [] buff = new byte[tam];
			flujo_e.read(buff);
			String usuario = new String(buff, 0, tam, "UTF-8");
			System.out.println("Nombre: " + usuario);
			
			//obtener contraseña
			tam = flujo_e.readInt();
			buff = new byte[tam];
			flujo_e.read(buff);
			String pass = new String(buff, 0, tam, "UTF-8");
			System.out.println("pass: " + pass);
			
			//obtener correo
			tam = flujo_e.readInt();
			buff = new byte[tam];
			flujo_e.read(buff);
			String correo = new String(buff, 0, tam, "UTF-8");
			System.out.println("mail: " + correo);
			
			//comprobar formato nombre, correo y contraseña
			if (correo.matches("^[a-zA-Z0-9_]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,}$") && 
					usuario.matches("^[a-zA-Z0-9_]{3,20}$") && pass.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")) {
				//crear los DAO
				UsuarioCredencialesDAO usuarioCDAO = new HibernateUsuarioCredencialesDAO(managerUsuario);
				UsuarioDAO usuarioDAO = new HibernateUsuarioDAO(managerApp);
				
				//comprobar si existe un usuario con ese nombre
				if (usuarioCDAO.comprobarUsuario(usuario)) {
					//usuario existe
					logg.error("Usuario " + usuario + " ya existe.");
				} else {
					//almacenar en base de datos credenciales
					usuarioCDAO.crearUsuario(usuario, correo, pass);
					
					//almacenar en base de datos app
					usuarioDAO.crearUsuario(usuario, correo, pass);
					
					//cambiar resultado
					res = 0;
					
					logg.info("Usuario \"" + usuario + "\" creado correctamente.");
				}
			} else {
				//codigo para error de formato
				logg.error("Error en el formato de los datos recibidos.");
				res = 2;
			}
			
			
		} catch (IOException e) {
			logg.error("Error al recibir los datos.");
			res = 1;
			return 1;
		} 
		
		return res;
	}
	

}
