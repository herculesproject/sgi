<#assign data = PII_COM_MESES_HASTA_FIN_PRIORIDAD_SOLICITUD_PROTECCION_DATA?eval />
<#macro renderEs>
<#setting locale="es">
<p>Estimado/a investigador/a</p>
<p>Le informamos de la próxima finalización del plazo de prioridad para la extensión de la invención de referencia:</p>
<p>
- Meses restantes: ${data.monthsBeforeFechaFinPrioridad}<br>
- Fecha de finalización el plazo de prioridad: ${sgi.formatDate(data.fechaFinPrioridad, "SHORT")}<br>
- Título: ${sgi.getFieldValue(data.solicitudTitle)}
</p>
<p>
Reciba un cordial saludo,<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
</p>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>Dear Researcher</p>
<p>Please be advised that the priority period for the extension of the following invention is about to expire:</p>
<p>
- Remaining months: ${data.monthsBeforeFechaFinPrioridad}<br>
- Date of expiry of the priority period: ${sgi.formatDate(data.fechaFinPrioridad, "SHORT")}<br>
- Title: ${sgi.getFieldValue(data.solicitudTitle)}
</p>
<p>
Yours sincerely,<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
</p>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>Ikertzaile agurgarria:</p>
<p>Honen bidez jakinarazten dizugu laster amaituko dela behean aipatutako asmakizunaren hedapenerako lehentasun epea:</p>
<p>
- Epea amaitzeko geratzen diren hilabeteak: ${data.monthsBeforeFechaFinPrioridad}<br>
- Lehentasun epearen amaiera data: ${sgi.formatDate(data.fechaFinPrioridad, "SHORT")}<br>
- Izenburua: ${sgi.getFieldValue(data.solicitudTitle)}
</p>
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