package util.db.manejadoresDAO.manejadores;

import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import util.Configuration;
import util.db.AuxiliarDB;
import util.db.manejadoresDAO.interfaces.UsuarioCredencialesDAO;
import util.db.modelos.UsuarioCredenciales;

public class HibernateUsuarioCredencialesDAO implements UsuarioCredencialesDAO {
	
	private EntityManagerFactory managerUsuario;
	private String claveCifrado;

	public HibernateUsuarioCredencialesDAO(EntityManagerFactory managerUsuario) {
		this.managerUsuario = managerUsuario;
		this.claveCifrado = Configuration.obtenerConfiguracion("claveContraseña");
	}

	@Override
	public boolean comprobarUsuario(String usuario) {
		boolean [] test = {false};
		AuxiliarDB.inTransaction(entityManager -> {
			TypedQuery<UsuarioCredenciales> query = entityManager.createQuery("SELECT u FROM UsuarioCredenciales u WHERE u.id = :id", UsuarioCredenciales.class);
		    query.setParameter("id", usuario);

		    List<UsuarioCredenciales> usuarios = query.getResultList();

		    test[0] = !usuarios.isEmpty();
		}, this.managerUsuario);
		return test[0];
	}

	@Override
	public void crearUsuario(String usuario, String correo, String pass) {
		AuxiliarDB.inTransaction(entityManager -> {
			try {
				entityManager.persist(new UsuarioCredenciales(usuario, cifrar(pass, claveCifrado), cifrar(pass, claveCifrado), correo));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, this.managerUsuario);
	}

	@Override
	public void insertarClave(String correo, String clave) {
		AuxiliarDB.inTransaction(entityManager -> {
		    TypedQuery<UsuarioCredenciales> query = entityManager.createQuery("FROM UsuarioCredenciales u WHERE u.correo = :mail", UsuarioCredenciales.class);
		    query.setParameter("mail", correo);

		    List<UsuarioCredenciales> usuarios = query.getResultList();

		    if (!usuarios.isEmpty()) {
		        // El usuario ya existe, actualiza la clave
		        UsuarioCredenciales usuarioCredenciales = usuarios.get(0);

		        // Actualizar la entidad con la nueva clave
		        usuarioCredenciales.setClave(clave);
		    }

		}, this.managerUsuario);
		
	}
	
	@Override
	public String getClave(String correo) {
		String [] res = {""};
		AuxiliarDB.inTransaction(entityManager -> {
			TypedQuery<UsuarioCredenciales> query = entityManager.createQuery("FROM UsuarioCredenciales as u WHERE u.correo = :correo", UsuarioCredenciales.class);
		    query.setParameter("correo", correo);

		    List<UsuarioCredenciales> usuarios = query.getResultList();
		    
		    if (!usuarios.isEmpty()) {
		    	//existe el usuario
		    	res[0] = usuarios.get(0).getClave();
			}
		    
		}, this.managerUsuario);
		return res[0];
	}
	

	@Override
	public boolean comprobarCredenciales(String correo, String contraseña) {
		boolean [] test = {false};
		AuxiliarDB.inTransaction(entityManager -> {
			TypedQuery<UsuarioCredenciales> query = entityManager.createQuery("FROM UsuarioCredenciales as u WHERE u.correo = :correo", UsuarioCredenciales.class);
		    query.setParameter("correo", correo);

		    List<UsuarioCredenciales> usuarios = query.getResultList();
		    
		    if (!usuarios.isEmpty()) {
		    	//existe el usuario
		    	String pass = null;
				try {
					pass = descifrar(usuarios.get(0).getPass(), claveCifrado);
				} catch (Exception e) {
					e.printStackTrace();
				}
		    	
		    	//comprobamos
		    	test[0] = pass.compareToIgnoreCase(contraseña) == 0;
			}
		    
		}, this.managerUsuario);
		return test[0];
	}
	
	@Override
	public void borrarClave(String correo) {
		AuxiliarDB.inTransaction(entityManager -> {
			TypedQuery<UsuarioCredenciales> query = entityManager.createQuery("FROM UsuarioCredenciales as u WHERE u.correo = :correo", UsuarioCredenciales.class);
		    query.setParameter("correo", correo);

		    List<UsuarioCredenciales> usuarios = query.getResultList();
		    
		    if (!usuarios.isEmpty()) {
		    	//existe el usuario
		    	usuarios.get(0).setClave("null");
			}
		    
		}, this.managerUsuario);
	}

	@Override
	public String getNombreUsuario(String correo) {
		String [] res = {""};
		AuxiliarDB.inTransaction(entityManager -> {
			TypedQuery<UsuarioCredenciales> query = entityManager.createQuery("FROM UsuarioCredenciales as u WHERE u.correo = :correo", UsuarioCredenciales.class);
		    query.setParameter("correo", correo);

		    List<UsuarioCredenciales> usuarios = query.getResultList();
		    
		    if (!usuarios.isEmpty()) {
		    	//existe el usuario
		    	res[0] = usuarios.get(0).getNombreUsuario();
			}
		    
		}, this.managerUsuario);
		return res[0];
	}
	
	private static String cifrar(String texto, String clave) throws Exception {
        SecretKeySpec key = new SecretKeySpec(clave.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] textoCifrado = cipher.doFinal(texto.getBytes());
        return Base64.getEncoder().encodeToString(textoCifrado);
    }

	private static String descifrar(String textoCifrado, String clave) throws Exception {
        SecretKeySpec key = new SecretKeySpec(clave.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] textoDescifrado = cipher.doFinal(Base64.getDecoder().decode(textoCifrado));
        return new String(textoDescifrado);
    }

}
