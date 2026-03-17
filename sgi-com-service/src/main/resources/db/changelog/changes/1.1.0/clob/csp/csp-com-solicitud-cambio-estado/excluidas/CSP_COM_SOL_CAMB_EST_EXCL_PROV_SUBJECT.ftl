<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Solicitud excluida con carácter provisional
</#macro>
<#macro renderEn>
<#setting locale="en">
Application provisionally excluded
</#macro>
<#macro renderEu>
<#setting locale="eu">
Behin-behinean baztertutako eskaera
</#macro>
<#macro renderCa>
<#setting locale="ca">
Sol·licitud exclosa de manera provisional
</#macro>
<@.vars["render${renderLang?capitalize}"] />