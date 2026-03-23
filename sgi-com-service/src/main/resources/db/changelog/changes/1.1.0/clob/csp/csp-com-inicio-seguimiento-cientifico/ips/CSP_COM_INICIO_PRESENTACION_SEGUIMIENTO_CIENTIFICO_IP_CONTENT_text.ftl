<#assign data = CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP_DATA?eval />
<#--
  Formato CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP_DATA:
  { 
    "titulo": "[{"lang":"es", "value":"Proyecto 1"}]",
    "fechaInicio": "2022-01-01T00:00:00Z",
    "fechaFin": "2022-01-31T23:59:59Z",
    "numPeriodo": 2,
    enlaceAplicacion: "https://sgi.treelogic.com"
  }
-->
<#macro renderEs>
<#setting locale="es">
Estimado/a investigador/a,
Le informamos de que próximamente se iniciará el plazo de presentación de la documentación de seguimiento científico del proyecto abajo indicado:
- Proyecto: ${sgi.getFieldValue(data.titulo)}
- Fecha de inicio de presentación de documentación:  ${sgi.formatDate(data.fechaInicio, "SHORT")}, ${sgi.formatTime(data.fechaInicio, "SHORT")}
- Periodo de seguimiento: ${data.numPeriodo}
Puede revisar la información a través de la aplicación: ${data.enlaceAplicacion}

Reciba un cordial saludo,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear Researcher,
Please be advised that the deadline for the submission of the scientific will soon begin documentation for the project below is approaching:
- Project: ${sgi.getFieldValue(data.titulo)}
- Start date for submission of documentation: ${sgi.formatDate(data.fechaInicio, "SHORT")}, ${sgi.formatTime(data.fechaInicio, "SHORT")}
- Follow-up period:${data.numPeriodo}
You can review the information through the application: ${data.enlaceAplicacion}

Yours sincerely,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:
Honen bidez jakinarazten dizugu laster hasiko dela behean aipatutako proiektuaren jarraipen zientifikorako dokumentazioa aurkezteko epea: 
- Proiektua: ${sgi.getFieldValue(data.titulo)}
- Dokumentazioa aurkezteko epearen hasiera: ${sgi.formatDate(data.fechaInicio, "SHORT")}, ${sgi.formatTime(data.fechaInicio, "SHORT")}
- Jarraipen aldia: ${data.numPeriodo}
Informazioa berrikus dezakezu aplikazioaren bidez: ${data.enlaceAplicacion}

Jaso agur bero bat.
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>

<#macro renderCa>
<#setting locale="ca">
Benvolgut/da investigador/a,

Us informem que properament s'iniciarà el termini de presentació de la documentació de seguiment científic del projecte sota indicat:

- Projecte: ${sgi.getFieldValue(data.titulo)}
- Data d'inici de presentació de documentació: ${sgi.formatDate(data.fechaInicio, "SHORT")}, ${sgi.formatTime(data.fechaInicio, "SHORT")}
- Període de seguiment: ${data.numPeriodo}

Podeu revisar la informació a través de l'aplicació: ${data.enlaceAplicacion}

Rebi una salutació cordial,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>

<#list languagePriorities as renderLang>
<@.vars["render${renderLang?capitalize}"] />
<#if renderLang?has_next>

-------------------------------------------------------------------------------

</#if>
</#list>