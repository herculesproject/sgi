<#assign data = ETI_COM_DICT_EVA_SEG_REV_MINIMA_DATA?eval_json />
<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Informe de evaluación: ${data.comiteCodigo}
</#macro>
<#macro renderEn>
<#setting locale="en">
Evaluation report: ${data.comiteCodigo}
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ebaluazio txostena: ${data.comiteCodigo}
</#macro>
<#macro renderCa>
<#setting locale="ca">
Informe d'avaluació: ${data.comiteCodigo}
</#macro>
<@.vars["render${renderLang?capitalize}"] />