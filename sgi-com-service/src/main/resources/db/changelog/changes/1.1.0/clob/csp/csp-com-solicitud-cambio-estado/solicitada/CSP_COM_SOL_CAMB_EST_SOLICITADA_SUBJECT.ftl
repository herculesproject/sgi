<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Registrada solicitud de convocatoria
</#macro>
<#macro renderEn>
<#setting locale="en">
Call request registered
</#macro>
<#macro renderEu>
<#setting locale="eu">
Deialdi eskaera erregistratuta
</#macro>
<#macro renderCa>
<#setting locale="ca">
Sol·licitud de convocatòria registrada
</#macro>
<@.vars["render${renderLang?capitalize}"] />