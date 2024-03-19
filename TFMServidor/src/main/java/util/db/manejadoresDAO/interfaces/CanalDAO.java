package util.db.manejadoresDAO.interfaces;

import java.util.List;

import util.db.modelos.Canal;
import util.db.modelos.Suscripcion;
import util.db.modelos.Usuario;

public interface CanalDAO {

	boolean existeCanal(String canal);

	void crearCanal(Usuario usuario, String canal);

	Canal getCanal(String canal);

	List<Suscripcion> getUsuariosSuscritos(Canal c);

}
