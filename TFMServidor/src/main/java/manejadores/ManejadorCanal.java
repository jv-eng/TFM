package manejadores;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.SSLSocket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import jakarta.persistence.EntityManagerFactory;
import main.Main;
import util.db.manejadoresDAO.interfaces.CanalDAO;
import util.db.manejadoresDAO.interfaces.SuscripcionDAO;
import util.db.manejadoresDAO.interfaces.UsuarioDAO;
import util.db.manejadoresDAO.manejadores.HibernateCanalDAO;
import util.db.manejadoresDAO.manejadores.HibernateSuscripcionDAO;
import util.db.manejadoresDAO.manejadores.HibernateUsuarioDAO;
import util.db.modelos.Canal;
import util.db.modelos.Suscripcion;
import util.db.modelos.Usuario;

public class ManejadorCanal {
	
	//logger
	private static final Logger logg = (Logger) LogManager.getLogger("com.tfm.app");
	
	private EntityManagerFactory managerApp;
	//private SSLSocket socket;
	private Socket socket;

	//public ManejadorCanal(EntityManagerFactory entityManagerFactoryApp, SSLSocket socket_sr) {
	public ManejadorCanal(EntityManagerFactory entityManagerFactoryApp, Socket socket_sr) {
		this.managerApp = entityManagerFactoryApp;
		this.socket = socket_sr;
	}

	public int crearCanal() {
		int res = 0;
		System.out.println("Crear canal");
		
		try {
			DataInputStream flujo_e = new DataInputStream(this.socket.getInputStream());
			
			//obtener correo
			int tam = flujo_e.readInt();
			byte [] buff = new byte[tam];
			flujo_e.read(buff);
			String correo = new String(buff, 0, tam, "UTF-8");
			correo = correo.toLowerCase();
			
			//nombre del canal
			tam = flujo_e.readInt();
			buff = new byte[tam];
			flujo_e.read(buff);
			String canal = new String(buff, 0, tam, "UTF-8");
			
			UsuarioDAO usuarioDAO = new HibernateUsuarioDAO(this.managerApp);
			CanalDAO canalDAO = new HibernateCanalDAO(this.managerApp);
			
			//comprobar si existe el usuario
			if (usuarioDAO.existeUsuario(correo)) {
				//comprobar si existe el canal
				if (!canalDAO.existeCanal(canal)) {
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
			return 1;
		}
		
		return res;

	}

	public int suscribirse() {
		int res = 0;
		System.out.println("Suscripcion");
		
		try {
			DataInputStream flujo_e = new DataInputStream(this.socket.getInputStream());
			DataOutputStream flujo_out = new DataOutputStream(this.socket.getOutputStream());
			
			//obtener usuario
			int tam = flujo_e.readInt();
			byte [] buff = new byte[tam];
			flujo_e.read(buff);
			String usuario = new String(buff, 0, tam, "UTF-8");
			usuario = usuario.toLowerCase();
			
			//nombre del canal
			tam = flujo_e.readInt();
			buff = new byte[tam];
			flujo_e.read(buff);
			String canal = new String(buff, 0, tam, "UTF-8");
			
			//crear dao
			UsuarioDAO usuarioDAO = new HibernateUsuarioDAO(this.managerApp);
			CanalDAO canalDAO = new HibernateCanalDAO(this.managerApp);
			SuscripcionDAO suscripcionDAO = new HibernateSuscripcionDAO(this.managerApp);
			Usuario usuarioObj = usuarioDAO.getUsuario(usuario);

			//comprobar si existe el canal
			if (canalDAO.existeCanal(canal)) {
				//comprobar si el usuario esta suscrito al canal
				Canal c = canalDAO.getCanal(canal);
				if (usuarioDAO.existeUsuario(usuario)) {// && !suscripcionDAO.usuarioSuscrito(usuarioObj, c)) {
					//obtener datos para la suscripcion
					Usuario u = usuarioDAO.getUsuario(usuario);

					//suscribir usuario
					if (!suscripcionDAO.usuarioSuscrito(usuarioObj, c)) suscripcionDAO.suscribir(u, c, socket.getInetAddress().getHostAddress(), socket.getPort());
					
					if (Main.mapa.get(canal) != null) {
						Main.mapa.get(canal).add(socket);
					} else {
						Main.mapa.put(canal, new LinkedList<Socket>());
						Main.mapa.get(canal).add(socket);
					}
					
					byte [] nombreFich;
					List<String> ficheros = canalDAO.getArchivosCanal(canal);
					
					//enviar numero de ficheros
					flujo_out.writeInt(ficheros.size());
					
					if (!ficheros.isEmpty() ) {
						//enviar nombres de los ficheros
						for (String str: ficheros) {
							nombreFich = str.getBytes();
				            flujo_out.writeInt(nombreFich.length);
				            flujo_out.write(nombreFich);
						}
					}
				} else {
					logg.error("Error, el usuario \"" + usuario + "\" ya está suscrito al canal \"" + canal + "\".");
					res = 3;
				}
			} else {
				logg.error("Error, el canal \"" + canal + "\" no existe.");
				res = 2;
			}
			
		} catch (IOException e) {
			logg.error("Error al recibir los datos.");
			return 1;
		}
		
		return res;
	}
	
	public int desuscribirse() {
		int res = 0;
		System.out.println("Desuscripción");
		
		try {
			DataInputStream flujo_e = new DataInputStream(this.socket.getInputStream());

			//obtener usuario
			int tam = flujo_e.readInt();
			byte [] buff = new byte[tam];
			flujo_e.read(buff);
			String usuario = new String(buff, 0, tam, "UTF-8");
			usuario = usuario.toLowerCase();
			
			//nombre del canal
			tam = flujo_e.readInt();
			buff = new byte[tam];
			flujo_e.read(buff);
			String canal = new String(buff, 0, tam, "UTF-8");
			
			//crear dao
			UsuarioDAO usuarioDAO = new HibernateUsuarioDAO(this.managerApp);
			CanalDAO canalDAO = new HibernateCanalDAO(this.managerApp);
			SuscripcionDAO suscripcionDAO = new HibernateSuscripcionDAO(this.managerApp);
			
			//comprobar si existe el canal
			if (!canalDAO.existeCanal(canal)) {
				//comprobar si el usuario esta suscrito al canal
				Canal c = canalDAO.getCanal(canal);
				
				if (usuarioDAO.existeUsuario(usuario) && suscripcionDAO.usuarioSuscrito(usuarioDAO.getUsuario(usuario), c)) {
					//obtener datos para la suscripcion
					Usuario u = usuarioDAO.getUsuario(usuario);
					Suscripcion s = suscripcionDAO.getSuscripcion(u, c);
					
					//suscribir usuario
					suscripcionDAO.desuscribir(s);
				} else {
					logg.error("Error, el usuario \"" + usuario + "\" ya está suscrito al canal \"" + canal + "\".");
					res = 3;
				}
			} else {
				logg.error("Error, el canal \"" + canal + "\" no existe.");
				res = 2;
			}
		} catch (IOException e) {
			logg.error("Error al recibir los datos.");
			return 1;
		}
		
		return res;
	}

}
