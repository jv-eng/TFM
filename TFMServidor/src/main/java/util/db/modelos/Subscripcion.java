package util.db.modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "Suscripciones")
public class Subscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SuscripcionID")
    private Long suscripcionID;

    @ManyToOne
    @JoinColumn(name = "UsuarioID")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "CanalID")
    private Canal canal;

    @Column(name = "FechaSuscripcion")
    private LocalDateTime fechaSuscripcion;
    
    @Column(name = "Socket")
    private int socket;
    
    
    
    public Subscripcion() {}

    public Subscripcion(Usuario usuario, Canal canal, LocalDateTime fechaSuscripcion, int socket) {
        this.usuario = usuario;
        this.canal = canal;
        this.fechaSuscripcion = fechaSuscripcion;
        this.socket = socket;
        // Inicializar otros atributos según sea necesario
    }

	public Long getSuscripcionID() {
		return suscripcionID;
	}

	public void setSuscripcionID(Long suscripcionID) {
		this.suscripcionID = suscripcionID;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Canal getCanal() {
		return canal;
	}

	public void setCanal(Canal canal) {
		this.canal = canal;
	}

	public LocalDateTime getFechaSuscripcion() {
		return fechaSuscripcion;
	}

	public void setFechaSuscripcion(LocalDateTime fechaSuscripcion) {
		this.fechaSuscripcion = fechaSuscripcion;
	}

	public int getSocket() {
		return socket;
	}

	public void setSocket(int socket) {
		this.socket = socket;
	}
    

}