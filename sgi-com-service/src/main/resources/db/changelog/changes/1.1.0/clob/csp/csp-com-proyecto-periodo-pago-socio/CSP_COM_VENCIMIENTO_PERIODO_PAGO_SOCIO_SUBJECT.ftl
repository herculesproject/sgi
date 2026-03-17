<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Vencimiento cercano de período de pago del proyecto
</#macro>
<#macro renderEn>
<#setting locale="en">
Upcoming expiry of project payment period
</#macro>
<#macro renderEu>
<#setting locale="eu">
Proiektuko ordainketa epea amaitzear
</#macro>
<#macro renderCa>
<#setting locale="ca">
Proper venciment del període de pagament del projecte
</#macro>
<@.vars["render${renderLang?capitalize}"] />