package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.io.FileInputStream;

public class Configuration {
	private static Properties propiedades = new Properties();

    static {
        try {
            //propiedades.load(new FileInputStream("config.properties"));
        	InputStream inputStream = Configuration.class.getClassLoader().getResourceAsStream("config.properties");
            propiedades.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            // Manejo de errores al cargar el archivo de configuración
        }
    }

    public static String obtenerConfiguracion(String clave) {
        return propiedades.getProperty(clave);
    }
}
