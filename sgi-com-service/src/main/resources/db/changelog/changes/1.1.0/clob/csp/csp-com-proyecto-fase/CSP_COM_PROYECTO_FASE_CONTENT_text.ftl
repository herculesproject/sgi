<#macro renderEs>
<#setting locale="es">
Le informamos de que próximamente dará comienzo la siguiente fase del proyecto abajo referenciado:

- Convocatoria: <#if CSP_PRO_FASE_TITULO_CONVOCATORIA?has_content && CSP_PRO_FASE_TITULO_CONVOCATORIA != "[]">${sgi.getFieldValue(CSP_PRO_FASE_TITULO_CONVOCATORIA)}<#else>-</#if>
- Fase: <#if CSP_PRO_TIPO_FASE?has_content && CSP_PRO_TIPO_FASE != "[]">${sgi.getFieldValue(CSP_PRO_TIPO_FASE)}<#else>-</#if>
- Proyecto: ${sgi.getFieldValue(CSP_PRO_FASE_TITULO_PROYECTO)}
- Apertura: ${sgi.formatDate(CSP_PRO_FASE_FECHA_INICIO, "SHORT")}, ${sgi.formatTime(CSP_PRO_FASE_FECHA_INICIO, "SHORT")}
- Cierre: ${sgi.formatDate(CSP_PRO_FASE_FECHA_FIN, "SHORT")}, ${sgi.formatTime(CSP_PRO_FASE_FECHA_FIN, "SHORT")}

<#if CSP_PRO_FASE_OBSERVACIONES?has_content && CSP_PRO_FASE_OBSERVACIONES != "[]">
En esta fase, se han indicado las siguientes observaciones:
${sgi.getFieldValue(CSP_PRO_FASE_OBSERVACIONES)}
</#if>
</#macro>
<#macro renderEn>
<#setting locale="en">
You are informed that the next phase of the project referred to below will soon begin:

- Call: <#if CSP_PRO_FASE_TITULO_CONVOCATORIA?has_content && CSP_PRO_FASE_TITULO_CONVOCATORIA != "[]">${sgi.getFieldValue(CSP_PRO_FASE_TITULO_CONVOCATORIA)}<#else>-</#if>
- Phase: <#if CSP_PRO_TIPO_FASE?has_content && CSP_PRO_TIPO_FASE != "[]">${sgi.getFieldValue(CSP_PRO_TIPO_FASE)}<#else>-</#if>
- Project: ${sgi.getFieldValue(CSP_PRO_FASE_TITULO_PROYECTO)}
- Opening: ${sgi.formatDate(CSP_PRO_FASE_FECHA_INICIO, "SHORT")}, ${sgi.formatTime(CSP_PRO_FASE_FECHA_INICIO, "SHORT")}
- Closing: ${sgi.formatDate(CSP_PRO_FASE_FECHA_FIN, "SHORT")}, ${sgi.formatTime(CSP_PRO_FASE_FECHA_FIN, "SHORT")}

<#if CSP_PRO_FASE_OBSERVACIONES?has_content && CSP_PRO_FASE_OBSERVACIONES != "[]">
The following remarks were made in this phase:
${sgi.getFieldValue(CSP_PRO_FASE_OBSERVACIONES)}
</#if>
</#macro>
<#macro renderEu>
<#setting locale="eu">
Jakinarazten dizugu laster hasiko dela behean aipatutako proiektuaren hurrengo fasea:

- Deialdia: <#if CSP_PRO_FASE_TITULO_CONVOCATORIA?has_content && CSP_PRO_FASE_TITULO_CONVOCATORIA != "[]">${sgi.getFieldValue(CSP_PRO_FASE_TITULO_CONVOCATORIA)}<#else>-</#if>
- Fasea: <#if CSP_PRO_TIPO_FASE?has_content && CSP_PRO_TIPO_FASE != "[]">${sgi.getFieldValue(CSP_PRO_TIPO_FASE)}<#else>-</#if>
- Proiektua: ${sgi.getFieldValue(CSP_PRO_FASE_TITULO_PROYECTO)}
- Hasiera: ${sgi.formatDate(CSP_PRO_FASE_FECHA_INICIO, "SHORT")}, ${sgi.formatTime(CSP_PRO_FASE_FECHA_INICIO, "SHORT")}
- Amaiera: ${sgi.formatDate(CSP_PRO_FASE_FECHA_FIN, "SHORT")}, ${sgi.formatTime(CSP_PRO_FASE_FECHA_FIN, "SHORT")}

<#if CSP_PRO_FASE_OBSERVACIONES?has_content && CSP_PRO_FASE_OBSERVACIONES != "[]">
Fase horretan ohar hauek adierazi dira:
${sgi.getFieldValue(CSP_PRO_FASE_OBSERVACIONES)}
</#if>
</#macro>

<#macro renderCa>
<#setting locale="ca">
Benvolgut/da investigador/a,

Us informem que properament començarà la següent fase del projecte baix referenciat:

- Convocatòria: <#if CSP_PRO_FASE_TITULO_CONVOCATORIA?has_content && CSP_PRO_FASE_TITULO_CONVOCATORIA != "[]">${sgi.getFieldValue(CSP_PRO_FASE_TITULO_CONVOCATORIA)}<#else>-</#if>
- Fase: <#if CSP_PRO_TIPO_FASE?has_content && CSP_PRO_TIPO_FASE != "[]">${sgi.getFieldValue(CSP_PRO_TIPO_FASE)}<#else>-</#if>
- Projecte: ${sgi.getFieldValue(CSP_PRO_FASE_TITULO_PROYECTO)}
- Obertura: ${sgi.formatDate(CSP_PRO_FASE_FECHA_INICIO, "SHORT")}, ${sgi.formatTime(CSP_PRO_FASE_FECHA_INICIO, "SHORT")}
- Tancament: ${sgi.formatDate(CSP_PRO_FASE_FECHA_FIN, "SHORT")}, ${sgi.formatTime(CSP_PRO_FASE_FECHA_FIN, "SHORT")}

<#if CSP_PRO_FASE_OBSERVACIONES?has_content && CSP_PRO_FASE_OBSERVACIONES != "[]">
En aquesta fase, s'han indicat les següents observacions:
${sgi.getFieldValue(CSP_PRO_FASE_OBSERVACIONES)}
</#if>

Rebi una cordial salutació,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>

<#list languagePriorities as renderLang>
<@.vars["render${renderLang?capitalize}"] />
<#if renderLang?has_next>

-------------------------------------------------------------------------------

</#if>
</#list>