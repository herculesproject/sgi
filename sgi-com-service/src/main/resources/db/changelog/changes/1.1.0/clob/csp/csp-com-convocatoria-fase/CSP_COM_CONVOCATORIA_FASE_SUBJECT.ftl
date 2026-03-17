<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Apertura de fase en convocatoria
</#macro>
<#macro renderEn>
<#setting locale="en">
Opening of phase in call
</#macro>
<#macro renderEu>
<#setting locale="eu">
Deialdiaren fasearen hasiera
</#macro>
<#macro renderCa>
<#setting locale="ca">
Obertura de fase en convocatòria
</#macro>
<@.vars["render${renderLang?capitalize}"] />