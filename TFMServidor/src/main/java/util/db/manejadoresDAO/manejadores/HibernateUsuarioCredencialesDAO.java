package util.db.manejadoresDAO.manejadores;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import util.db.manejadoresDAO.interfaces.UsuarioCredencialesDAO;

public class HibernateUsuarioCredencialesDAO implements UsuarioCredencialesDAO {
	
	private Connection conn;

	public HibernateUsuarioCredencialesDAO(Connection conn) {
		this.conn = conn;
	}

	@Override
	public boolean comprobarUsuario(String usuario) {
	    boolean[] test = {false};
	    String consulta = "SELECT COUNT(*) FROM UsuarioCredenciales WHERE NombreUsuario = ?";
	    
	    try {
	        PreparedStatement stmt = conn.prepareStatement(consulta);

	        stmt.setString(1, usuario);

	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                int count = rs.getInt(1);
	                test[0] = count > 0;
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return test[0];
	}


	@Override
	public void crearUsuario(String usuario, String correo, String pass) {
	    String consulta = "INSERT INTO UsuarioCredenciales (NombreUsuario, Correo, Password, Clave) VALUES (?, ?, ?, ?)";
	    
	    try {
	        PreparedStatement stmt = conn.prepareStatement(consulta);

	        stmt.setString(1, usuario);
	        stmt.setString(2, correo);
	        stmt.setString(3, pass);
	        stmt.setString(4, pass); // Aquí se está usando el mismo valor de pass para la clave

	        stmt.executeUpdate();

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


	@Override
	public void insertarClave(String correo, String clave) {
	    String consulta = "UPDATE UsuarioCredenciales SET Clave = ? WHERE Correo = ?";
	    
	    try {
	        PreparedStatement stmt = conn.prepareStatement(consulta);

	        stmt.setString(1, clave);
	        stmt.setString(2, correo);

	        stmt.executeUpdate();

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


	@Override
	public boolean comprobarCredenciales(String correo, String contraseña) {
	    boolean[] test = {false};
	    String consulta = "SELECT COUNT(*) FROM UsuarioCredenciales WHERE Correo = ? AND Password = ?";
	    
	    try {
	        PreparedStatement stmt = conn.prepareStatement(consulta);

	        stmt.setString(1, correo);
	        stmt.setString(2, contraseña);

	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                int count = rs.getInt(1);
	                test[0] = count > 0;
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return test[0];
	}

	
	@Override
	public void borrarClave(String correo) {
	    String consulta = "UPDATE UsuarioCredenciales SET Clave = NULL WHERE Correo = ?";
	    
	    try {
	        PreparedStatement stmt = conn.prepareStatement(consulta);

	        stmt.setString(1, correo);
	        stmt.executeUpdate();

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


	@Override
	public String getNombreUsuario(String correo) {
	    String nombreUsuario = "";
	    String consulta = "SELECT NombreUsuario FROM UsuarioCredenciales WHERE Correo = ?";
	    
	    try {
	        PreparedStatement stmt = conn.prepareStatement(consulta);

	        stmt.setString(1, correo);

	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                nombreUsuario = rs.getString("NombreUsuario");
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return nombreUsuario;
	}


}
