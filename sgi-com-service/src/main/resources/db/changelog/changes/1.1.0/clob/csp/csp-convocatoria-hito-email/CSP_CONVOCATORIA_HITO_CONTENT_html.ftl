<#ftl output_format="HTML">
<#macro renderEs>
<#setting locale="es">
<p>Le informamos de que se ha alcanzado el siguiente hito de la convocatoria referenciada:</p>
<p>
- Hito: <#if CSP_HITO_TIPO?has_content && CSP_HITO_TIPO != "[]">${sgi.getFieldValue(CSP_HITO_TIPO)}<#else>-</#if><br>
- Convocatoria: <#if CSP_CONVOCATORIA_TITULO?has_content && CSP_CONVOCATORIA_TITULO != "[]">${sgi.getFieldValue(CSP_CONVOCATORIA_TITULO)}<#else>-</#if><br>
- Fecha y hora: ${sgi.formatDate(CSP_HITO_FECHA, "SHORT")}, ${sgi.formatTime(CSP_HITO_FECHA, "SHORT")}
</p>
<#if CSP_HITO_OBSERVACIONES?has_content && CSP_HITO_OBSERVACIONES != "[]">
<p>En el hito se han indicado las siguientes observaciones:</p>
<p>${sgi.getFieldValue(CSP_HITO_OBSERVACIONES)}</p>
</#if>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>Please be advised that the following milestone of the call in question has been reached:</p>
<p>
- Milestone: <#if CSP_HITO_TIPO?has_content && CSP_HITO_TIPO != "[]">${sgi.getFieldValue(CSP_HITO_TIPO)}<#else>-</#if><br>
- Call: <#if CSP_CONVOCATORIA_TITULO?has_content && CSP_CONVOCATORIA_TITULO != "[]">${sgi.getFieldValue(CSP_CONVOCATORIA_TITULO)}<#else>-</#if><br>
- Date and time: ${sgi.formatDate(CSP_HITO_FECHA, "SHORT")}, ${sgi.formatTime(CSP_HITO_FECHA, "SHORT")}
</p>
<#if CSP_HITO_OBSERVACIONES?has_content && CSP_HITO_OBSERVACIONES != "[]">
<p>The following remarks have been indicated at the milestone:</p>
<p>${sgi.getFieldValue(CSP_HITO_OBSERVACIONES)}</p>
</#if>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>Honen bidez jakinarazten dizugu behean aipatutako deialdia hurrengo mugarrira iritsi dela:</p>
<p>
- Mugarria: <#if CSP_HITO_TIPO?has_content && CSP_HITO_TIPO != "[]">${sgi.getFieldValue(CSP_HITO_TIPO)}<#else>-</#if><br>
- Deialdia: <#if CSP_CONVOCATORIA_TITULO?has_content && CSP_CONVOCATORIA_TITULO != "[]">${sgi.getFieldValue(CSP_CONVOCATORIA_TITULO)}<#else>-</#if><br>
- Data eta ordua: ${sgi.formatDate(CSP_HITO_FECHA, "SHORT")}, ${sgi.formatTime(CSP_HITO_FECHA, "SHORT")}
</p>
<#if CSP_HITO_OBSERVACIONES?has_content && CSP_HITO_OBSERVACIONES != "[]">
<p>ugarrian ohar hauek adierazi dira:</p>
<p>${sgi.getFieldValue(CSP_HITO_OBSERVACIONES)}</p>
</#if>
</#macro>

<#macro renderCa>
<#setting locale="ca">
<p>Benvolgut/da investigador/a,</p>
<p>Us informem que s'ha aconseguit la següent fita de la convocatòria referenciada:</p>
<p>
- Fita: <#if CSP_HITO_TIPO?has_content && CSP_HITO_TIPO != "[]">${sgi.getFieldValue(CSP_HITO_TIPO)}<#else>-</#if><br>
- Convocatòria: <#if CSP_CONVOCATORIA_TITULO?has_content && CSP_CONVOCATORIA_TITULO != "[]">${sgi.getFieldValue(CSP_CONVOCATORIA_TITULO)}<#else>-</#if><br>
- Data i hora: ${sgi.formatDate(CSP_HITO_FECHA, "SHORT")}, ${sgi.formatTime(CSP_HITO_FECHA, "SHORT")}
</p>
<#if CSP_HITO_OBSERVACIONES?has_content && CSP_HITO_OBSERVACIONES != "[]">
<p>A la fita s'han indicat les següents observacions:</p>
<p>${sgi.getFieldValue(CSP_HITO_OBSERVACIONES)}</p>
</#if>
<p>
Rebi una salutació cordial,<br>
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