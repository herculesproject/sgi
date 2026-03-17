<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Notificación de solicitud validada
</#macro>
<#macro renderEn>
<#setting locale="en">
Notification of validated application
</#macro>
<#macro renderEu>
<#setting locale="eu">
Eskaera baliozkotu delako jakinarazpena
</#macro>
<#macro renderCa>
<#setting locale="ca">
Notificació de sol·licitud validada
</#macro>
<@.vars["render${renderLang?capitalize}"] />