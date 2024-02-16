package util.db.manejadoresDAO.interfaces;

import util.db.modelos.Usuario;

public interface UsuarioDAO {
	
	void guardarUsuario(Usuario usuario);
    Usuario obtenerUsuarioPorId(Long id);

}
