package util.db.modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.Set;

@Entity
@Table(name = "Canales")
public class Canal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CanalID")
    private Long canalID;

    @Column(name = "NombreCanal")
    private String nombreCanal;

    @ManyToOne
    @JoinColumn(name = "CreadorID")
    private Usuario creador;

    // Otros atributos según sea necesario

    @OneToMany(mappedBy = "canal")
    private Set<Subscripcion> suscripciones;

    @OneToMany(mappedBy = "canal")
    private Set<Archivo> archivos;
    
    
    
    public Canal() {}

    public Canal(String nombreCanal, Usuario creador) {
        this.nombreCanal = nombreCanal;
        this.creador = creador;
        // Inicializar otros atributos según sea necesario
    }

	public Long getCanalID() {
		return canalID;
	}

	public void setCanalID(Long canalID) {
		this.canalID = canalID;
	}

	public String getNombreCanal() {
		return nombreCanal;
	}

	public void setNombreCanal(String nombreCanal) {
		this.nombreCanal = nombreCanal;
	}

	public Usuario getCreador() {
		return creador;
	}

	public void setCreador(Usuario creador) {
		this.creador = creador;
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
