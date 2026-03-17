<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Próxima fecha límite para un procedimiento
</#macro>
<#macro renderEn>
<#setting locale="en">
Upcoming deadline for a procedure
</#macro>
<#macro renderEu>
<#setting locale="eu">
Prozedura bat egiteko azken eguna
</#macro>
<#macro renderCa>
<#setting locale="ca">
Propera data límit per a un procediment
</#macro>
<@.vars["render${renderLang?capitalize}"] />