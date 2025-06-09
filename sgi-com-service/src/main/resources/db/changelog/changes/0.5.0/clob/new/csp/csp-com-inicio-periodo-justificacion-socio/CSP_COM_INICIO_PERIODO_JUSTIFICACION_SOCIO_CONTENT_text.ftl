<#assign data = CSP_COM_INICIO_PERIODO_JUSTIFICACION_SOCIO_DATA?eval />
<#--
  Formato CSP_COM_INICIO_PERIODO_JUSTIFICACION_SOCIO_DATA:
  { 
    "titulo": "[{"lang":"es", "value":"Proyecto 1"}]",
    "nombreEntidad": "Entidad Colaboradora",
    "fechaInicio": "2022-01-01T00:00:00Z",
    "fechaFin": "2022-01-31T23:59:59Z",
    "numPeriodo": 2,
    "enlaceAplicacion": "https://sgi.treelogic.com"
  }
-->
<#macro renderEs>
<#setting locale="es">
Estimado/a investigador/a,

Le informamos de que próximamente dara inicio el periodo de presentación de justificación del proyecto referenciado más abajo. Puede proceder a realizar la justificación a través del siguiente enlace: ${data.enlaceAplicacion}

- Fecha de inicio de periodo de justificación: ${sgi.formatDate(data.fechaInicio, "SHORT")}, ${sgi.formatTime(data.fechaInicio, "SHORT")}
- Entidad socia: ${data.nombreEntidad}
- Proyecto en el que colabora: ${sgi.getFieldValue(data.titulo)}
- Periodo que se debe justificar: ${data.numPeriodo}

Reciba un cordial saludo,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear researcher,

Please be advised that the period for submission of justification for the project in question below will start soon. You can proceed with the justification through the following link: ${data.enlaceAplicacion}

- Start date of the justification period: ${sgi.formatDate(data.fechaInicio, "SHORT")}, ${sgi.formatTime(data.fechaInicio, "SHORT")}
- Partner entity: ${data.nombreEntidad}
- Collaboration project: ${sgi.getFieldValue(data.titulo)}
- Period to be justified: ${data.numPeriodo}

Yours sincerely,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:

Honen bidez jakinarazten dizugu laster hasiko dela behean aipatutako proiektuaren justifikazioa aurkezteko epea. Honako esteka honen bidez egin dezakezu justifikazioa: ${data.enlaceAplicacion}

- Justifikazio epearen hasiera data: ${sgi.formatDate(data.fechaInicio, "SHORT")}, ${sgi.formatTime(data.fechaInicio, "SHORT")}
- Erakunde bazkidea: ${data.nombreEntidad}
- Zer proiektutan kolaboratzen duen: ${sgi.getFieldValue(data.titulo)}
- Justifikatu beharreko aldia: ${data.numPeriodo}

Jaso agur bero bat.
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#list languagePriorities as renderLang>
<@.vars["render${renderLang?capitalize}"] />
<#if renderLang?has_next>

-------------------------------------------------------------------------------

</#if>
</#list>