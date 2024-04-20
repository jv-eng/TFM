package util.db.manejadoresDAO.interfaces;

public interface UsuarioCredencialesDAO {

	boolean comprobarUsuario(String usuario);

	void crearUsuario(String usuario, String correo, String pass);
	
	void insertarClave(String correo, String clave);
	
	boolean comprobarCredenciales(String correo, String contraseña);
	
	void borrarClave(String correo);
	
	String getNombreUsuario(String correo);
	
}
