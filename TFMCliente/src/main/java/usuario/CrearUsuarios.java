package usuario;

import canal.Canal;

public class CrearUsuarios {

	public static void main(String [] args) {
		Registro.crearUsuario("Pepe", "Ja123456", "pepe@mail.com");
		Registro.crearUsuario("Pepe2", "Ja123456", "pepe2@mail.com");
		Canal.crearCanal("F");
	}
	
}
