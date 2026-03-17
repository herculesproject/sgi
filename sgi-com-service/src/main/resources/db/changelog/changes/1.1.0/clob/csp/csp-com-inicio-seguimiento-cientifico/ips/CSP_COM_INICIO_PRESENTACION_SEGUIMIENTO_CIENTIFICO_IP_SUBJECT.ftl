<#assign data = CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP_DATA?eval />
<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Inicio del período de presentación de la documentación de seguimiento científico del proyecto: ${sgi.getFieldValue(data.titulo)}
</#macro>
<#macro renderEn>
<#setting locale="en">
Start of the submission period for scientific monitoring documentation on the project: ${sgi.getFieldValue(data.titulo)}
</#macro>
<#macro renderEu>
<#setting locale="eu">
Proiektuaren jarraipen zientifikoari buruzko dokumentazioa aurkezteko epearen hasiera: ${sgi.getFieldValue(data.titulo)}
</#macro>
<#macro renderCa>
<#setting locale="ca">
Inici del període de presentació de la documentació de seguiment científic del projecte: ${sgi.getFieldValue(data.titulo)}
</#macro>
<@.vars["render${renderLang?capitalize}"] />