<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Proceso de baremación finalizado.
</#macro>
<#macro renderEn>
<#setting locale="en">
The ranking process has concluded.
</#macro>
<#macro renderEu>
<#setting locale="eu">
Baremazio prozesua amaituta
</#macro>
<#macro renderCa>
<#setting locale="ca">
Procés de baremació finalitzat
</#macro>
<@.vars["render${renderLang?capitalize}"] />