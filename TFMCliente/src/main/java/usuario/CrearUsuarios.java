package usuario;

import canal.Canal;

public class CrearUsuarios {

	public static void main(String [] args) {
		Registro.crearUsuario("PepeCrear", "Ja123456", "pepeCrear@mail.com");
		Registro.crearUsuario("PepeCrear2", "Ja123456", "pepeCrear2@mail.com");
		Canal.crearCanal("F");
	}
	
}
