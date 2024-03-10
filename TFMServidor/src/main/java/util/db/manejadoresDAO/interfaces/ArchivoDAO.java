package util.db.manejadoresDAO.interfaces;

import util.db.modelos.Archivo;
import util.db.modelos.Canal;
import util.db.modelos.Usuario;

public interface ArchivoDAO {

	void guardarFichero(String fileName, String string, Usuario u, Canal c);

	Archivo getFichero(String nombreFich);

}
