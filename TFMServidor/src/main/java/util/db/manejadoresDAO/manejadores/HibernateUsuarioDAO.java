package util.db.manejadoresDAO.manejadores;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import util.db.manejadoresDAO.interfaces.UsuarioDAO;
import util.db.modelos.Usuario;

public class HibernateUsuarioDAO implements UsuarioDAO {
	
	private Connection conn;

	public HibernateUsuarioDAO(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void crearUsuario(String usuario, String correo, String pass) {
	    String consulta = "INSERT INTO Usuario (NombreUsuario, CorreoElectronico) VALUES (?, ?)";
	    
	    try {
	        PreparedStatement stmt = conn.prepareStatement(consulta);

	        stmt.setString(1, usuario);
	        stmt.setString(2, correo);

	        stmt.executeUpdate();

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	
	@Override
	public boolean existeUsuario(String correo) {
	    boolean[] test = {false};
	    String consulta = "SELECT COUNT(*) FROM Usuario WHERE CorreoElectronico = ?";
	    
	    try {
	        PreparedStatement stmt = conn.prepareStatement(consulta);

	        stmt.setString(1, correo);

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
	public boolean comprobarUsuarioId(String usuario) {
	    boolean[] test = {false};
	    String consulta = "SELECT COUNT(*) FROM Usuario WHERE NombreUsuario = ?";
	    
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
	public Usuario getUsuario(String correo) {
	    Usuario usuario = null;
	    String consulta = "SELECT * FROM Usuario WHERE CorreoElectronico = ?";
	    
	    try {
	        PreparedStatement stmt = conn.prepareStatement(consulta);

	        stmt.setString(1, correo);

	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                // Crear un objeto Usuario con los datos del resultado
	                usuario = new Usuario();
	                usuario.setNombreUsuario(rs.getString("NombreUsuario"));
	                usuario.setCorreoElectronico(rs.getString("CorreoElectronico"));
	                // Otras asignaciones de atributos si es necesario
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return usuario;
	}


}
