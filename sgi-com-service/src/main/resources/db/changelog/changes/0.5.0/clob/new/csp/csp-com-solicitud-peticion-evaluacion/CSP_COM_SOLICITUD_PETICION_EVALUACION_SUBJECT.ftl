<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Necesidad de solicitud de evaluación ante CEID
</#macro>
<#macro renderEn>
<#setting locale="en">
Need to apply to CEID for evaluation
</#macro>
<#macro renderEu>
<#setting locale="eu">
IIEBk ebaluatzeko eskaera
</#macro>
<@.vars["render${renderLang?capitalize}"] />