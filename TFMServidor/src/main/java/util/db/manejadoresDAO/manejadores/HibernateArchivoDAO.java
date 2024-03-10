package util.db.manejadoresDAO.manejadores;

import jakarta.persistence.EntityManagerFactory;
import util.db.AuxiliarDB;
import util.db.manejadoresDAO.interfaces.ArchivoDAO;
import util.db.modelos.Archivo;
import util.db.modelos.Canal;
import util.db.modelos.Usuario;

public class HibernateArchivoDAO implements ArchivoDAO {
	
	private EntityManagerFactory managerApp;

	public HibernateArchivoDAO(EntityManagerFactory managerApp) {
		this.managerApp = managerApp;
	}

	@Override
	public void guardarFichero(String fileName, String ruta, Usuario u, Canal c) {
		AuxiliarDB.inTransaction(entityManager -> {
			entityManager.persist(new Archivo(ruta, fileName, null, u, c));
		}, this.managerApp);
	}

	@Override
	public Archivo getFichero(String nombreFich) {
		// TODO Auto-generated method stub
		return null;
	}

}
