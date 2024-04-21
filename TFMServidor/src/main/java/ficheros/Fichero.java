package ficheros;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import jakarta.persistence.EntityManagerFactory;
import main.Main;
import util.db.manejadoresDAO.interfaces.ArchivoDAO;
import util.db.manejadoresDAO.interfaces.CanalDAO;
import util.db.manejadoresDAO.interfaces.SuscripcionDAO;
import util.db.manejadoresDAO.interfaces.UsuarioDAO;
import util.db.manejadoresDAO.manejadores.HibernateArchivoDAO;
import util.db.manejadoresDAO.manejadores.HibernateCanalDAO;
import util.db.manejadoresDAO.manejadores.HibernateSuscripcionDAO;
import util.db.manejadoresDAO.manejadores.HibernateUsuarioDAO;
import util.db.modelos.Archivo;
import util.db.modelos.Canal;
import util.db.modelos.Suscripcion;
import util.db.modelos.Usuario;

//gestionar el tema de almacenamiento en esta clase o en otras
public class Fichero {
	
	//logger
	private static final Logger logg = (Logger) LogManager.getLogger("com.tfm.app");
	
	private static final String ruta = "F:\\descargas_tfm\\";
	
	private EntityManagerFactory managerApp;
	private Socket socket;
	
	public Fichero(EntityManagerFactory entityManagerFactoryApp, Socket socket_sr) {
		this.managerApp = entityManagerFactoryApp;
		this.socket = socket_sr;
	}

	public int enviarFichero() {
		int res = 0;
		System.out.println("iniciamos envio fichero");
		try {
			DataInputStream flujo_e = new DataInputStream(this.socket.getInputStream());
			
			//obtener usuario
			int tam = flujo_e.readInt();
			byte [] buff = new byte[tam];
			flujo_e.read(buff);
			String usuario = new String(buff, 0, tam, "UTF-8");
			usuario = usuario.toLowerCase();
			System.out.println("usuario: " + usuario);
			
			//nombre del canal
			tam = flujo_e.readInt();
			buff = new byte[tam];
			flujo_e.read(buff);
			String canal = new String(buff, 0, tam, "UTF-8");
			System.out.println("canal: " + canal);
			
			//crear los dao
			UsuarioDAO usuarioDAO = new HibernateUsuarioDAO(this.managerApp);
			CanalDAO canalDAO = new HibernateCanalDAO(this.managerApp);
			ArchivoDAO archivoDAO = new HibernateArchivoDAO(this.managerApp);
			
			//comprobar que existen usuario y canal
			System.out.println("existe usuario: " + usuarioDAO.existeUsuario(usuario));
			if (usuarioDAO.existeUsuario(usuario)) {
				if (canalDAO.existeCanal(canal)) {
					Canal c = canalDAO.getCanal(canal);
					Usuario u = usuarioDAO.getUsuario(usuario);
					System.out.println("usuario --> " + u.getCorreoElectronico());
					System.out.println("canal --> " + c.getNombreCanal());
					
					//comprobar que el usuario es el creador del canal
					if (c.getCreador().getCorreoElectronico().compareToIgnoreCase(usuario) == 0) {
						//recibir fichero
						String fileName = this.recibirFicheroCompleto(socket, flujo_e);
						System.out.println("Fichero recibido");
						//guardar datos
						archivoDAO.guardarFichero(fileName, ruta + fileName, u, c);
						System.out.println(">>>>>>>" + fileName + "\t" +  ruta + fileName + "\t" + u.getCorreoElectronico() + "\t" + c.getNombreCanal());
						
						//notificar usuarios
						this.notificarUsuarios(c, fileName, canalDAO);
					} else {
						logg.error("Error, el usuario \"" + usuario + "\" no es el creador del canal \"" + canal + "\".");
						res = 4;
					}
				} else {
					logg.error("Error, el canal \"" + canal+ "\" no existe.");
					res = 3;
				}
			} else {
				logg.error("Error, el usuario \"" + usuario + "\" no existe.");
				res = 2;
			}
			
		} catch (IOException e) {
			logg.error("Error al recibir los datos.");
			return 1;
		}
		
		return res;	
	}

	public int recibirFichero() {
		return 0;
	}

	public int descargarFichero() {
		int res = 0;
		System.out.println("Descarga de fichero");
		try {
			DataInputStream flujo_e = new DataInputStream(this.socket.getInputStream());
			
			//obtener usuario
			int tam = flujo_e.readInt();
			byte [] buff = new byte[tam];
			flujo_e.read(buff);
			String usuario = new String(buff, 0, tam, "UTF-8");
			usuario = usuario.toLowerCase();
			System.out.println("usuario: " + usuario);
			
			//nombre del canal
			tam = flujo_e.readInt();
			buff = new byte[tam];
			flujo_e.read(buff);
			String canal = new String(buff, 0, tam, "UTF-8");
			System.out.println("canal: " + canal);
			
			//nombre del fichero
			tam = flujo_e.readInt();
			buff = new byte[tam];
			flujo_e.read(buff);
			String nombreFich = new String(buff, 0, tam, "UTF-8");
			System.out.println("nombre fichero: " + nombreFich);
			
			//crear DAO
			UsuarioDAO usuarioDAO = new HibernateUsuarioDAO(this.managerApp);
			CanalDAO canalDAO = new HibernateCanalDAO(this.managerApp);
			ArchivoDAO archivoDAO = new HibernateArchivoDAO(this.managerApp);
			SuscripcionDAO suscripcionDAO = new HibernateSuscripcionDAO(this.managerApp);
			
			//comprobar que existen canal y usuario
			if (canalDAO.existeCanal(canal) && usuarioDAO.existeUsuario(usuario)) {
				//obtener objetos
				Canal c = canalDAO.getCanal(canal);
				Usuario u = usuarioDAO.getUsuario(usuario);
				
				System.out.println("usuario --> " + u.getCorreoElectronico());
				System.out.println("canal --> " + c.getNombreCanal());
				
				//comprobar que el usuario esta suscrito al canal
				if (suscripcionDAO.usuarioSuscrito(u, c)) {
					//obtener fichero
					Archivo a = archivoDAO.getFichero(nombreFich);
					System.out.println(a.getRutaSistemaArchivos());
					//enviar fichero
					File f = new File(a.getRutaSistemaArchivos());
					this.enviarFicheroCompleto(socket, a.getRutaSistemaArchivos(), f.length());
				} else {
					logg.error("Error, el usuario \"" + usuario + "\" no está suscrito al canal \"" + canal + "\".");
					res = 3;
				}
			} else {
				logg.error("Error, el usuario \"" + usuario + "\" no existe o el canal \"" + canal + "\" no existe.");
				res = 2;
			}
			
		} catch (IOException e) {
			logg.error("Error al recibir los datos.");
			return 1;
		}
		
		return res;
	}
	
	private String recibirFicheroCompleto(Socket socket, DataInputStream flujo_e) {
		try {			
			//obtener nombre
			int tam = flujo_e.readInt();
			byte [] buff = new byte[tam];
			flujo_e.read(buff);
			String nombre = new String(buff, 0, tam, "UTF-8");
			nombre = nombre.toLowerCase();
			System.out.println("nombre fichero: " + nombre);
			//String rutaFichero = ruta  + nombre;
			
			//recibir longitud
			long num_recibido = flujo_e.readLong();
			System.out.println("Tamaño fichero: " + num_recibido);
			
			//recibir mensaje
			buff = new byte[64000];
			
			FileOutputStream fich = new FileOutputStream(ruta + nombre);
			int bytes_leidos = 0;
			long bytes_acumulados = 0;
			
			do {
				bytes_leidos = flujo_e.read(buff);
				bytes_acumulados += bytes_leidos;
				fich.write(buff,0,bytes_leidos);
				System.out.println("Recibiendo Fichero ...");
				System.out.println("longitud enviada fich: " + bytes_acumulados);
				System.out.println("NumBytesLeidos "+ bytes_leidos);
			} while (bytes_acumulados < num_recibido);
			
			fich.close();
			System.out.println("terminamos, volvemos");
			
			return nombre;
		} catch (IOException e) {
			logg.error("Error al recibir el fichero.");
			return null;
		}
		
	}
	
	private void enviarFicheroCompleto(Socket socket, String rutaFichero, long size) {
		byte [] buff;
		try {
			DataOutputStream flujo_out = new DataOutputStream(socket.getOutputStream());
			
			//enviar longitud
			flujo_out.writeLong(size);
			System.out.println("enviamos tamaño fichero");
			//obtener nombre
			if (size > 64000) buff = new byte[64000];
			else buff = new byte[(int) size];
			
			FileInputStream fich_stream = new FileInputStream(rutaFichero);
			
			long num_total = 0;
			int bytes_leidos = 0;
			
			do {
				bytes_leidos = fich_stream.read(buff);
				num_total += bytes_leidos;
				flujo_out.write(buff, 0, bytes_leidos);
				System.out.println("NumBytesLeidos "+ bytes_leidos );
				System.out.println("longitud enviada fich: "+ num_total );
			} while(num_total < size);
			
			//cerrar
			fich_stream.close();
		} catch (IOException e) {
			logg.error("Error al recibir el fichero.");
		}
	}
	
	private void notificarUsuarios(Canal c, String fileName, CanalDAO canalDAO) {
		System.out.println("notificando a usuarios");
		
		try {
			//obtener lista de usuarios suscritos al canal
			List<Suscripcion> usuariosSuscritos = canalDAO.getUsuariosSuscritos(c);
			System.out.println(usuariosSuscritos.size());
			
			//notificar canal y nombre del fichero
			if (!usuariosSuscritos.isEmpty()) {
				for (Socket sock: Main.mapa.get(c.getNombreCanal())) {
					DataOutputStream flujo_out = new DataOutputStream(sock.getOutputStream());
					//enviar canal
					flujo_out.writeInt(c.getNombreCanal().getBytes().length);
					flujo_out.write(c.getNombreCanal().getBytes());
					//enviar nombre del fichero
					flujo_out.writeInt(fileName.getBytes().length);
					flujo_out.write(fileName.getBytes());
					System.out.println("fichero enviado");
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error, no hay cliente");
		}
	}
}
