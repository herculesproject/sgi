<#assign data = PII_COM_MESES_HASTA_FIN_PRIORIDAD_SOLICITUD_PROTECCION_DATA?eval />
<#macro renderEs>
<#setting locale="es">
Estimado/a investigador/a

Le informamos de la próxima finalización del plazo de prioridad para la extensión de la invención de referencia:

- Meses restantes: ${data.monthsBeforeFechaFinPrioridad} 
- Fecha de finalización el plazo de prioridad: ${sgi.formatDate(data.fechaFinPrioridad, "SHORT")}
- Título: ${sgi.getFieldValue(data.solicitudTitle)}

Reciba un cordial saludo,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear Researcher

Please be advised that the priority period for the extension of the following invention is about to expire:

- Remaining months: ${data.monthsBeforeFechaFinPrioridad} 
- Date of expiry of the priority period: ${sgi.formatDate(data.fechaFinPrioridad, "SHORT")}
- Title: ${sgi.getFieldValue(data.solicitudTitle)}

Yours sincerely,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:

Honen bidez jakinarazten dizugu laster amaituko dela behean aipatutako asmakizunaren hedapenerako lehentasun epea: 

- Epea amaitzeko geratzen diren hilabeteak: ${data.monthsBeforeFechaFinPrioridad} 
- Lehentasun epearen amaiera data: ${sgi.formatDate(data.fechaFinPrioridad, "SHORT")}
- Izenburua: ${sgi.getFieldValue(data.solicitudTitle)}

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