<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Inicio de período de presentación de justificación socio de proyecto
</#macro>
<#macro renderEn>
<#setting locale="en">
Start of period for submission of justification
</#macro>
<#macro renderEu>
<#setting locale="eu">
Justifikazioa aurkezteko epearen hasiera
</#macro>
<@.vars["render${renderLang?capitalize}"] />