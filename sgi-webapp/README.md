# SGI - HÉRCULES

Proyecto generado con [Angular CLI](https://github.com/angular/angular-cli) version 9.1.7.

## Acerca de

El objetivo del **Proyecto SGI** dentro del Proyecto **HÉRCULES** es crear un Prototipo de un Sistema de Gestión de Investigación (SGI) basado en datos abiertos semánticos que ofrezca una visión global de los datos de investigación del Sistema Universitario Español (SUE), para mejorar la gestión, el análisis y las posibles sinergias entre universidades y el gran público desarrollando e incorporando soluciones que superen las actualmente disponibles en el mercado.
También forma parte del objeto del contrato, el posterior desarrollo de un piloto del prototipo innovador de SGI desarrollado en la Universidad de Murcia con las correspondientes migraciones de datos e integraciones con otros sistemas de información existentes, tanto internos de la Universidad como externos.
El desarrollo de un prototipo innovador de Sistema de Gestión de la Investigación servirá como una de las bases y sobre el que se desarrollarán, las funcionalidades descritas en la capa semántica de la solución global Hércules (Proyecto Arquitectura Semántica).

*    Realizar explotación conjunta de información.
*    Unificar los criterios para la obtención de información, ofreciendo mayores garantías de una adecuada interpretación de la información y, con ello, la exactitud de los indicadores obtenidos.
*    Poder establecer sinergias y colaboraciones entre universidades y grupos de investigación.
*    Incrementar la transparencia.
*    Facilitar la transferencia tecnológica y la colaboración universidad-empresa.
*    Facilitar el conocimiento de la producción científica, para el resto de investigadores y para la sociedad en general.
*    Facilitar la integración del Currículum Vitae Normalizado (CVN). Mayor facilidad para la movilidad del Personal Docente Investigador (PDI) entre las universidades españolas.
*    Proporcionar con mayor facilidad al usuario, al contribuyente y a la sociedad datos estadísticos que puedan ser relevantes desde el punto de la transparencia en el ejercicio del servicio público.
*    Y, en definitiva, permitir la explotación conjunta de información de investigación de todas las universidades, permitiendo con ello una total transparencia en la gestión universitaria

## Desarrollo

Este proyecto está configurado para ser editado en **[VSCode](https://code.visualstudio.com/)** y sus extensiones para **[Desarrollo Remoto](https://code.visualstudio.com/docs/remote/remote-overview)**.

Concretamente el desarrollo se realiza en un contenedor **Docker** (es necesario tener instalado Docker en tu equipo) con una imagen con **Node 12**.

Antes de abrir el proyecto en el contedor usando **VSCode**, es necesario adaptar los siguiente parámetros en el fichero **./devcontainer/docker-compose.yml**:
*  **runArgs**
    *  **"1000:1000"**: 1000:1000 se debe cambiar por tu UID, GID.  Para conocerlos puedes ejecutar el comando `echo "$(id -u):$(id -g)"` en una consola.

Además, deben existir los siguientes directorios en tu directorio **HOME**:
*  **.nvm/.cache**: Se usa para compartir las distribuciones descargadas de Node entre el host y el contenedor.
*  **.npm/_cacache**:  Se usa para compartir la caché de paquetes de Node entre el host y el contenedor.
*  **.ssh**: Se usa para compartir las credenciales ssh entre el host y el contenedor.

Así como el siguiente fichero (también en tu directorio **HOME**):
*  **.gitconfig**: Se usa para compartir la configuración de git entre el host y el contenedor.

## Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

## Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

## Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory. Use the `--prod` flag for a production build.

## Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).

## Running end-to-end tests

Run `ng e2e` to execute the end-to-end tests via [Protractor](http://www.protractortest.org/).

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI README](https://github.com/angular/angular-cli/blob/master/README.md).
