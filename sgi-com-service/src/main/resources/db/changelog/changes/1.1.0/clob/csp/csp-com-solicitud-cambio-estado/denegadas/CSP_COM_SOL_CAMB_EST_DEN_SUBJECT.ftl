<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Solicitud denegada con carácter definitivo
</#macro>
<#macro renderEn>
<#setting locale="en">
Application definitively rejected
</#macro>
<#macro renderEu>
<#setting locale="eu">
Behin betiko ukatutako eskaera
</#macro>
<#macro renderCa>
<#setting locale="ca">
Sol·licitud denegada de manera definitiva
</#macro>
<@.vars["render${renderLang?capitalize}"] />