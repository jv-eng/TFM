package util.db.manejadoresDAO.manejadores;

import jakarta.persistence.EntityManagerFactory;
import util.db.manejadoresDAO.interfaces.UsuarioDAO;
import util.db.modelos.Usuario;

public class HibernateUsuarioDAO implements UsuarioDAO {
	
	private EntityManagerFactory managerApp;

	public HibernateUsuarioDAO(EntityManagerFactory managerApp) {
		this.managerApp = managerApp;
	}

	@Override
	public void guardarUsuario(Usuario usuario) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Usuario obtenerUsuarioPorId(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void crearUsuario(String usuario, String correo, String pass) {
		// TODO Auto-generated method stub
		
	}

}
