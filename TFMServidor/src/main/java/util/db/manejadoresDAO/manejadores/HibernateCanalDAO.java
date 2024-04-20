package util.db.manejadoresDAO.manejadores;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import util.db.manejadoresDAO.interfaces.CanalDAO;
import util.db.modelos.Canal;
import util.db.modelos.Suscripcion;
import util.db.modelos.Usuario;

public class HibernateCanalDAO implements CanalDAO {
	
	private Connection conn;

	public HibernateCanalDAO(Connection conn) {
		this.conn = conn;
	}

	//comprobar si existe canal
	@Override
	public boolean existeCanal(String canal) {
	    boolean exists = false;
	    String consulta = "SELECT COUNT(*) FROM Canal WHERE nombreCanal = ?";
	    
	    try {
	        PreparedStatement stmt = conn.prepareStatement(consulta);
	        
	        stmt.setString(1, canal);

	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                int count = rs.getInt(1);
	                exists = count > 0;
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return exists;
	}


	@Override
	public void crearCanal(Usuario usuario, String canal) {
	    String consulta = "INSERT INTO Canal (nombreCanal, CreadorID) VALUES (?, ?)";
	    
	    try {
	        PreparedStatement stmt = conn.prepareStatement(consulta);

	        stmt.setString(1, canal);
	        stmt.setString(2, usuario.getNombreUsuario());
	        stmt.executeUpdate();

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


	@Override
	public Canal getCanal(String canal) {
	    Canal c = null;
	    String consulta = "SELECT * FROM Canal WHERE nombreCanal = ?";
	    
	    try {
	        PreparedStatement stmt = conn.prepareStatement(consulta);

	        stmt.setString(1, canal);

	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                // Crear un objeto Canal con los datos del resultado
	                c = new Canal();
	                c.setNombreCanal(rs.getString("nombreCanal"));
	                // Otras asignaciones de atributos si es necesario
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return c;
	}


	@Override
	public List<Suscripcion> getUsuariosSuscritos(Canal c) {
	    List<Suscripcion> suscripciones = new ArrayList<>();
	    String consulta = "SELECT * FROM Suscripcion WHERE NombreCanal = ?";
	    
	    try {
	        PreparedStatement stmt = conn.prepareStatement(consulta);

	        stmt.setString(1, c.getNombreCanal());

	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                // Crear objetos Suscripcion con los datos del resultado
	                Suscripcion suscripcion = new Suscripcion();
	                // Asignar valores a suscripcion según los campos en la tabla
	                suscripciones.add(suscripcion);
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return suscripciones;
	}


	@Override
	public List<String> getArchivosCanal(String nombreCanal) {
	    List<String> nombresArchivos = new ArrayList<>();
	    String consulta = "SELECT nombreArchivo FROM Archivos WHERE CanalID = (SELECT NombreCanal FROM Canal WHERE nombreCanal = ?)";
	    
	    try {
	        PreparedStatement stmt = conn.prepareStatement(consulta);

	        stmt.setString(1, nombreCanal);

	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                nombresArchivos.add(rs.getString("nombreArchivo"));
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return nombresArchivos;
	}

	


}
