package util.db.manejadoresDAO.manejadores;

import java.util.List;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import util.db.AuxiliarDB;
import util.db.manejadoresDAO.interfaces.SuscripcionDAO;
import util.db.modelos.Suscripcion;
import util.db.modelos.Usuario;
import util.db.modelos.Canal;

public class HibernateSuscripcionDAO implements SuscripcionDAO {
	
	private EntityManagerFactory managerApp;

	public HibernateSuscripcionDAO(EntityManagerFactory managerApp) {
		this.managerApp = managerApp;
	}

	@Override
	public boolean usuarioSuscrito(Usuario usuarioObj, Canal canalObj) {
		boolean [] test = {false};
		AuxiliarDB.inTransaction(entityManager -> {
			TypedQuery<Suscripcion> query = entityManager.createQuery("FROM Suscripcion as s WHERE s.usuario = :nombre AND s.canal = :canal", Suscripcion.class);
		    query.setParameter("nombre", usuarioObj);
		    query.setParameter("canal", canalObj);

		    List<Suscripcion> canales = query.getResultList();
		    
		    test[0] = !canales.isEmpty();	    
		}, this.managerApp);
		return test[0];
	}

	@Override
	public void suscribir(Usuario usuario, Canal canal, String socket) {
		AuxiliarDB.inTransaction(entityManager -> {
			entityManager.persist(new Suscripcion(usuario.getNombreUsuario() + "__" + canal.getNombreCanal(), usuario, canal, null, socket));
		}, this.managerApp);
	}

	@Override
	public void desuscribir(Suscripcion s) {
		AuxiliarDB.inTransaction(entityManager -> {
	        Suscripcion suscripcion = entityManager.find(Suscripcion.class, s.getSuscripcionID()); // Suponiendo que tienes un campo id en la clase Suscripcion
	        if (suscripcion != null) {
	            entityManager.remove(suscripcion);
	        }
	    }, this.managerApp);
	}

	@Override
	public Suscripcion getSuscripcion(Usuario u, Canal c) {
		Suscripcion [] test = new Suscripcion[1];
		AuxiliarDB.inTransaction(entityManager -> {
			TypedQuery<Suscripcion> query = entityManager.createQuery("FROM Suscripcion as s WHERE s.usuario = :nombre AND s.canal = :canal", Suscripcion.class);
		    query.setParameter("nombre", u);
		    query.setParameter("canal", c);

		    test[0] = query.getResultList().get(0);
		    
		}, this.managerApp);
		return test[0];
	}

}
