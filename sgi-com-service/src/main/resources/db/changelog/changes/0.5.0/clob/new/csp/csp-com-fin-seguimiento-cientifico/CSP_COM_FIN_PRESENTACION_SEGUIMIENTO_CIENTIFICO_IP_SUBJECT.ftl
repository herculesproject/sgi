<#assign data = CSP_COM_FIN_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP_DATA?eval />
<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Vencimiento cercano de período de presentación de justificación del proyecto: ${sgi.getFieldValue(data.titulo)}
</#macro>
<#macro renderEn>
<#setting locale="en">
Upcoming expiry of period for submission of justification of project expenditure: ${sgi.getFieldValue(data.titulo)}
</#macro>
<#macro renderEu>
<#setting locale="eu">
Proiektuaren justifikazioa aurkezteko epea amaitzear: ${sgi.getFieldValue(data.titulo)}
</#macro>
<@.vars["render${renderLang?capitalize}"] />