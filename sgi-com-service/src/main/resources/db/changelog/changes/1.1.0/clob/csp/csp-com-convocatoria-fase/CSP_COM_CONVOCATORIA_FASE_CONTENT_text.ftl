<#macro renderEs>
<#setting locale="es">
Le informamos de que próximamente se abrirá la siguiente fase de la convocatoria abajo referenciada:

- Convocatoria: <#if CSP_CONV_FASE_TITULO?has_content && CSP_CONV_FASE_TITULO != "[]">${sgi.getFieldValue(CSP_CONV_FASE_TITULO)}<#else>-</#if>
- Fase: <#if CSP_CONV_TIPO_FASE?has_content && CSP_CONV_TIPO_FASE != "[]">${sgi.getFieldValue(CSP_CONV_TIPO_FASE)}<#else>-</#if>
- Apertura: ${sgi.formatDate(CSP_CONV_FASE_FECHA_INICIO, "SHORT")} a las ${sgi.formatTime(CSP_CONV_FASE_FECHA_INICIO, "SHORT")}
- Cierre: ${sgi.formatDate(CSP_CONV_FASE_FECHA_FIN, "SHORT")} a las ${sgi.formatTime(CSP_CONV_FASE_FECHA_FIN, "SHORT")}

<#if CSP_CONV_FASE_OBSERVACIONES?has_content && CSP_CONV_FASE_OBSERVACIONES != "[]">
En dicha fase se han indicado las siguientes observaciones:
${sgi.getFieldValue(CSP_CONV_FASE_OBSERVACIONES)}
</#if>
</#macro>
<#macro renderEn>
<#setting locale="en">
Please be advised that the next stage of the below call will be launched shortly:

- Call: <#if CSP_CONV_FASE_TITULO?has_content && CSP_CONV_FASE_TITULO != "[]">${sgi.getFieldValue(CSP_CONV_FASE_TITULO)}<#else>-</#if>
- Phase: <#if CSP_CONV_TIPO_FASE?has_content && CSP_CONV_TIPO_FASE != "[]">${sgi.getFieldValue(CSP_CONV_TIPO_FASE)}<#else>-</#if>
- Opening: ${sgi.formatDate(CSP_CONV_FASE_FECHA_INICIO, "SHORT")} at ${sgi.formatTime(CSP_CONV_FASE_FECHA_INICIO, "SHORT")}
- Closing: ${sgi.formatDate(CSP_CONV_FASE_FECHA_FIN, "SHORT")} at ${sgi.formatTime(CSP_CONV_FASE_FECHA_FIN, "SHORT")}

<#if CSP_CONV_FASE_OBSERVACIONES?has_content && CSP_CONV_FASE_OBSERVACIONES != "[]">
The following remarks were made in this phase:
${sgi.getFieldValue(CSP_CONV_FASE_OBSERVACIONES)}
</#if>
</#macro>
<#macro renderEu>
<#setting locale="eu">
Honen bidez jakinarazten dizugu laster hasiko dela behean aipatutako deialdiaren hurrengo fasea:

- Deialdia: <#if CSP_CONV_FASE_TITULO?has_content && CSP_CONV_FASE_TITULO != "[]">${sgi.getFieldValue(CSP_CONV_FASE_TITULO)}<#else>-</#if>
- Fasea: <#if CSP_CONV_TIPO_FASE?has_content && CSP_CONV_TIPO_FASE != "[]">${sgi.getFieldValue(CSP_CONV_TIPO_FASE)}<#else>-</#if>
- Hasiera: ${sgi.formatDate(CSP_CONV_FASE_FECHA_INICIO, "SHORT")}, ${sgi.formatTime(CSP_CONV_FASE_FECHA_INICIO, "SHORT")}
- Amaiera: ${sgi.formatDate(CSP_CONV_FASE_FECHA_FIN, "SHORT")}, ${sgi.formatTime(CSP_CONV_FASE_FECHA_FIN, "SHORT")}

<#if CSP_CONV_FASE_OBSERVACIONES?has_content && CSP_CONV_FASE_OBSERVACIONES != "[]">
Fase horretan ohar hauek adierazi dira:
${sgi.getFieldValue(CSP_CONV_FASE_OBSERVACIONES)}

</#if>
</#macro>

<#macro renderCa>
<#setting locale="ca">
Benvolgut/da investigador/a,

Us informem que properament s'obrirà la següent fase de la convocatòria a baix referenciada:

- Convocatòria: <#if CSP_CONV_FASE_TITULO?has_content && CSP_CONV_FASE_TITULO != "[]">${sgi.getFieldValue(CSP_CONV_FASE_TITULO)}<#else>-</#if>
- Fase: <#if CSP_CONV_TIPO_FASE?has_content && CSP_CONV_TIPO_FASE != "[]">${sgi.getFieldValue(CSP_CONV_TIPO_FASE)}<#else>-</#if>
- Obertura: ${CSP_CONV_FASE_FECHA_INICIO?string("dd/MM/yyyy")} a les ${CSP_CONV_FASE_FECHA_INICIO?string("HH:mm")}h
- Tancament: ${CSP_CONV_FASE_FECHA_FIN?string("dd/MM/yyyy")} a les ${CSP_CONV_FASE_FECHA_FIN?string("HH:mm")}h

<#if CSP_CONV_FASE_OBSERVACIONES?has_content && CSP_CONV_FASE_OBSERVACIONES != "[]">
En aquesta fase s'han indicat les següents observacions:
${sgi.getFieldValue(CSP_CONV_FASE_OBSERVACIONES)}
</#if>

Rebi una salutació cordial,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>

<#list languagePriorities as renderLang>
<@.vars["render${renderLang?capitalize}"] />
<#if renderLang?has_next>

-------------------------------------------------------------------------------

</#if>
</#list>