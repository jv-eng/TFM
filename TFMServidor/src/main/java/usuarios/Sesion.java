package usuarios;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.PublicKey;

import javax.net.ssl.SSLSocket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import jakarta.persistence.EntityManagerFactory;
import util.Serializar;
import util.db.manejadoresDAO.interfaces.UsuarioCredencialesDAO;
import util.db.manejadoresDAO.manejadores.HibernateUsuarioCredencialesDAO;

public class Sesion {
	
	//logger
	private static final Logger logg = (Logger) LogManager.getLogger("com.tfm.sesiones");
	
	private EntityManagerFactory managerUsuario;
	private SSLSocket socket;

	public Sesion(EntityManagerFactory entityManagerFactoryCredenciales, SSLSocket socket_sr) {
		this.managerUsuario = entityManagerFactoryCredenciales;
		this.socket = socket_sr;
	}

	public int iniciarSesion() {
		int res = 0;
		UsuarioCredencialesDAO usuarioCDAO = null;
		String correo = null;
		
		try {
			DataInputStream flujo_e = new DataInputStream(this.socket.getInputStream());
			DataOutputStream output = new DataOutputStream(this.socket.getOutputStream());
			
			//obtener correo
			int tam = flujo_e.readInt();
			byte [] buff = new byte[tam];
			flujo_e.read(buff);
			correo = new String(buff, 0, tam, "UTF-8");
			correo = correo.toLowerCase().trim();
			
			//obtener contraseña
			tam = flujo_e.readInt();
			buff = new byte[tam];
			flujo_e.read(buff);
			String pass = new String(buff, 0, tam, "UTF-8");
			
			//obtener clave publica firma
	        tam = flujo_e.readInt();
			buff = new byte[tam];
			flujo_e.read(buff);
        
	        PublicKey key = Serializar.stringClave(new String(buff, 0, tam, "UTF-8"));
	        String clave = Serializar.claveString(key);
	        
	        //comprobar si el usuario existe y las credenciales son correctas
	        usuarioCDAO = new HibernateUsuarioCredencialesDAO(this.managerUsuario);
	        
	        //si hay error, cambiamos el resultado
	        if (!usuarioCDAO.comprobarCredenciales(correo, pass)) {
	        	logg.error("Error cuando el usuario \"" + correo + "\" ha intentado iniciar sesión.");
	        	res = -1;
	        } else {
	        	logg.info("El usuario \"" + correo + "\" ha iniciado sesión correctamente");
	        	//almacenar clave
	        	usuarioCDAO.insertarClave(correo, clave);
	        	//enviar nombre
	        	String nombreUsuario = usuarioCDAO.getNombreUsuario(correo);
				byte [] nombreByte = nombreUsuario.getBytes();
				output.writeInt(nombreByte.length);
				output.write(nombreByte);
	        }
	        
		} catch (Exception e) {
			res = -1;
			logg.error("Error al recibir los datos.");
		}
	
		return res;
		
	}

	public int cerrarrSesion() {
		int res = 0;
		
		try {
			DataInputStream flujo_e = new DataInputStream(this.socket.getInputStream());
			
			//obtener correo
			int tam = flujo_e.readInt();
			byte [] buff = new byte[tam];
			flujo_e.read(buff);
			String correo = new String(buff, 0, tam, "UTF-8");
			
			//comprobar si el usuario existe
	        UsuarioCredencialesDAO usuarioCDAO = new HibernateUsuarioCredencialesDAO(this.managerUsuario);
	        
	        if (usuarioCDAO.comprobarUsuario(correo)) {
	        	//borrar clave
	        	usuarioCDAO.borrarClave(correo);
	        	logg.info("Sesión de usuario \"" + correo + "\" cerrada.");
	        }
			
		} catch (IOException e) {
			logg.info("Error al cerrar la sesión.");
			res = 1;
		}
		
		return res;		
	}
	
	

}
