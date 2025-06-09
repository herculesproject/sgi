<#macro renderEs>
<#setting locale="es">
Estimado/a investigador/a,

Le informamos de que proximamente se alcanzará un nuevo hito en el proyecto asociado a la convocatoria abajo referenciada:

- Fecha y hora del nuevo hito: ${sgi.formatDate(CSP_HITO_FECHA, "SHORT")}, ${sgi.formatTime(CSP_HITO_FECHA, "SHORT")}
- Hito que se alcanzará: ${sgi.getFieldValue(CSP_HITO_TIPO)}
- Proyecto: ${sgi.getFieldValue(CSP_PROYECTO_TITULO)}
- Convocatoria: <#if CSP_CONVOCATORIA_TITULO?has_content && CSP_CONVOCATORIA_TITULO != "[]">${sgi.getFieldValue(CSP_CONVOCATORIA_TITULO)}<#else>-</#if>

<#if CSP_HITO_OBSERVACIONES?has_content && && CSP_HITO_OBSERVACIONES != "[]">
En el hito se han indicado las siguientes observaciones:
${sgi.getFieldValue(CSP_HITO_OBSERVACIONES)}
</#if>

Reciba un cordial saludo,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear Researcher,

Please be advised that a new milestone in the project associated to the below call will soon be reached:

- Date and time: ${sgi.formatDate(CSP_HITO_FECHA, "SHORT")}, ${sgi.formatTime(CSP_HITO_FECHA, "SHORT")}
- Milestone to be reached: ${sgi.getFieldValue(CSP_HITO_TIPO)}
- Project: ${sgi.getFieldValue(CSP_PROYECTO_TITULO)}
- Call: <#if CSP_CONVOCATORIA_TITULO?has_content && CSP_CONVOCATORIA_TITULO != "[]">${sgi.getFieldValue(CSP_CONVOCATORIA_TITULO)}<#else>-</#if>

<#if CSP_HITO_OBSERVACIONES?has_content && && CSP_HITO_OBSERVACIONES != "[]">
The following remarks have been indicated in the milestone:
${sgi.getFieldValue(CSP_HITO_OBSERVACIONES)}
</#if>

Yours sincerely,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:

Honen bidez jakinarazten dizugu laster mugarri berri batera iritsiko dela aipatuta ageri den deialdiari lotutako eskaera:

- Data eta ordua: ${sgi.formatDate(CSP_HITO_FECHA, "SHORT")}, ${sgi.formatTime(CSP_HITO_FECHA, "SHORT")}
- Mugarri berria: ${sgi.getFieldValue(CSP_HITO_TIPO)}
- Proiektua: ${sgi.getFieldValue(CSP_PROYECTO_TITULO)}
- Deialdia: <#if CSP_CONVOCATORIA_TITULO?has_content && CSP_CONVOCATORIA_TITULO != "[]">${sgi.getFieldValue(CSP_CONVOCATORIA_TITULO)}<#else>-</#if>

<#if CSP_HITO_OBSERVACIONES?has_content && && CSP_HITO_OBSERVACIONES != "[]">
Mugarrian ohar hauek adierazi dira:
${sgi.getFieldValue(CSP_HITO_OBSERVACIONES)}
</#if>

Jaso agur bero bat.
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#list languagePriorities as renderLang>
<@.vars["render${renderLang?capitalize}"] />
<#if renderLang?has_next>

-------------------------------------------------------------------------------

</#if>
</#list>