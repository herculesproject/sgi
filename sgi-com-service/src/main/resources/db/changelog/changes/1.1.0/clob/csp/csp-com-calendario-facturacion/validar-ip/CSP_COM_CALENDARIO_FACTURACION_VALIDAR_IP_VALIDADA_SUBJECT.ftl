<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Visto bueno para la emisión de factura
</#macro>
<#macro renderEn>
<#setting locale="en">
Approval for issuing an invoice
</#macro>
<#macro renderEu>
<#setting locale="eu">
Faktura jaulkitzeko oniritzia
</#macro>
<#macro renderCa>
<#setting locale="ca">
Vistiplau per emetre la factura
</#macro>
<@.vars["render${renderLang?capitalize}"] />