package manejadores;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;

import javax.net.ssl.SSLSocket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import jakarta.persistence.EntityManagerFactory;
import main.Main;
import util.Configuration;
import util.FirmaDigitalUtil;
import util.Serializar;
import util.db.manejadoresDAO.interfaces.ArchivoDAO;
import util.db.manejadoresDAO.interfaces.CanalDAO;
import util.db.manejadoresDAO.interfaces.SuscripcionDAO;
import util.db.manejadoresDAO.interfaces.UsuarioDAO;
import util.db.manejadoresDAO.manejadores.HibernateArchivoDAO;
import util.db.manejadoresDAO.manejadores.HibernateCanalDAO;
import util.db.manejadoresDAO.manejadores.HibernateSuscripcionDAO;
import util.db.manejadoresDAO.manejadores.HibernateUsuarioCredencialesDAO;
import util.db.manejadoresDAO.manejadores.HibernateUsuarioDAO;
import util.db.modelos.Archivo;
import util.db.modelos.Canal;
import util.db.modelos.Suscripcion;
import util.db.modelos.Usuario;


public class ManejadorFichero {
	
	//logger
	private static final Logger logg = (Logger) LogManager.getLogger("com.tfm.app");
	private static final Logger loggSig = (Logger) LogManager.getLogger("com.tfm.digital_signature");
	
	private static final String ruta = Configuration.obtenerConfiguracion("dirDescargas");
	
	private EntityManagerFactory managerApp;
	private EntityManagerFactory entityManagerFactoryCredenciales;
	private SSLSocket socket;
		
	public ManejadorFichero(EntityManagerFactory entityManagerFactoryApp, EntityManagerFactory entityManagerFactoryCredenciales, 
			SSLSocket socket_sr) {
		this.managerApp = entityManagerFactoryApp;
		this.entityManagerFactoryCredenciales = entityManagerFactoryCredenciales;
		this.socket = socket_sr;
	}

	public int enviarFichero() {
		int res = 0;
		System.out.println("Iniciamos envio fichero: envío de nombre");
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
			
			//crear los dao
			UsuarioDAO usuarioDAO = new HibernateUsuarioDAO(this.managerApp);
			CanalDAO canalDAO = new HibernateCanalDAO(this.managerApp);
			ArchivoDAO archivoDAO = new HibernateArchivoDAO(this.managerApp);
			
			//comprobar que existen usuario y canal
			if (usuarioDAO.existeUsuario(usuario)) {
				if (canalDAO.existeCanal(canal)) {
					Canal c = canalDAO.getCanal(canal);
					Usuario u = usuarioDAO.getUsuario(usuario);
					
					//comprobar que el usuario es el creador del canal
					if (c.getCreador().getCorreoElectronico().compareToIgnoreCase(usuario) == 0) {
						//recibir fichero
						String fileName = this.recibirFicheroCompleto(socket, usuario, flujo_e);
						
						if (fileName == null) {
							res = 5; //error de firma
						} else {
							System.out.println("Nombre recibido");
							
							//guardar datos
							archivoDAO.guardarFichero(fileName, ruta + fileName, u, c);
							System.out.println(">>>>>>>" + fileName + "\t" +  ruta + fileName + "\t" + u.getCorreoElectronico() + "\t" + c.getNombreCanal());
							
							loggSig.info("Recepción de fichero \"" + fileName + "\" de usuario \"" + usuario + "\" por canal \"" + canal + "\".");

							//notificar usuarios
							this.notificarUsuarios(c, fileName, canalDAO);
						}
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

	public int descargarFichero() {
		int res = 0;
		System.out.println("Envío de fichero: envío de fichero al cliente");
		try {
			DataInputStream flujo_e = new DataInputStream(this.socket.getInputStream());
			
			//obtener usuario
			int tam = flujo_e.readInt();
			byte [] buff = new byte[tam];
			flujo_e.read(buff);
			String usuario = new String(buff, 0, tam, "UTF-8");
			usuario = usuario.toLowerCase().trim();
			
			//nombre del canal
			tam = flujo_e.readInt();
			buff = new byte[tam];
			flujo_e.read(buff);
			String canal = new String(buff, 0, tam, "UTF-8");
			
			//nombre del fichero
			tam = flujo_e.readInt();
			buff = new byte[tam];
			flujo_e.read(buff);
			String nombreFich = new String(buff, 0, tam, "UTF-8");
			
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
				
				//comprobar que el usuario esta suscrito al canal
				if (suscripcionDAO.usuarioSuscrito(u, c)) {
					//obtener fichero
					Archivo a = archivoDAO.getFichero(nombreFich);
					
					//enviar fichero
					File f = new File(a.getRutaSistemaArchivos());
					loggSig.info("Envío de fichero \"" + nombreFich + "\" a usuario \"" + usuario + "\" por canal \"" + canal + "\".");
					System.out.println("Envío de fichero \"" + nombreFich + "\" a usuario \"" + usuario + "\" por canal \"" + canal + "\".");
					
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
	
	private String recibirFicheroCompleto(Socket socket, String correo, DataInputStream flujo_e) {
		try {			
			//obtener nombre
			int tam = flujo_e.readInt();
			byte [] buff = new byte[tam];
			flujo_e.read(buff);
			String nombre = new String(buff, 0, tam, "UTF-8");
			
			System.out.println("fichero: " + nombre);
			
			//recibir longitud
			long num_recibido = flujo_e.readLong();
			
			//obtener clave
			PublicKey key = 
					Serializar.stringClave((new HibernateUsuarioCredencialesDAO(entityManagerFactoryCredenciales)
							.getClave(correo)));
			Signature verifier = Signature.getInstance("SHA512withRSA");
            verifier.initVerify(key);
			
			//recibir mensaje
            if (num_recibido < 64000) buff = new byte[64000];
            else buff = new byte[(int) num_recibido];
			
			FileOutputStream fich = new FileOutputStream(ruta + nombre);
			int bytes_leidos = 0;
			long bytes_acumulados = 0;
			
			do {
				bytes_leidos = flujo_e.read(buff, 0, (int) Math.min(buff.length, num_recibido - bytes_acumulados));
				bytes_acumulados += bytes_leidos;
				fich.write(buff,0,bytes_leidos);
				verifier.update(buff, 0, bytes_leidos);
			} while (bytes_acumulados < num_recibido);
			
			fich.close();
			
			System.out.println("recibir firma y comprobar");
			//recibir firma
			tam = flujo_e.readInt();
			buff = new byte[tam];
			flujo_e.read(buff);
			
			//comprobamos firma
			boolean verificado = verifier.verify(buff);
            if (verificado) {
            	loggSig.info("Firma correcta usuario \"" + correo + "\".");
                System.out.println("Firma verificada correctamente");
            } else {
            	loggSig.error("Se ha producido un error en la firma. Fichero: \"" + nombre + "\". Usuario: \"" + correo + "\".");
            	File f = new File(ruta + nombre);
            	if (f.exists()) f.delete();
                System.out.println("Firma no válida");
                return null;
            }
			
			return nombre;
		} catch (Exception e) {
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
			
			//obtener nombre
			if (size > 64000) buff = new byte[64000];
			else buff = new byte[(int) size];
			
			//obtener clave y firma
			PrivateKey key = FirmaDigitalUtil.getFirmaCertServidor();
			Signature sig = Signature.getInstance("SHA512withRSA");
            sig.initSign(key);
			
			FileInputStream fich_stream = new FileInputStream(rutaFichero);
			
			long num_total = 0;
			int bytes_leidos = 0;
			
			do {
				bytes_leidos = fich_stream.read(buff);
				num_total += bytes_leidos;
				flujo_out.write(buff, 0, bytes_leidos);
				sig.update(buff, 0, bytes_leidos);
			} while(num_total < size);
			
			//cerrar
			fich_stream.close();
			
			//enviar firma
			byte[] firma = sig.sign();
            flujo_out.writeInt(firma.length);
            flujo_out.write(firma);
            System.out.println("firma enviada: " + firma.length);
			
		} catch (Exception e) {
			logg.error("Error al recibir el fichero.");
			System.out.println("Error al recibir fichero");
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
					//eliminar socket si ya no está activo
					if (sock.isClosed() || !sock.isConnected() || sock.isInputShutdown() || sock.isOutputShutdown()) {
						Main.mapa.get(c.getNombreCanal()).remove(sock);
						continue;
					}
					
					//el socket sigue activo, enviamos notificacion
					DataOutputStream flujo_out = new DataOutputStream(sock.getOutputStream());
					
					//enviar canal
					flujo_out.writeInt(c.getNombreCanal().getBytes().length);
					flujo_out.write(c.getNombreCanal().getBytes());
					
					//enviar nombre del fichero
					flujo_out.writeInt(fileName.getBytes().length);
					flujo_out.write(fileName.getBytes());
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error, no hay cliente");
		}
	}
}
