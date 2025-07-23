<#ftl output_format="HTML">
<#macro renderEs>
<#setting locale="es">
<p>Estimado/a investigador/a,</p>
<p>Le informamdos de que se ha alcanzado el siguiente hito de la convocatoria referenciada:</p>
<p>
- Hito: <#if CSP_HITO_TIPO?has_content && CSP_HITO_TIPO != "[]">${sgi.getFieldValue(CSP_HITO_TIPO)}<#else>-</#if><br>
- Convocatoria: ${sgi.getFieldValue(CSP_CONVOCATORIA_TITULO)}<br>
- Fecha y hora: ${sgi.formatDate(CSP_HITO_FECHA, "SHORT")}, ${sgi.formatTime(CSP_HITO_FECHA, "SHORT")}
</p>
<#if CSP_HITO_OBSERVACIONES?has_content && CSP_HITO_OBSERVACIONES != "[]">
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
<p>Please be advised that the following milestone of the call in question has been reached:</p>
<p>
- Milestone: <#if CSP_HITO_TIPO?has_content && CSP_HITO_TIPO != "[]">${sgi.getFieldValue(CSP_HITO_TIPO)}<#else>-</#if><br>
- Call: ${sgi.getFieldValue(CSP_CONVOCATORIA_TITULO)}<br>
- Date and time: ${sgi.formatDate(CSP_HITO_FECHA, "SHORT")}, ${sgi.formatTime(CSP_HITO_FECHA, "SHORT")}
</p>
<#if CSP_HITO_OBSERVACIONES?has_content && CSP_HITO_OBSERVACIONES != "[]">
<p>The following remarks have been indicated at the milestone:</p>
<p>${sgi.getFieldValue(CSP_HITO_OBSERVACIONES)}</p>
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
<p>Honen bidez jakinarazten dizugu behean aipatutako deialdia hurrengo mugarrira iritsi dela:</p>
<p>
- Mugarria: <#if CSP_HITO_TIPO?has_content && CSP_HITO_TIPO != "[]">${sgi.getFieldValue(CSP_HITO_TIPO)}<#else>-</#if><br>
- Deialdia: ${sgi.getFieldValue(CSP_CONVOCATORIA_TITULO)}<br>
- Data eta ordua: ${sgi.formatDate(CSP_HITO_FECHA, "SHORT")}, ${sgi.formatTime(CSP_HITO_FECHA, "SHORT")}
</p>
<#if CSP_HITO_OBSERVACIONES?has_content && CSP_HITO_OBSERVACIONES != "[]">
<p>ugarrian ohar hauek adierazi dira:</p>
<p>${sgi.getFieldValue(CSP_HITO_OBSERVACIONES)}</p>
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