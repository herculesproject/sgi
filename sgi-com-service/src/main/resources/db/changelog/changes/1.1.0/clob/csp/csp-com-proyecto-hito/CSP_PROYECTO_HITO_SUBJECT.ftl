<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Nuevo hito de proyecto
</#macro>
<#macro renderEn>
<#setting locale="en">
New project milestone
</#macro>
<#macro renderEu>
<#setting locale="eu">
Eskaerako mugarri berria
</#macro>
<#macro renderCa>
<#setting locale="ca">
Nou esdeveniment del projecte
</#macro>
<@.vars["render${renderLang?capitalize}"] />