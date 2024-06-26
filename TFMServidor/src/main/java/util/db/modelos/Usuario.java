package util.db.modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.Set;

@Entity
@Table(name = "Usuario")
public class Usuario {

    @Column(name = "NombreUsuario")
    private String nombreUsuario;  // Clave primaria

    @Id
    @Column(name = "CorreoElectronico")
    private String correoElectronico;
    
    @Column(name = "Socket")
    private String socket;


    // Otros atributos según sea necesario

    @OneToMany(mappedBy = "usuario")
    private Set<Suscripcion> suscripciones;

    @OneToMany(mappedBy = "usuario")
    private Set<Archivo> archivos;
    
    
    public Usuario() {}

    public Usuario(String nombreUsuario, String correoElectronico, String socket) {
        this.nombreUsuario = nombreUsuario;
        this.correoElectronico = correoElectronico;
        this.socket = socket;
        // Inicializar otros atributos según sea necesario
    }

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	public String getCorreoElectronico() {
		return correoElectronico;
	}

	public void setCorreoElectronico(String correoElectronico) {
		this.correoElectronico = correoElectronico;
	}

	public String getSocket() {
		return socket;
	}

	public void setSocket(String socket) {
		this.socket = socket;
	}

	public Set<Suscripcion> getSuscripciones() {
		return suscripciones;
	}

	public void setSuscripciones(Set<Suscripcion> suscripciones) {
		this.suscripciones = suscripciones;
	}

	public Set<Archivo> getArchivos() {
		return archivos;
	}

	public void setArchivos(Set<Archivo> archivos) {
		this.archivos = archivos;
	}

    

}

