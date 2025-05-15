<#assign data = CSP_COM_FIN_PERIODO_JUSTIFICACION_SOCIO_DATA?eval />
<#--
  Formato CSP_COM_FIN_PERIODO_JUSTIFICACION_SOCIO_DATA:
  { 
    "titulo": "[{"lang":"es", "value":"Proyecto 1"}]",
    "nombreEntidad": "Entidad Colaboradora",
    "fechaInicio": "2022-01-01T00:00:00Z",
    "fechaFin": "2022-01-31T23:59:59Z",
    "numPeriodo": 2
  }
-->
<#macro renderEs>
<#setting locale="es">
<p>Estimado/a investigador/a,</p>
<p>Le informamos de que proximamente dara inicio el periodo de presentación de justificación del proyecto referenciado más abajo. Puede proceder a realizar la justificación a traves del siguiente enlace: <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a></p>
<p>
- Fecha de finalización de periodo de justificación: ${sgi.formatDate(data.fechaFin, "SHORT")}, ${sgi.formatTime(data.fechaFin, "SHORT")}<br>
- Entidad colaboradora: ${data.nombreEntidad}<br>
- Proyecto: ${sgi.getFieldValue(data.titulo)}<br>
- Periodo: ${data.numPeriodo}
</p>
<p>
Reciba un cordial saludo,<br>
Dirección de gestión de la investigación<br>
convocatorias.dgi@ehu.eus
</p>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>Dear researcher,</p>
<p>Please be advised that the period for submission of justification of the below project will start soon. You can proceed with the justification through the following link: <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a></p>
<p>
- Expiry of the justification period: ${sgi.formatDate(data.fechaFin, "SHORT")}, ${sgi.formatTime(data.fechaFin, "SHORT")}<br>
- Collaborating entity: ${data.nombreEntidad}<br>
- Project: ${sgi.getFieldValue(data.titulo)}<br>
- Period: ${data.numPeriodo}
</p>
<p>
Yours sincerely,<br>
Research Management Directorate<br>
convocatorias.dgi@ehu.eus
</p>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>Ikertzaile agurgarria:</p>
<p>Honen bidez jakinarazten dizugu laster amaituko dela behean aipatutako proiektuaren justifikazioa aurkezteko epea. Honako esteka honen bidez egin dezakezu justifikazioa: <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a></p>
<p>
- Justifikazio epearen amaiera data: ${sgi.formatDate(data.fechaFin, "SHORT")}, ${sgi.formatTime(data.fechaFin, "SHORT")}<br>
- Erakunde kolaboratzailea: ${data.nombreEntidad}<br>
- Proiektua: ${sgi.getFieldValue(data.titulo)}<br>
- Aldia: ${data.numPeriodo}
</p>
<p>
Jaso agur bero bat.<br>
Ikerketa Kudeatzeko Zuzendaritza<br>
convocatorias.dgi@ehu.eus
</p>
</#macro>
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
<#list languagePriorities as renderLang>
<@.vars["render${renderLang?capitalize}"] />
<#if renderLang?has_next>
<hr>
</#if>
</#list>
</body>
</html>