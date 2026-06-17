# Changelog

## No publicado

<!-- Añadir cambios aquí conforme se van integrando en la rama principal.
     Mover a una sección versionada en el momento de cada release.

     Categorías válidas (en este orden):
       ### Added            Nuevas funcionalidades
       ### Changed          Cambios en funcionalidad existente
       ### Deprecated       Funcionalidad que se eliminará próximamente
       ### Removed          Funcionalidad eliminada
       ### Fixed            Corrección de errores
       ### Security         Correcciones de vulnerabilidades
-->

### Changed
- [CSP] Reordenadas las columnas de la exportación de solicitudes: el bloque de unidades de vinculación se muestra ahora antes de las columnas "Datos Solicitud RRHH" y sus columnas llevan el prefijo "Datos Proyecto", igual que el resto de columnas de las solicitudes de tipo proyecto ([#108](https://github.com/herculesproject/sgi/issues/108)).
- [CSP] Renombrada la etiqueta de la variable de configuración de unidades de vinculación para proyectos a "Habilitar unidades de vinculación para Proyectos y Solicitudes" para reflejar que también aplica a las solicitudes ([#108](https://github.com/herculesproject/sgi/issues/108)).

### Fixed
- Los campos `created_by` y `creation_date` de la clase base `Auditable` se marcan ahora como no actualizables (`updatable = false`), impidiendo que Hibernate los sobreescriba y garantizando la inmutabilidad de los datos de auditoría de creación en cualquier entidad ([#71](https://github.com/herculesproject/sgi/issues/71)).
- [CSP] Corregida la pérdida de los campos de auditoría de creación (`created_by`, `creation_date`) al clonar una convocatoria ([#71](https://github.com/herculesproject/sgi/issues/71)).
- [CSP] Corregida la pérdida de los campos de auditoría de creación (`created_by`, `creation_date`) al modificar una convocatoria y sus entidades relacionadas ([#103](https://github.com/herculesproject/sgi/issues/103)).
- [CSP] Corregida la pérdida de los campos de auditoría (`created_by`, `creation_date`, `last_modified_by`, `last_modified_date`) al modificar el equipo de un proyecto. Además, los miembros sin cambios reales ya no generan un UPDATE innecesario en base de datos ([#72](https://github.com/herculesproject/sgi/issues/72)).

## 1.1.0 (2026-06-11)

**Notas de actualización:** [1.1.0](/docs/upgrade/1.1.0.md)

### Added
- Añadido soporte para el idioma catalán ([#23](https://github.com/herculesproject/sgi/issues/23)).
- Añadidos nuevos componentes para poder ser utilizados en los formularios de memoria ([#33](https://github.com/herculesproject/sgi/issues/33)).
- Añadida documentación para las apis de integración: SGP, SGEMP, SGO, SGE y SGDOC. ([#47](https://github.com/herculesproject/sgi/issues/47)).
- [CSP] Añadida tabla maestra de tipos de grupo de investigación en sustitución del enum anterior, con soporte i18n y personalizable por instalación (configurable en *CSP > Configuración > Tipos de grupo*) ([#45](https://github.com/herculesproject/sgi/issues/45)).
- [CSP] Añadidos los campos acrónimo, dirección, email e imagen en los datos generales del grupo de investigación. La relación de aspecto recomendada y el tamaño máximo de la imagen son configurables en *CSP > Configuración* ([#37](https://github.com/herculesproject/sgi/issues/37)).
- [CSP] Añadido el campo descripción con soporte i18n en las líneas de investigación ([#51](https://github.com/herculesproject/sgi/issues/51)).
- [CSP] Añadida la descripción de la línea de investigación seleccionada en modo solo lectura en el formulario de líneas de investigación de los grupos de investigación ([#52](https://github.com/herculesproject/sgi/issues/52)).
- [CSP] Añadido el campo opcional tipo de enlace en los enlaces de grupo de investigación ([#50](https://github.com/herculesproject/sgi/issues/50)).
- [CSP] Añadido el apartado Relaciones institucionales en grupos de investigación ([#53](https://github.com/herculesproject/sgi/issues/53)).
- [CSP] Añadida tabla maestra de tipos de descriptor de grupos de investigación con soporte i18n (configurable en *CSP > Configuración > Tipos de descriptor de grupos*) ([#54](https://github.com/herculesproject/sgi/issues/54)).
- [CSP] Añadidos descriptores en los grupos de investigación ([#54](https://github.com/herculesproject/sgi/issues/54)).
- [CSP] Añadida tabla maestra de tipos de confidencialidad de proyectos en sustitución del campo booleano anterior, con soporte i18n y personalizable por instalación (configurable en *CSP > Configuración > Tipos de confidencialidad*) ([#57](https://github.com/herculesproject/sgi/issues/57)).
- [CSP] Añadido el campo régimen de concurrencia en los datos generales del proyecto, heredado de la convocatoria cuando el proyecto está vinculado a una ([#58](https://github.com/herculesproject/sgi/issues/58)).
- [CSP] Añadido el apartado Unidades de vinculación en grupos de investigación y añadido filtro por unidades de vinculación en el buscador ampliado de grupos. ([#56](https://github.com/herculesproject/sgi/issues/56)).
- [CSP] Añadido el apartado Unidades de vinculación en proyectos, con filtro por unidades en el buscador avanzado y nuevas variables de configuración para habilitar unidades de vinculación y áreas de conocimiento en proyectos ([#59](https://github.com/herculesproject/sgi/issues/59)).
- [CSP] Añadido el apartado Unidades de vinculación en solicitudes, al crear un proyecto desde la solicitud se copian las unidades de la solicitud al proyecto. El apartado de áreas de conocimiento y el nuevo apartado unidades de vinculación de solicitudes pasan a ser opcionales mediante las mismas variables de configuración que en proyecto ([#86](https://github.com/herculesproject/sgi/issues/86)).
- [WEB] Añadido soporte en el componente compartido de subida de ficheros para la descarga del fichero asociado, tooltips configurables y validación de tamaño máximo ([#37](https://github.com/herculesproject/sgi/issues/37)).

### Fixed
- [CSP] Se corrige error al clonar una convocatoria cuando sus periodos de justificación tienen observaciones ([#62](https://github.com/herculesproject/sgi/issues/62)).
- [CSP] Corregido el nombre del concepto de gasto que se mostraba como `[object Object]` en el listado de códigos económicos no permitidos al añadir una partida de gasto en el presupuesto del proyecto ([#74](https://github.com/herculesproject/sgi/issues/74)).
- [CSP] Corregidos valores NULL en columnas booleanas de `configuracion` en SQL Server y Oracle, donde el valor por defecto no se aplica automáticamente a las filas existentes al añadir columnas ([#90](https://github.com/herculesproject/sgi/issues/90)).
- Corregido el error 403 (`InvalidCsrfTokenException`) en las peticiones a endpoints públicos (`/public/**`) en despliegues donde los servicios se sirven bajo un prefijo de ruta (`server.servlet.context-path`, p. ej. `/api/csp`), causado por cookies `XSRF-TOKEN` duplicadas con distinto `path`. La cookie CSRF se emite ahora con un `path` fijo (`/` por defecto, configurable con `spring.security.csrf.cookie-path`) ([#99](https://github.com/herculesproject/sgi/issues/99)).
- [WEB] Corregido un error al usar validaciones a nivel de formulario (p. ej., la comparación de fechas entre campos) cuando el apartado o el campo implicado está oculto ([#101](https://github.com/herculesproject/sgi/issues/101)).



### Changed
- [CSP] Reordenados los campos de la pantalla de datos generales del grupo de investigación ([#37](https://github.com/herculesproject/sgi/issues/37)).
- [CSP] Ampliado el tamaño máximo de los campos título (1000 caracteres) y observaciones (8000 caracteres) del proyecto ([#63](https://github.com/herculesproject/sgi/issues/63)).
- [CSP] Ampliado el tamaño máximo del campo nombre (550 caracteres) de la fuente de financiación ([#64](https://github.com/herculesproject/sgi/issues/64)).
- [ETI] Añadir reemplazo de placeholders de nombre de comité en esquemas Formly para el contexto dev ([#36](https://github.com/herculesproject/sgi/issues/36)).

### Security
- [WEB] Eliminado el uso de `eval` en la generación de los mensajes personalizados de los validadores de los formularios dinámicos (formly), que permitía ejecutar código arbitrario desde mensajes configurables externamente (clobs en BD, integraciones) ([#101](https://github.com/herculesproject/sgi/issues/101)).

## 1.0.0 (2026-02-26)

**Notas de actualización:** [1.0.0](/docs/upgrade/1.0.0.md)

### Added
- [AUTH] Ampliados permisos del grupo SYSADM-CSP a los grupos ADMINISTRADOR-CSP y ADMINISTRADOR-CSP-UNIDAD_GESTION en el realm por defecto para nuevas instalaciones.
- [AUTH] Añadida [guía de rotación de claves y secretos](/sgi-auth/docs/keycloak-secrets-rotation.md) de Keycloak en el SGI.
- [CSP] Añadida capacidad de personalizar el formulario de proyecto económico según el contexto (proyectos o grupos de investigación).
- [REP] Añadido soporte para aplicar estilos a variables dinámicas en plantillas de informes mediante sufijos (|italic, |clarify, |clarify_i).
- [WEB] Añadida validación de permisos a nivel de módulo en el menú del perfil de investigador para ocultar los módulos a los que no tiene acceso.

### Changed
- [CSP] Actualizada plantilla de informe de autorización de participación en proyecto externo en español.
- [CSP] Con la configuración “anualidades obligatorias” se habilita siempre el desplegable “anualidades”. Cuando las anualidades se calculen con la duración del proyecto los años futuros se mostrarán pero no estarán seleccionados.
- [CSP] Modificado el formly 
- [CSP] Se oculta la exportación de requerimientos del seguimiento de justificación en caso de no estar habilitado el menú de Seguimiento Justificación - Requerimientos.
- [CSP] Unificación nombres menús Configuración.
- [ETI] Actualizada plantilla de informe de evaluación no favorable en inglés.
- [ETI] Actualizada plantilla de informe favorable en español e inglés.
- [ETI] Actualizada plantilla de informe favorable modificación en español e inglés.
- [ETI] Actualizada plantilla de informe favorable ratificación en español e inglés.
- [ETI] Actualizada plantilla de informe favorable retrospectiva en español, inglés y euskera.
- [ETI] Renombrado el comite de seres humanos (M10) en los datos de ejemplo (liquibase context=dev)
- [ETI] Se aplican mejoras en el interlineado de las plantillas de informes.
- [ETI] Se permite que no existan datos de contacto en la información recuperada del SGP evitando así que se produzca un error en el proceso de generación de informe de memoria y, por tanto, del envío a secretaría

### Fixed
- [CSP] Corrección de obtención de anualidades obligatorias cuando el proyecto tiene un presupuesto de tipo global definido.
- [CSP] Error en la creación de proyecto económico sin buscador con "habilitar buscador identificador sge" a "No".
- [CSP] No se actualizaba el identificador del proyecto SGE al realizar cambios en el calendario de facturación desde el perfil de investigador.
- [CSP] Revisión de envío de comunicado de justificación económica de proyectos.
- [CSP] Se corrige defecto al cargar detalle de un proyecto desde perfil de investigación cuando el usuario no tiene permisos para ver la solicitud relacionada.
- [CSP] Se corrige defecto al cargar detalle de un proyecto desde perfil de investigación.
- [CSP] Se corrige defecto en la exportación de grupos relativo al tratamiento de las clasificaciones.
- [CSP] Se corrige defecto que impedía la activación del botón en modal de actualización de proyecto económico.
- [CSP] Se corrige mensaje empresaRef no encontrado en tabla de periodos de amortización de proyectos.
- [ETI] Se corrige defecto en la creación/eliminación de miembros de comités relativo a las comprobaciones para evitar la duplicidad de mismo cargo en fechas.
- [ETI] Se corrige defecto en la generación de informes en múltiples idiomas. Los informes se descargaban en un idioma diferente al seleccionado.
- [ETI] Se corrige defecto sobre la visualización del mensaje de confirmación en página de Configuración del módulo de Etica.
- [ETI] Se corrige mensaje personaref no encontrado en vista de Evaluador - Seguimientos
- [ETI] Se corrige título en apartado documentación de las memorias.
- [ETI] Se corrigen defectos en plantilla de informe de la ficha del equipo evaluador en español, inglés y euskera.
- [REP] No se respetaba el interlineado configurado en las plantillas docx al convertir a PDF.
- [REP] Se eliminan los atributos comunidadAutonomaID y paisID de la información recuperada para cargar los datos de persona en los informes. Son atributos que no se utilizan y susceptibles de generar error en la conversión a long.

### Security
- [AUTH] Actualizado el Keycloak a la versión 13.0.0 para resolver 
[CVE-2020-27838](https://nvd.nist.gov/vuln/detail/CVE-2020-27838).
- [AUTH] Eliminada información sensible del realm de ejemplo (`sgi-realm.json`) incluido en la imagen de contenedor para prevenir despliegues inseguros si no se hace una personalización previa correcta.



## Versiones anteriores

Para consultar los cambios de versiones anteriores a la 1.0.0 (versionadas por fecha de liberación), ver el [histórico de changelogs](/docs/archive/changelog).
