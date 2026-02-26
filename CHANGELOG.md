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
