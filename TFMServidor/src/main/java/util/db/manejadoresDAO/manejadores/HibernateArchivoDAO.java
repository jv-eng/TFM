package util.db.manejadoresDAO.manejadores;

import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import util.db.AuxiliarDB;
import util.db.manejadoresDAO.interfaces.ArchivoDAO;
import util.db.modelos.Archivo;
import util.db.modelos.Canal;
import util.db.modelos.Suscripcion;
import util.db.modelos.Usuario;

public class HibernateArchivoDAO implements ArchivoDAO {
	
	private EntityManagerFactory managerApp;

	public HibernateArchivoDAO(EntityManagerFactory managerApp) {
		this.managerApp = managerApp;
	}

	@Override
	public void guardarFichero(String fileName, String ruta, Usuario u, Canal c) {
		AuxiliarDB.inTransaction(entityManager -> {
			entityManager.persist(new Archivo(fileName, ruta, null, u, c));
		}, this.managerApp);
	}

	@Override
	public Archivo getFichero(String nombreFich) {
		Archivo [] c = new Archivo[1];
		AuxiliarDB.inTransaction(entityManager -> {
			TypedQuery<Archivo> query = entityManager.createQuery("SELECT a FROM Archivo a WHERE a.nombreArchivo = :id", Archivo.class);
		    query.setParameter("id", nombreFich);

		    c[0] = query.getResultList().get(0);
		}, this.managerApp);
		return c[0];
	}
	
	@Override
	public boolean getAll() {
		boolean [] c = {false};
		AuxiliarDB.inTransaction(entityManager -> {
			TypedQuery<Archivo> query = entityManager.createQuery("SELECT a FROM Archivo a", Archivo.class);

			c[0] = query.getResultList().isEmpty();
		}, this.managerApp);
		return c[0];
	}
	
	@Override
	public List<String> getFicherosCanal(String canal) {
		List<String> lista = new LinkedList<String>();
		
		AuxiliarDB.inTransaction(entityManager -> {
			TypedQuery<Archivo> query = entityManager.createQuery("SELECT a FROM Archivo a WHERE a.canal.nombreCanal = :canal", Archivo.class);
			query.setParameter("canal", canal);

			for (Archivo a: query.getResultList()) {
				lista.add(a.getNombreArchivo());
			}
		}, this.managerApp);
		
		return lista;
	}
	
}
