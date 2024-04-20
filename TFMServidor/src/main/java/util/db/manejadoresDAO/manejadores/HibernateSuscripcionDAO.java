package util.db.manejadoresDAO.manejadores;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import util.db.manejadoresDAO.interfaces.SuscripcionDAO;
import util.db.modelos.Suscripcion;
import util.db.modelos.Usuario;
import util.db.modelos.Canal;

public class HibernateSuscripcionDAO implements SuscripcionDAO {
	
	private Connection conn;

	public HibernateSuscripcionDAO(Connection conn) {
		this.conn = conn;
	}

	@Override
	public boolean usuarioSuscrito(Usuario usuarioObj, Canal canalObj) {
	    boolean[] test = {false};
	    String consulta = "SELECT COUNT(*) FROM Suscripcion WHERE usuario_NombreUsuario = ? AND NombreCanal = ?";
	    
	    try {
	        PreparedStatement stmt = conn.prepareStatement(consulta);

	        stmt.setString(1, usuarioObj.getNombreUsuario());
	        stmt.setString(2, canalObj.getNombreCanal());

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
	public void suscribir(Usuario usuario, Canal canal, String ip, int puerto) {
	    String consulta = "INSERT INTO Suscripcion (SuscripcionID, usuario_NombreUsuario, NombreCanal, FechaSuscripcion, ip, puerto) VALUES (?, ?, ?, ?, ?, ?)";
	    
	    try {
	        PreparedStatement stmt = conn.prepareStatement(consulta);

	        String suscripcionID = usuario.getNombreUsuario() + "__" + canal.getNombreCanal();
	        Timestamp fechaSuscripcion = new Timestamp(System.currentTimeMillis());

	        stmt.setString(1, suscripcionID);
	        stmt.setString(2, usuario.getNombreUsuario());
	        stmt.setString(3, canal.getNombreCanal());
	        stmt.setTimestamp(4, fechaSuscripcion);
	        stmt.setString(5, ip);
	        stmt.setInt(6, puerto);

	        stmt.executeUpdate();

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


	@Override
	public void desuscribir(Suscripcion s) {
	    String consulta = "DELETE FROM Suscripcion WHERE SuscripcionID = ?";
	    
	    try {
	        PreparedStatement stmt = conn.prepareStatement(consulta);

	        stmt.setString(1, s.getSuscripcionID());
	        stmt.executeUpdate();

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


	@Override
	public Suscripcion getSuscripcion(Usuario u, Canal c) {
	    Suscripcion suscripcion = null;
	    String consulta = "SELECT * FROM Suscripcion WHERE usuario_NombreUsuario = ? AND NombreCanal = ?";
	    
	    try {
	        PreparedStatement stmt = conn.prepareStatement(consulta);

	        stmt.setString(1, u.getNombreUsuario());
	        stmt.setString(2, c.getNombreCanal());

	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                // Crear un objeto Suscripcion con los datos del resultado
	                suscripcion = new Suscripcion();
	                // Asignar valores a suscripcion según los campos en la tabla
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return suscripcion;
	}


}
