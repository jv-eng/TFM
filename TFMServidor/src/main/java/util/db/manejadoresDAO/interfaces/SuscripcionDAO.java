package util.db.manejadoresDAO.interfaces;

import util.db.modelos.Canal;
import util.db.modelos.Suscripcion;
import util.db.modelos.Usuario;

public interface SuscripcionDAO {

	boolean usuarioSuscrito(Usuario usuarioObj, Canal canalObj);

	void suscribir(Usuario u, Canal c, String sock);

	void desuscribir(Suscripcion s);

	Suscripcion getSuscripcion(Usuario u, Canal c);

}
