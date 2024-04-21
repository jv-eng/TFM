package util.db.manejadoresDAO.manejadores;

import java.util.List;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import util.db.AuxiliarDB;
import util.db.manejadoresDAO.interfaces.UsuarioDAO;
import util.db.modelos.Usuario;

public class HibernateUsuarioDAO implements UsuarioDAO {
	
	private EntityManagerFactory managerApp;

	public HibernateUsuarioDAO(EntityManagerFactory managerApp) {
		this.managerApp = managerApp;
	}

	@Override
	public void crearUsuario(String usuario, String correo, String pass) {
		AuxiliarDB.inTransaction(entityManager -> {
			entityManager.persist(new Usuario(usuario, correo, null));
		}, this.managerApp);
	}
	
	@Override
	public boolean existeUsuario(String correo) {
		boolean [] test = {false};
		AuxiliarDB.inTransaction(entityManager -> {
			TypedQuery<Usuario> query = entityManager.createQuery("SELECT u FROM Usuario u WHERE u.correoElectronico = :id", Usuario.class);
		    query.setParameter("id", correo);

		    List<Usuario> usuarios = query.getResultList();

		    test[0] = !usuarios.isEmpty();
		}, this.managerApp);
		return test[0];
	}
	
	@Override
	public boolean comprobarUsuarioId(String usuario) {
		boolean [] test = {false};
		AuxiliarDB.inTransaction(entityManager -> {
			TypedQuery<Usuario> query = entityManager.createQuery("SELECT u FROM Usuario u WHERE u.nombreUsuario = :id", Usuario.class);
		    query.setParameter("id", usuario);

		    List<Usuario> usuarios = query.getResultList();

		    test[0] = !usuarios.isEmpty();
		}, this.managerApp);
		return test[0];
	}

	@Override
	public Usuario getUsuario(String correo) {
		Usuario [] usuario = new Usuario[1];
		AuxiliarDB.inTransaction(entityManager -> {
			TypedQuery<Usuario> query = entityManager.createQuery("SELECT u FROM Usuario u WHERE u.correoElectronico = :id", Usuario.class);
		    query.setParameter("id", correo);

		    if (query.getResultList().isEmpty()) {
		    	usuario[0] = null;
		    } else {
		    	usuario[0] = query.getResultList().get(0);
		    }
		}, this.managerApp);
		return usuario[0];
	}

}
