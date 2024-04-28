package util.db.manejadoresDAO.manejadores;

import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import util.db.AuxiliarDB;
import util.db.manejadoresDAO.interfaces.CanalDAO;
import util.db.modelos.Archivo;
import util.db.modelos.Canal;
import util.db.modelos.Suscripcion;
import util.db.modelos.Usuario;

public class HibernateCanalDAO implements CanalDAO {
	
	private EntityManagerFactory managerApp;

	public HibernateCanalDAO(EntityManagerFactory managerApp) {
		this.managerApp = managerApp;
	}

	//comprobar si existe canal
	@Override
	public boolean existeCanal(String canal) {
		boolean [] test = {false};
		AuxiliarDB.inTransaction(entityManager -> {
			TypedQuery<Canal> query = entityManager.createQuery("SELECT c FROM Canal c WHERE c.nombreCanal = :nombre", Canal.class);
		    query.setParameter("nombre", canal);

		    List<Canal> canales = query.getResultList();
		    
		    test[0] = !canales.isEmpty();	    
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

		    if (query.getResultList().isEmpty()) c[0] = null;
		    else c[0] = query.getResultList().get(0);
		}, this.managerApp);
		return c[0];
	}

	@Override
	public List<Suscripcion> getUsuariosSuscritos(Canal c) {
		List<Suscripcion> res = new LinkedList<Suscripcion>();
		AuxiliarDB.inTransaction(entityManager -> {
			TypedQuery<Suscripcion> query = entityManager.createQuery(
					"SELECT u FROM Suscripcion u where u.canal.nombreCanal = :nombreCanal", Suscripcion.class);
		    query.setParameter("nombreCanal", c.getNombreCanal());
			System.out.println("tamaño lista " + query.getResultList().size());
		    for (Suscripcion s: query.getResultList()) {
		    	res.add(s);
		    }
		}, this.managerApp);
		return res;
	}

	@Override
	public List<String> getArchivosCanal(String nombreCanal) {
        List<String> nombresArchivos = new LinkedList<>();

        AuxiliarDB.inTransaction(entityManager -> {
            TypedQuery<Archivo> query = entityManager.createQuery(
                    "SELECT a FROM Archivo a WHERE a.canal.nombreCanal = :nombreCanal", Archivo.class);
            query.setParameter("nombreCanal", nombreCanal);

            List<Archivo> archivos = query.getResultList();
            for (Archivo archivo : archivos) {
                nombresArchivos.add(archivo.getNombreArchivo());
            }
        }, this.managerApp);

        return nombresArchivos;
    }
	


}
