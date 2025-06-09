<#macro renderEs>
<#setting locale="es">
<p>Estimado/a investigador/a,</p>
<p>Le informamos de que proximamente se alcanzará un nuevo hito en la solicitud asoiada a la convocatoria abajo referenciada:</p>
<p>
- Fecha y hora: ${sgi.formatDate(CSP_HITO_FECHA, "SHORT")}, ${sgi.formatTime(CSP_HITO_FECHA, "SHORT)}<br>
- Hito que se alcanzará: ${sgi.getFieldValue(CSP_HITO_TIPO)}<br>
- Solicitud: ${sgi.getFieldValue(CSP_SOLICITUD_TITULO)}<br>
- Convocatoria: ${sgi.getFieldValue(CSP_CONVOCATORIA_TITULO)}
</p>
<#if CSP_HITO_OBSERVACIONES?has_content && && CSP_HITO_OBSERVACIONES != "[]">
<p>En el hito se han indicado las siguientes observaciones:</p>
<p>${sgi.getFieldValue(CSP_HITO_OBSERVACIONES)}</p>
</#if>
<p>
Reciba un cordial saludo,<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
</p>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>Dear Researcher,</p>
<p>Please be advised that a new milestone in the application associated to the below call will soon be reached:</p>
<p>
- Date and time: ${sgi.formatDate(CSP_HITO_FECHA, "SHORT")}, ${sgi.formatTime(CSP_HITO_FECHA, "SHORT)}<br>
- Milestone to be reached: ${sgi.getFieldValue(CSP_HITO_TIPO)}<br>
- Application: ${sgi.getFieldValue(CSP_SOLICITUD_TITULO)}<br>
- Call: ${sgi.getFieldValue(CSP_CONVOCATORIA_TITULO)}
</p>
<#if CSP_HITO_OBSERVACIONES?has_content && && CSP_HITO_OBSERVACIONES != "[]">
<p>The following remarks have been indicated at the milestone:</p>
${CSP_HITO_OBSERVACIONES}
</#if>
<p>
Yours sincerely,<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
</p>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>Ikertzaile agurgarria:</p>
<p>Honen bidez jakinarazten dizugu laster mugarri berri batera iritsiko dela aipatuta ageri den deialdiari lotutako eskaera:</p>
<p>
- Data eta ordua: ${sgi.formatDate(CSP_HITO_FECHA, "SHORT")}, ${sgi.formatTime(CSP_HITO_FECHA, "SHORT)}<br>
- Mugarri berria: ${sgi.getFieldValue(CSP_HITO_TIPO)}<br>
- Eskaera: ${sgi.getFieldValue(CSP_SOLICITUD_TITULO)}<br>
- Deialdia: ${sgi.getFieldValue(CSP_CONVOCATORIA_TITULO)}
</p>
<#if CSP_HITO_OBSERVACIONES?has_content && && CSP_HITO_OBSERVACIONES != "[]">
<p>Mugarrian ohar hauek adierazi dira:</p>
<p>${CSP_HITO_OBSERVACIONES}</p>
</#if>
<p>
Jaso agur bero bat.<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
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