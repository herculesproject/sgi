<#macro renderEs>
<#setting locale="es">
Le informamos que próximamente se alcanzará un nuevo hito en el proyecto abajo referenciado:

- Fecha y hora del nuevo hito: ${sgi.formatDate(CSP_HITO_FECHA, "SHORT")}, ${sgi.formatTime(CSP_HITO_FECHA, "SHORT")}
- Hito que se alcanzará: <#if CSP_HITO_TIPO?has_content && CSP_HITO_TIPO != "[]">${sgi.getFieldValue(CSP_HITO_TIPO)}<#else>-</#if>
- Proyecto: ${sgi.getFieldValue(CSP_PROYECTO_TITULO)}
- Convocatoria: <#if CSP_CONVOCATORIA_TITULO?has_content && CSP_CONVOCATORIA_TITULO != "[]">${sgi.getFieldValue(CSP_CONVOCATORIA_TITULO)}<#else>-</#if>

<#if CSP_HITO_OBSERVACIONES?has_content && CSP_HITO_OBSERVACIONES != "[]">
En el hito se han indicado las siguientes observaciones:
${sgi.getFieldValue(CSP_HITO_OBSERVACIONES)}
</#if>

Reciba un cordial saludo,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEn>
<#setting locale="en">
You are informed that a new milestone in the project referred to below will soon be reached:

- Date and time: ${sgi.formatDate(CSP_HITO_FECHA, "SHORT")}, ${sgi.formatTime(CSP_HITO_FECHA, "SHORT")}
- Milestone to be reached: <#if CSP_HITO_TIPO?has_content && CSP_HITO_TIPO != "[]">${sgi.getFieldValue(CSP_HITO_TIPO)}<#else>-</#if>
- Project: ${sgi.getFieldValue(CSP_PROYECTO_TITULO)}
- Call: <#if CSP_CONVOCATORIA_TITULO?has_content && CSP_CONVOCATORIA_TITULO != "[]">${sgi.getFieldValue(CSP_CONVOCATORIA_TITULO)}<#else>-</#if>

<#if CSP_HITO_OBSERVACIONES?has_content && CSP_HITO_OBSERVACIONES != "[]">
The following remarks have been indicated in the milestone:
${sgi.getFieldValue(CSP_HITO_OBSERVACIONES)}
</#if>

Yours sincerely,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEu>
<#setting locale="eu">
Jakinarazten dizugu laster beste mugarri bat lortuko dela behean aipatutako proiektuan:

- Data eta ordua: ${sgi.formatDate(CSP_HITO_FECHA, "SHORT")}, ${sgi.formatTime(CSP_HITO_FECHA, "SHORT")}
- Mugarri berria: <#if CSP_HITO_TIPO?has_content && CSP_HITO_TIPO != "[]">${sgi.getFieldValue(CSP_HITO_TIPO)}<#else>-</#if>
- Proiektua: ${sgi.getFieldValue(CSP_PROYECTO_TITULO)}
- Deialdia: <#if CSP_CONVOCATORIA_TITULO?has_content && CSP_CONVOCATORIA_TITULO != "[]">${sgi.getFieldValue(CSP_CONVOCATORIA_TITULO)}<#else>-</#if>

<#if CSP_HITO_OBSERVACIONES?has_content && CSP_HITO_OBSERVACIONES != "[]">
Mugarrian ohar hauek adierazi dira:
${sgi.getFieldValue(CSP_HITO_OBSERVACIONES)}
</#if>

Jaso agur bero bat,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>

<#macro renderCa>
<#setting locale="ca">
Benvolgut/da investigador/a,

Us informem que properament s'assolirà una nova fita en el projecte a baix referenciat:

- Data i hora: ${sgi.formatDate(CSP_HITO_FECHA, "SHORT")}, ${sgi.formatTime(CSP_HITO_FECHA, "SHORT")}
- Fita que s'assolirà: <#if CSP_HITO_TIPO?has_content && CSP_HITO_TIPO != "[]">${sgi.getFieldValue(CSP_HITO_TIPO)}<#else>-</#if>
- Projecte: ${sgi.getFieldValue(CSP_PROYECTO_TITULO)}
- Convocatòria: <#if CSP_CONVOCATORIA_TITULO?has_content && CSP_CONVOCATORIA_TITULO != "[]">${sgi.getFieldValue(CSP_CONVOCATORIA_TITULO)}<#else>-</#if>

<#if CSP_HITO_OBSERVACIONES?has_content && CSP_HITO_OBSERVACIONES != "[]">
A la fita s'han indicat les següents observacions:
${sgi.getFieldValue(CSP_HITO_OBSERVACIONES)}
</#if>

Rebeu una cordial salutació,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>

<#list languagePriorities as renderLang>
<@.vars["render${renderLang?capitalize}"] />
<#if renderLang?has_next>

-------------------------------------------------------------------------------

</#if>
</#list>
