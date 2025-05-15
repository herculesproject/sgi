<#assign data = ETI_COM_DICT_MEM_REV_MINIMA_ARCH_DATA?eval_json />
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
IIEB: eskaera artxibatuta ${data.comiteCodigo}
</#macro>
<@.vars["render${renderLang?capitalize}"] />