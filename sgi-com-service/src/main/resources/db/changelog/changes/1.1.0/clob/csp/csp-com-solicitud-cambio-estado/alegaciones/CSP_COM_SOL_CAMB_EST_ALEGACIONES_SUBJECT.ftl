<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Presentadas alegaciones a convocatoria
</#macro>
<#macro renderEn>
<#setting locale="en">
Allegations to the call submitted
</#macro>
<#macro renderEu>
<#setting locale="eu">
Deialdiari alegazioak aurkeztu dizkiote
</#macro>
<#macro renderCa>
<#setting locale="ca">
Al·legacions a la convocatòria presentades
</#macro>
<@.vars["render${renderLang?capitalize}"] />