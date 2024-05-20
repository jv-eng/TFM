package util.db.manejadoresDAO.interfaces;

import java.util.List;

import util.db.modelos.Archivo;
import util.db.modelos.Canal;
import util.db.modelos.Usuario;

public interface ArchivoDAO {

	void guardarFichero(String fileName, String path, Usuario u, Canal c);

	Archivo getFichero(String nombreFich);

	boolean getAll();
	
	List<String> getFicherosCanal(String canal);

}
