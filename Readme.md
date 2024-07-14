# Sistema de Intercambio de Archivos con Servicios de Seguridad y Nearby

## Trabajo de fin de Máster en Ingeniería Informática (MUII)

### Descripción

En este repositorio de GitHub se incluye el código del desarrollo del TFM.

Concretamente, se incluyen los siguientes ficheros:

1. Código del servidor Java con Apache Maven
2. Código del cliente de prueba Java
3. Código de la aplicación Android con Nearby
4. Fichero comprimido con la infraestructura de seguridad
5. Comandos empleados para esa infraestructura
6. Fichero SQL para crear las tablas de la base de datos MySQL
7. Fichero de configuración MySQL para habilitar TLS


### Instrucciones

Antes de ejecutar el proyecto, es necesario seguir algunos pasos:

1. Instalar la base de datos
2. Crear las 2 bases de datos necesarias: servidor y usuarios
3. Instalar OpenSSL
4. Cambiar direcciones IP y puertos en los ficheros de configuración de la aplicación Android, servidor Java y ORM
5. Compilar aplicación Android

### Notas

Esta aplicación emplea TLS 1.3 y autenticación de cliente, por lo que en versiones bajas de Android podría dar problemas.

Es importante destacar que, para usar Nearby, hay algunos permisos que no basta solo con especificarlos en el manifest.xml. Aunque se solicitan, si hay algún problema, sería recomendable revisar los permisos asignados a la aplicación. [Más información aquí](https://developers.google.com/nearby/connections/android/get-started?hl=es-419)

Las siguientes configuraciones podrían ser de interés (enlaces directos al fichero):

1. [Configuración TLS 1.3 en el servidor](https://github.com/jv-eng/TFM/blob/main/TFMServidor/src/main/java/main/Main.java)
2. [Configuración contexto SSL cliente Android](https://github.com/jv-eng/TFM/blob/main/TFMProjectMobile/app/src/main/java/com/jv/tfmprojectmobile/util/AuxiliarUtil.java)
3. [Revisión necesaria de permisos en actividad para usar Nearby](https://github.com/jv-eng/TFM/blob/main/TFMProjectMobile/app/src/main/java/com/jv/tfmprojectmobile/activities/MenuActivity.java)
4. [Usuario anunciante en Nearby](https://github.com/jv-eng/TFM/blob/main/TFMProjectMobile/app/src/main/java/com/jv/tfmprojectmobile/activities/CreateChannelActivity.java)
5. [Usuario descubridor en Nearby](https://github.com/jv-eng/TFM/blob/main/TFMProjectMobile/app/src/main/java/com/jv/tfmprojectmobile/activities/DescubrirCanalesActivity.java)