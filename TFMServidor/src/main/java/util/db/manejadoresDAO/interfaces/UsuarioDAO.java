package util.db.manejadoresDAO.interfaces;

import util.db.modelos.Usuario;

public interface UsuarioDAO {
	
	public void crearUsuario(String usuario, String correo);
	public boolean existeUsuario(String correo);
	public boolean comprobarUsuarioId(String usuario);
	public Usuario getUsuario(String correo);

}
