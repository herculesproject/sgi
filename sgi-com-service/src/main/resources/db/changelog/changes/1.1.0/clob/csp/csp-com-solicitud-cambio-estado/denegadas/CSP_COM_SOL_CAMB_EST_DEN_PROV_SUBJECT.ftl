<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Solicitud denegada con carácter provisional
</#macro>
<#macro renderEn>
<#setting locale="en">
Application provisionally rejected
</#macro>
<#macro renderEu>
<#setting locale="eu">
Behin-behinean ukatutako eskaera
</#macro>
<#macro renderCa>
<#setting locale="ca">
Sol·licitud provisionalment denegada
</#macro>
<@.vars["render${renderLang?capitalize}"] />