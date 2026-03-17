<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Nuevo proyecto registrado en CVN
</#macro>
<#macro renderEn>
<#setting locale="en">
New project registered in CVN
</#macro>
<#macro renderEu>
<#setting locale="eu">
Proiektu berria erregistratuta CVNan
</#macro>
<#macro renderCa>
<#setting locale="ca">
Projecte nou registrat a CVN
</#macro>
<@.vars["render${renderLang?capitalize}"] />