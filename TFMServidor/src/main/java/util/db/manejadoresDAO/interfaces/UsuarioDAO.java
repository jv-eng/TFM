package util.db.manejadoresDAO.interfaces;

import util.db.modelos.Usuario;

public interface UsuarioDAO {
	
	void guardarUsuario(Usuario usuario);
    Usuario obtenerUsuarioPorId(String usuario);
	void crearUsuario(String usuario, String correo, String pass);
	boolean comprobarUsuario(String correo);
	Usuario getUsuario(String correo);
	boolean existeUsuario(String usuario);
	public Usuario getUsuarioId(String correo);
	boolean comprobarUsuarioId(String usuario);

}
