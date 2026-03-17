<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Nuevo hito alcanzado en convocatoria
</#macro>
<#macro renderEn>
<#setting locale="en">
New milestone reached in call
</#macro>
<#macro renderEu>
<#setting locale="eu">
Deialdiko mugarri berria
</#macro>
<#macro renderCa>
<#setting locale="ca">
Nova fita assolida en convocatòria
</#macro>
<@.vars["render${renderLang?capitalize}"] />