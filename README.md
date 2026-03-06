![](./doc/images/logos_feder.png)

# HÉRCULES - SGI 
El objetivo del Proyecto SGI dentro del Proyecto HÉRCULES es crear un Prototipo de un Sistema de Gestión de Investigación (SGI) basado en datos abiertos semánticos que ofrezca una visión global de los datos de investigación del Sistema Universitario Español (SUE) para mejorar la gestión, el análisis y las posibles sinergias entre universidades y el gran público desarrollando e incorporando  soluciones que superen las actualmente disponibles en el mercado.


## Getting Started

### Prerrequisitos

- [Git](https://git-scm.com/)
- [Docker](https://docs.docker.com/get-docker/)
- [Visual Studio Code](https://code.visualstudio.com/) con la extensión [Dev Containers](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-containers)

### 1. Clonar el repositorio y abrir en Dev Container

```bash
git clone https://github.com/herculesproject/sgi.git
cd sgi
code .
```

VS Code detectará automáticamente `.devcontainer` y mostrará el aviso **Reopen in Container**. Aceptar y esperar a que termine la construcción del contenedor.

### 2. Abrir el workspace

Una vez dentro del contenedor, abrir **`dev-sgi.code-workspace`** desde el explorador de archivos (**File > Open Workspace from File…**).

### 3. Configurar las variables de entorno

El fichero `.env.example` contiene la plantilla con los valores por defecto para desarrollo local. 

Para utilizar una configuración personalizada, basta con crear el fichero `.env.dev` usando la plantilla como referencia; las tareas y configuraciones de lanzamiento ya están preparadas para leer este fichero si existe (estando a su vez excluido del repositorio mediante `.gitignore`).


Para utilizar una configuración personalizada, crea un fichero `.env.dev` a partir de esta plantilla:

```bash
cp .env.example .env.dev
```

El `dev-sgi.code-workspace` está configurado para utilizar automáticamente el `.env.dev` si existe. 

### 4. Arrancar servicios requeridos

Los servicios del SGI necesitan **sgi-auth** (Keycloak) y **sgi-esb** (bus de integración) para funcionar. Para desarrollo local, ambos pueden levantarse mediante tasks de VS Code (**Terminal > Run Task…** o `Ctrl+Shift+P` **> Tasks: Run Task**):

| Task                  | Descripción                                              |
| --------------------- | -------------------------------------------------------- |
| `Start sgi-auth`      | Keycloak con el realm SGI preconfigurado                 |
| `Start sgi-esb stack` | ESB junto con todos los servicios externos de referencia |

### 5. Arrancar la aplicación

Desde el panel **Run and Debug** (`Ctrl+Shift+D`):

1. Seleccionar el servicio que se quiera ejecutar.
2. Pulsa **Start Debugging**.
