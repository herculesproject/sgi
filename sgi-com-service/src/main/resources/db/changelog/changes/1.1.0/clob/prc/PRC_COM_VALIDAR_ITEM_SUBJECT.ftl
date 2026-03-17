<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Nuevo ítem disponible para su validación
</#macro>
<#macro renderEn>
<#setting locale="en">
New item available for validation
</#macro>
<#macro renderEu>
<#setting locale="eu">
Baliozkotzeko item berria 
</#macro>
<#macro renderCa>
<#setting locale="ca">
Nou ítem disponible per a validació
</#macro>
<@.vars["render${renderLang?capitalize}"] />