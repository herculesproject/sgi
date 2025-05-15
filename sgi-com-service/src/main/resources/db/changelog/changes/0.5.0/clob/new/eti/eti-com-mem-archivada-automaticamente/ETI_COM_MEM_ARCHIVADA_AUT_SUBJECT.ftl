<#assign data = ETI_COM_MEM_ARCHIVADA_AUT_DATA?eval_json />
<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Solicitud CEID archivada ${data.comiteCodigo}
</#macro>
<#macro renderEn>
<#setting locale="en">
CEID application filed ${data.comiteCodigo}
</#macro>
<#macro renderEu>
<#setting locale="eu">
IIEB: eskaera artxibatua ${data.comiteCodigo}
</#macro>
<@.vars["render${renderLang?capitalize}"] />