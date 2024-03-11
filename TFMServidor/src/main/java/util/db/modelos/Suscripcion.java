package util.db.modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "Suscripcion")
public class Suscripcion {

    @Id
    @Column(name = "SuscripcionID")
    private String suscripcionID;

    @ManyToOne
    @JoinColumn(name = "usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "NombreCanal")
    private Canal canal;

    @Column(name = "FechaSuscripcion")
    private LocalDateTime fechaSuscripcion;
    
    @Column(name = "ip")
    private String ip;
    
    @Column(name = "puerto")
    private int puerto;
    
    
    
    public Suscripcion() {}

    public Suscripcion(String suscripcionID, Usuario usuario, Canal canal, LocalDateTime fechaSuscripcion, String ip, int puerto) {
        this.usuario = usuario;
        this.canal = canal;
        this.fechaSuscripcion = fechaSuscripcion;
        this.ip = ip;
        this.puerto = puerto;
        this.suscripcionID = suscripcionID;
        // Inicializar otros atributos según sea necesario
    }

	public String getSuscripcionID() {
		return suscripcionID;
	}

	public void setSuscripcionID(String suscripcionID) {
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

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPuerto() {
		return puerto;
	}

	public void setPuerto(int puerto) {
		this.puerto = puerto;
	}

}