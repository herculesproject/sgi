<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Apertura de fase en proyecto
</#macro>
<#macro renderEn>
<#setting locale="en">
Opening of phase in project
</#macro>
<#macro renderEu>
<#setting locale="eu">
Proiektuaren fasearen hasiera
</#macro>
<#macro renderCa>
<#setting locale="ca">
Inici de fase en projecte
</#macro>
<@.vars["render${renderLang?capitalize}"] />