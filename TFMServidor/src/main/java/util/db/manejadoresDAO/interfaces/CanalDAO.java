package util.db.manejadoresDAO.interfaces;

import util.db.modelos.Usuario;

public interface CanalDAO {

	boolean comprobarCanal(String canal);

	void crearCanal(Usuario usuario, String canal);

}
