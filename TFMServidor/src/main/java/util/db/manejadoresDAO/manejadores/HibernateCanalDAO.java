package util.db.manejadoresDAO.manejadores;

import java.util.List;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import util.db.AuxiliarDB;
import util.db.manejadoresDAO.interfaces.CanalDAO;
import util.db.modelos.Canal;
import util.db.modelos.Usuario;

public class HibernateCanalDAO implements CanalDAO {
	
	private EntityManagerFactory managerApp;

	public HibernateCanalDAO(EntityManagerFactory managerApp) {
		this.managerApp = managerApp;
	}

	//comprobar si existe canal
	@Override
	public boolean comprobarCanal(String canal) {
		boolean [] test = {false};
		AuxiliarDB.inTransaction(entityManager -> {
			TypedQuery<Canal> query = entityManager.createQuery("FROM Canal as c WHERE c.nombreCanal = :nombre", Canal.class);
		    query.setParameter("nombre", canal);

		    List<Canal> canales = query.getResultList();
		    
		    test[0] = canales.isEmpty();	    
		}, this.managerApp);
		return test[0];
	}

	@Override
	public void crearCanal(Usuario usuario, String canal) {
		AuxiliarDB.inTransaction(entityManager -> {
			entityManager.persist(new Canal(canal, usuario));
		}, this.managerApp);
	}

	@Override
	public Canal getCanal(String canal) {
		Canal [] c = new Canal[1];
		AuxiliarDB.inTransaction(entityManager -> {
			TypedQuery<Canal> query = entityManager.createQuery("SELECT u FROM Canal u WHERE u.nombreCanal = :id", Canal.class);
		    query.setParameter("id", canal);

		    c[0] = query.getResultList().get(0);
		}, this.managerApp);
		return c[0];
	}

}
