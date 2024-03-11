package util.db.manejadoresDAO.manejadores;

import java.util.List;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import util.db.AuxiliarDB;
import util.db.manejadoresDAO.interfaces.UsuarioCredencialesDAO;
import util.db.modelos.UsuarioCredenciales;

public class HibernateUsuarioCredencialesDAO implements UsuarioCredencialesDAO {
	
	private EntityManagerFactory managerUsuario;

	public HibernateUsuarioCredencialesDAO(EntityManagerFactory managerUsuario) {
		this.managerUsuario = managerUsuario;
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
			entityManager.persist(new UsuarioCredenciales(usuario, pass, pass, correo));
		}, this.managerUsuario);
	}

	@Override
	public void insertarClave(String correo, String clave) {
		AuxiliarDB.inTransaction(entityManager -> {
		    TypedQuery<UsuarioCredenciales> query = entityManager.createQuery("FROM UsuarioCredenciales u WHERE u.correo = :mail", UsuarioCredenciales.class);
		    query.setParameter("mail", correo);

		    List<UsuarioCredenciales> usuarios = query.getResultList();
		    System.out.println(usuarios.size());

		    if (!usuarios.isEmpty()) {
		        // El usuario ya existe, actualiza la clave
		        UsuarioCredenciales usuarioCredenciales = usuarios.get(0);

		        // Actualizar la entidad con la nueva clave
		        usuarioCredenciales.setClave(clave);
		    }

		}, this.managerUsuario);
		
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
		    	String pass = usuarios.get(0).getPass();
		    	//hay que descrifrar o algo
		    	System.out.println(pass);
		    	//comprobamos
		    	test[0] = pass.compareToIgnoreCase(contraseña)==0;
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

}
