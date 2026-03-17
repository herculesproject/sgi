<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Notificación de solicitud rechazada
</#macro>
<#macro renderEn>
<#setting locale="en">
Notification of rejected application
</#macro>
<#macro renderEu>
<#setting locale="eu">
Eskaera ukatu delako jakinarazpena 
</#macro>
<#macro renderCa>
<#setting locale="ca">
Notificació de sol·licitud rebutjada
</#macro>
<@.vars["render${renderLang?capitalize}"] />