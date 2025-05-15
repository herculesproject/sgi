<#assign data = CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP_DATA?eval />
<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Inicio de período de presentación de justificación del proyecto: ${sgi.getFieldValue(data.titulo)}
</#macro>
<#macro renderEn>
<#setting locale="en">
Start of period for submission of justification of project expenditure: ${sgi.getFieldValue(data.titulo)}
</#macro>
<#macro renderEu>
<#setting locale="eu">
Proiektuaren justifikazioa aurkezteko epearen hasiera: ${sgi.getFieldValue(data.titulo)}
</#macro>
<@.vars["render${renderLang?capitalize}"] />