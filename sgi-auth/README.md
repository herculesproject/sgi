
## Desarrollo

Este proyecto está configurado para ser editado en **[VSCode](https://code.visualstudio.com/)** y sus extensiones para **[Desarrollo Remoto](https://code.visualstudio.com/docs/remote/remote-overview)**.

Concretamente el desarrollo se realiza en un contenedor **Docker** (es necesario tener instalado Docker en tu equipo) con una imagen con **Java 8** y **Maven 3.6**.

Antes de abrir el proyecto en el contedor usando **VSCode**, es necesario adaptar los siguiente parámetros en el fichero **./devcontainer/docker-compose.yml**:
*  **user**
    *  **"1000:1000"**: 1000:1000 se debe cambiar por tu UID, GID.  Para conocerlos puedes ejecutar el comando `echo "$(id -u):$(id -g)"` en una consola.

Además, deben existir los siguientes directorios en tu directorio **HOME**:
* **.sdkman/archives**: Se usa para compartir las distribuciones descargadas de Java y Maven entre el host y el contenedor.
* **.m2/repository**: Se usa para compartir el repositorio de Maven entre el host y el contenedor.
* **.ssh**: Se usa para compartir las credenciales ssh entre el host y el contenedor.

Así como el siguiente fichero (también en tu directorio **HOME**):
* **.gitconfig**: Se usa para compartir la configuración de git entre el host y el contenedor.

## Run the Application

