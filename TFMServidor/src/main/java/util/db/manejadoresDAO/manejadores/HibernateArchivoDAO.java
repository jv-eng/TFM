package util.db.manejadoresDAO.manejadores;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import util.db.manejadoresDAO.interfaces.ArchivoDAO;
import util.db.modelos.Archivo;
import util.db.modelos.Canal;
import util.db.modelos.Usuario;

public class HibernateArchivoDAO implements ArchivoDAO {
	
	private Connection conn;

	public HibernateArchivoDAO(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void guardarFichero(String fileName, String ruta, Usuario u, Canal c) {
	    String consulta = "INSERT INTO Archivos (nombreArchivo, RutaSistemaArchivos, UsuarioID, CanalID) VALUES (?, ?, ?, ?)";
	    
	    try {
	        PreparedStatement stmt = conn.prepareStatement(consulta);

	        stmt.setString(1, fileName);
	        stmt.setString(2, ruta);
	        stmt.setString(3, u.getNombreUsuario());
	        stmt.setString(4, c.getNombreCanal());

	        stmt.executeUpdate();

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


	@Override
	public Archivo getFichero(String nombreFich) {
	    Archivo archivo = null;
	    String consulta = "SELECT * FROM Archivos WHERE nombreArchivo = ?";
	    
	    try {
	        PreparedStatement stmt = conn.prepareStatement(consulta);

	        stmt.setString(1, nombreFich);

	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                // Crear un objeto Archivo con los datos del resultado
	                archivo = new Archivo();
	                archivo.setNombreArchivo(rs.getString("nombreArchivo"));
	                // Otras asignaciones de atributos si es necesario
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return archivo;
	}

	
	@Override
	public boolean getAll() {
	    boolean isEmpty = true;
	    String consulta = "SELECT COUNT(*) FROM Archivos";
	    
	    try {
	         PreparedStatement stmt = conn.prepareStatement(consulta);
	         ResultSet rs = stmt.executeQuery();

	        if (rs.next()) {
	            int count = rs.getInt(1);
	            isEmpty = count == 0;
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return isEmpty;
	}

	
}
