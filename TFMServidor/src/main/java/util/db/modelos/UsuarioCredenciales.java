package util.db.modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "UsuarioCredenciales")
public class UsuarioCredenciales {
	
    @Column(name = "NombreUsuario")
    private String nombreUsuario;  // Clave primaria
	
    @Id
	@Column(name = "Correo")
	private String correo;
	
	@Column(name = "Password")
	private String pass;
	
	@Column(name = "Clave", length=500)
	private String clave;
	
	
	public UsuarioCredenciales() {}
	
	public UsuarioCredenciales(String nombreUsuario, String pass, String clave, String correo) {
		this.nombreUsuario = nombreUsuario;
		this.pass = pass;
		this.clave = clave;
		this.correo = correo;
	}

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

}
