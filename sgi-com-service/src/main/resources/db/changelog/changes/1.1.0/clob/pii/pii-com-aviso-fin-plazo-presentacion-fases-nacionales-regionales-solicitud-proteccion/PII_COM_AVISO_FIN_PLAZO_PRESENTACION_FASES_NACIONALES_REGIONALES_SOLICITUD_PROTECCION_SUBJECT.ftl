<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Próximo vencimiento de Entrada de invención en fases nacionales/regionales
</#macro>
<#macro renderEn>
<#setting locale="en">
Upcoming deadline for Entry of invention in national/regional phases
</#macro>
<#macro renderEu>
<#setting locale="eu">
Asmakizuna nazio/eskualde mailako faseetan sartzeko epea amaitzear
</#macro>
<#macro renderCa>
<#setting locale="ca">
Propera data límit per a l'entrada de la invenció a fases nacionals/regionals
</#macro>
<@.vars["render${renderLang?capitalize}"] />