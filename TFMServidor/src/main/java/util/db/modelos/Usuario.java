package util.db.modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "Usuarios")
public class Usuario {

	@Id
    @Column(name = "NombreUsuario")
    private String nombreUsuario;  // Clave primaria

    @Column(name = "CorreoElectronico")
    private String correoElectronico;

    @Column(name = "FechaRegistro")
    private LocalDateTime fechaRegistro;

    // Otros atributos según sea necesario

    @OneToMany(mappedBy = "usuario")
    private Set<Subscripcion> suscripciones;

    @OneToMany(mappedBy = "usuario")
    private Set<Archivo> archivos;
    
    
    public Usuario() {}

    public Usuario(String nombreUsuario, String correoElectronico, LocalDateTime localDateTime) {
        this.nombreUsuario = nombreUsuario;
        this.correoElectronico = correoElectronico;
        this.fechaRegistro = localDateTime;
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

	public LocalDateTime getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(LocalDateTime fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	public Set<Subscripcion> getSuscripciones() {
		return suscripciones;
	}

	public void setSuscripciones(Set<Subscripcion> suscripciones) {
		this.suscripciones = suscripciones;
	}

	public Set<Archivo> getArchivos() {
		return archivos;
	}

	public void setArchivos(Set<Archivo> archivos) {
		this.archivos = archivos;
	}

    

}

