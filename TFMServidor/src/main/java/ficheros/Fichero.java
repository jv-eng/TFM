package ficheros;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import jakarta.persistence.EntityManagerFactory;
import main.Main;
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

//gestionar el tema de almacenamiento en esta clase o en otras
public class Fichero {
	
	//logger
	private static final Logger logg = (Logger) LogManager.getLogger("com.tfm.app");
	private static final Logger loggSig = (Logger) LogManager.getLogger("com.tfm.digital_signature");
	
	private static final String ruta = "F:\\descargas_tfm\\";
	
	private EntityManagerFactory managerApp;
	private EntityManagerFactory entityManagerFactoryCredenciales;
	private Socket socket;
	
	public Fichero(EntityManagerFactory entityManagerFactoryApp, EntityManagerFactory entityManagerFactoryCredenciales, 
			Socket socket_sr) {
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
						System.out.println("Nombre recibido");
						
						//guardar datos
						archivoDAO.guardarFichero(fileName, ruta + fileName, u, c);
						System.out.println(">>>>>>>" + fileName + "\t" +  ruta + fileName + "\t" + u.getCorreoElectronico() + "\t" + c.getNombreCanal());
						
						loggSig.info("Recepción de fichero \"" + fileName + "\" de usuario \"" + usuario + "\" por canal \"" + canal + "\".");

						
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
			
			System.out.println(nombre);
			
			//recibir longitud
			long num_recibido = flujo_e.readLong();
			
			//obtener clave
			//PublicKey key = FirmaDigitalUtil.obtenerClaveCliente(correo, entityManagerFactoryCredenciales);
			PublicKey key = 
					Serializar.stringClave((new HibernateUsuarioCredencialesDAO(entityManagerFactoryCredenciales)
							.getClave(correo)));
			Signature verifier = Signature.getInstance("SHA512withRSA");
            verifier.initVerify(key);
            System.out.println(key);
			
			//recibir mensaje
            if (num_recibido < 64000) buff = new byte[64000];
            else buff = new byte[(int) num_recibido];
			
			FileOutputStream fich = new FileOutputStream(ruta + nombre);
			int bytes_leidos = 0;
			long bytes_acumulados = 0;
			
			System.out.println("empezamos a recibir " + num_recibido);
			
			do {
				System.out.println("leer del buffer");
				bytes_leidos = flujo_e.read(buff, 0, (int) Math.min(buff.length, num_recibido - bytes_acumulados));
				bytes_acumulados += bytes_leidos;
				fich.write(buff,0,bytes_leidos);
				verifier.update(buff, 0, bytes_leidos);
				System.out.println("despues");
			} while (bytes_acumulados < num_recibido);
			
			fich.close();
			
			System.out.println(bytes_acumulados + "\t" + num_recibido);
			System.out.println("recibir firma y comprobar");
			//recibir firma
			tam = flujo_e.readInt();
			System.out.println("tam firma: " + tam);
			buff = new byte[tam];
			System.out.println("leemos la firma");
			flujo_e.read(buff);
			
			//comprobamos firma
			boolean verificado = verifier.verify(buff);
            if (verificado) {
            	loggSig.info("Firma correcta.");
                System.out.println("Firma verificada correctamente");
            } else {
            	loggSig.error("Se ha producido un error en la firma.");
                System.out.println("Firma no válida");
            }
			
			System.out.println("Fichero enviado");
			
			
			return nombre;
		} catch (IOException e) {
			logg.error("Error al recibir el fichero.");
			return null;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
			//PrivateKey key = FirmaDigitalUtil.getFirmaCertServidor();
			PrivateKey key = Serializar.stringClavePriv("MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCHQwjGWFRL5DEC/OiWNAUYhRq2iu+YxXMSjt+g9t231VJVNuaWz1hEIbXHe9TVVnAOxK67vja4sdQUN9stVmkK/b5NW8db2royJO384AJs3QPqcPHhE8eF71+4NvEe54qDPWE5nscaINyn6Yeev3xiMprsss6ZF30S1/Bd3pI87p0NZlOpiHioUHAye1aRs5T5dofxom+5YcVOpJVe3PqjP+kjf31cIgk+tja2H5nd6m95QpLaonj5CcyJmqInckT8KmrDA6zP2YXlyiz7OxxyL33l0xO0WIVieZXYbpKgW+D6/nZ9/mlazhyee1GpgxREdm60dgrELgrjPU2l20q/AgMBAAECggEAJEVfhwCMqkUot2pmXFDaxPZ8cVLUaR0ZfHJ/mURZtEAp8dvk/a/gNXTsr++O5R5msi8fxAx/1AsrmbhYt9GiHWZpr4ja7tC6eVPZ1aOkQtkJgcaDth19tXa5LkbdZbIlPIgrGC5xqZoCUayXx4QFsLdtP+e66uaXjYQGWsWiZWAbQj+qNaVJAGltTSNl3mPKnpg5bCEp+gSBjfIEB4hU2GDvWKnI5Ma6FYF2Zu0Uo891PXik9Kt88KHYR4vlwsAKR9TwNJRkfLxGRPnpkdBkiFQzdhQaaAueQGvWjIeKRvpyLcY5HerbfBC0SiPXhp5hQVI7N30BObgbt1s90N6T0QKBgQC8ov93EZFq9Fq5z9sU1aCElgQDBRfPe5tpkGAt3DDG7qJJd/OVCOpNmPsOm6qjYiM5JJBq0Xcu2qWgOTHR0iNRwXNjLohBQIyocNJCg/yhQCeOMrvjLQAo6NThi4w8S3bk7GT3GCswC+gvoP4PZRa5dHMbE6Kq5Pkxltot2ya7rQKBgQC3kI3HKTv5mt6Gp9aMO7caQl42bpY/tA035uLdP09Jzs9yX2ctErEIwvWDZ0pnihTeN1wWpyB8jD514tz7Hx9Fnz0LlBDGPMCv34mqQjETFt4S6x8bYBJ+YvJcfDm6MftBVZfaaQBGDjBbmXh05TcDJyGH3pYf4hm0RuVk5VttmwKBgFA+qpwVPh4YeqvGrzTKt7EPO/+o3/skYvViNHftzlYh16mXPGhu1XVTGaGaONmt+rvpQQIfvyqQWpqxGe9fDCVQPOy5M79GXU+eRuOC0CosZ2dHT8QRNZsxiLW1rl9L3vT9VuoCPwT+W7Q/MTSNVUBpODoRfUZjh1pACOou6ug1AoGBAIj3LxKfzcRcrhVDPm68T88kHi/3K9y0d/hyKmxzRLIJwffQ/6c9/yJOdepqM7Y17YQxQmEUqsTD0AceE0y82BSW0HHHNQz1X8Daxllnsj5QHbt3/GnssV/kbHpdpqUrjhIdz4SEPFQJWkO+q4ZtUTLlC7vZdaKsjglZrOgvypxpAoGAc9ecOGYe8ooeyLSiye+ybZxrmzsnP0o6PBMG0RYZd+Gq6fBskZovL2Fti0wewBNDa575bXWJa79qC5+ejnHaFdLYMIfRGk2oJW1rXGPGUKUyv7i3ROasdjBREPoXEMvsaDf6L2lrEsMFMXfGPK8CIaVtIu7hxgOwfGQtCAqyPIc=");
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
			
		} catch (IOException e) {
			logg.error("Error al recibir el fichero.");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error, no hay cliente");
		}
	}
}
