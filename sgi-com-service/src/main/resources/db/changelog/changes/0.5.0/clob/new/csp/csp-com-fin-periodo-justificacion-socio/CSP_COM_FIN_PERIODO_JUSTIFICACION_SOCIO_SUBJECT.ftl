<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Vencimiento cercano de período de presentación de justificación socio de proyecto
</#macro>
<#macro renderEn>
<#setting locale="en">
Upcoming expiry of period for submission of justification
</#macro>
<#macro renderEu>
<#setting locale="eu">
Justifikazioa aurkezteko epea amaitzear
</#macro>
<@.vars["render${renderLang?capitalize}"] />