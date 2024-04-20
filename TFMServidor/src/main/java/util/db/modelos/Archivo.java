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
@Table(name = "Archivos")
public class Archivo {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "archivo_id")
    private Long id;

    @Column(name = "nombreArchivo")
    private String nombreArchivo;
    
    @Column(name = "RutaSistemaArchivos")
    private String rutaSistemaArchivos;

    @ManyToOne
    @JoinColumn(name = "UsuarioID")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "CanalID")
    private Canal canal;

    @Column(name = "FechaEnvio")
    private LocalDateTime fechaEnvio;
    
    
    
    public Archivo() {}

    public Archivo(String nombreArchivo, String rutaSistemaArchivos, LocalDateTime fechaEnvio, Usuario usuario, Canal canal) {
    	this.nombreArchivo = nombreArchivo;
    	this.rutaSistemaArchivos = rutaSistemaArchivos;
        this.fechaEnvio = fechaEnvio;
        this.usuario = usuario;
        this.canal = canal;
        // Inicializar otros atributos según sea necesario
    }


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRutaSistemaArchivos() {
		return rutaSistemaArchivos;
	}

	public void setRutaSistemaArchivos(String rutaSistemaArchivos) {
		this.rutaSistemaArchivos = rutaSistemaArchivos;
	}

	public String getNombreArchivo() {
		return nombreArchivo;
	}

	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
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

	public LocalDateTime getFechaEnvio() {
		return fechaEnvio;
	}

	public void setFechaEnvio(LocalDateTime fechaEnvio) {
		this.fechaEnvio = fechaEnvio;
	}


}