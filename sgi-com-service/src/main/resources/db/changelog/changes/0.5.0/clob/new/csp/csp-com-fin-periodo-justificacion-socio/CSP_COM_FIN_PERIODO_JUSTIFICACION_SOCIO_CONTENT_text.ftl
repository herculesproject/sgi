<#assign data = CSP_COM_FIN_PERIODO_JUSTIFICACION_SOCIO_DATA?eval />
<#--
  Formato CSP_COM_FIN_PERIODO_JUSTIFICACION_SOCIO_DATA:
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

Le informamos de que proximamente dara inicio el periodo de presentación de justificación del proyecto referenciado más abajo. Puede proceder a realizar la justificación a traves del siguiente enlace: ${data.enlaceAplicacion}

- Fecha de finalización de periodo de justificación: ${sgi.formatDate(data.fechaFin, "SHORT")}, ${sgi.formatTime(data.fechaFin, "SHORT")}
- Entidad colaboradora: ${data.nombreEntidad}
- Proyecto: ${sgi.getFieldValue(data.titulo)}
- Periodo: ${data.numPeriodo}

Reciba un cordial saludo,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear researcher,

Please be advised that the period for submission of justification of the below project will start soon. You can proceed with the justification through the following link: ${data.enlaceAplicacion}

- Expiry of the justification period: ${sgi.formatDate(data.fechaFin, "SHORT")}, ${sgi.formatTime(data.fechaFin, "SHORT")}
- Collaborating entity: ${data.nombreEntidad}
- Project: ${sgi.getFieldValue(data.titulo)}
- Period: ${data.numPeriodo}

Yours sincerely,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:

Honen bidez jakinarazten dizugu laster amaituko dela behean aipatutako proiektuaren justifikazioa aurkezteko epea. Honako esteka honen bidez egin dezakezu justifikazioa: ${data.enlaceAplicacion}

- Justifikazio epearen amaiera data: ${sgi.formatDate(data.fechaFin, "SHORT")}, ${sgi.formatTime(data.fechaFin, "SHORT")}
- Erakunde kolaboratzailea: ${data.nombreEntidad}
- Proiektua: ${sgi.getFieldValue(data.titulo)}
- Aldia: ${data.numPeriodo}

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