<#macro renderEs>
<#setting locale="es">
<p>Estimado/a investigador/a,</p>
<p>Le informamos de que próximamente se abrirá la siguiente fase de la convocatoria abajo referenciada:</p>
<p>
- Convocatoria: ${sgi.getFieldValue(CSP_CONV_FASE_TITULO)}<br>
- Fase: ${sgi.getFieldValue(CSP_CONV_TIPO_FASE)}<br>
- Apertura: ${sgi.formatDate(CSP_CONV_FASE_FECHA_INICIO, "SHORT")} a las ${sgi.formatTime(CSP_CONV_FASE_FECHA_INICIO, "SHORT")}<br>
- Cierre: ${sgi.formatDate(CSP_CONV_FASE_FECHA_FIN, "SHORT")} a las ${sgi.formatTime(CSP_CONV_FASE_FECHA_FIN, "SHORT")}
</p>
<p>
En dicha fase se han indicado las siguientes observaciones:<br>
${sgi.getFieldValue(CSP_CONV_FASE_OBSERVACIONES)}
</p>
<p>
Reciba un cordial saludo,<br>
Dirección de gestión de la investigación<br>
convocatorias.dgi@ehu.eus
</p>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>Dear Researcher,</p>
<p>Please be advised that the next stage of the below call will be launched shortly:</p>
<p>
- Call: ${sgi.getFieldValue(CSP_CONV_FASE_TITULO)}<br>
- Phase: ${sgi.getFieldValue(CSP_CONV_TIPO_FASE)}<br>
- Opening: ${sgi.formatDate(CSP_CONV_FASE_FECHA_INICIO, "SHORT")} at ${sgi.formatTime(CSP_CONV_FASE_FECHA_INICIO, "SHORT")}<br>
- Closing: ${sgi.formatDate(CSP_CONV_FASE_FECHA_FIN, "SHORT")} at ${sgi.formatTime(CSP_CONV_FASE_FECHA_FIN, "SHORT")}
</p>
<p>
The following remarks were made in this phase:<br>
${sgi.getFieldValue(CSP_CONV_FASE_OBSERVACIONES)}
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
<p>Honen bidez jakinarazten dizugu laster hasiko dela behean aipatutako deialdiaren hurrengo fasea:</p>
<p>
- Deialdia: ${sgi.getFieldValue(CSP_CONV_FASE_TITULO)}<br>
- Fasea: ${sgi.getFieldValue(CSP_CONV_TIPO_FASE)}<br>
- Hasiera: ${sgi.formatDate(CSP_CONV_FASE_FECHA_INICIO, "SHORT")}, ${sgi.formatTime(CSP_CONV_FASE_FECHA_INICIO, "SHORT")}<br>
- Amaiera: ${sgi.formatDate(CSP_CONV_FASE_FECHA_FIN, "SHORT")}, ${sgi.formatTime(CSP_CONV_FASE_FECHA_FIN, "SHORT")}
</p>
<p>
Fase horretan ohar hauek adierazi dira:<br>
${sgi.getFieldValue(CSP_CONV_FASE_OBSERVACIONES)}
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