<#macro renderEs>
<#setting locale="es">
Estimado/a investigador/a,
Le informamdos de que se ha alcanzado el siguiente hito de la convocatoria referenciada:

- Hito: ${sgi.getFieldValue(CSP_HITO_TIPO)}
- Convocatoria: ${sgi.getFieldValue(CSP_CONVOCATORIA_TITULO)}
- Fecha y hora: ${sgi.formatDate(CSP_HITO_FECHA, "SHORT")}, ${sgi.formatTime(CSP_HITO_FECHA, "SHORT)}

<#if CSP_HITO_OBSERVACIONES?has_content && && CSP_HITO_OBSERVACIONES != "[]">
En el hito se han indicado las siguientes observaciones:
${sgi.getFieldValue(CSP_HITO_OBSERVACIONES)}
</#if>

Reciba un cordial saludo,
Dirección de gestión de la investigación
convocatorias.dgi@ehu.eus
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear Researcher,
Please be advised that the following milestone of the call in question has been reached:

- Milestone: ${sgi.getFieldValue(CSP_HITO_TIPO)}
- Call: ${sgi.getFieldValue(CSP_CONVOCATORIA_TITULO)}
- Date and time: ${sgi.formatDate(CSP_HITO_FECHA, "SHORT")}, ${sgi.formatTime(CSP_HITO_FECHA, "SHORT)}

<#if CSP_HITO_OBSERVACIONES?has_content && && CSP_HITO_OBSERVACIONES != "[]">
The following remarks have been indicated at the milestone:
${CSP_HITO_OBSERVACIONES}
</#if>

Yours sincerely,
Research Management Directorate
convocatorias.dgi@ehu.eus
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:
Honen bidez jakinarazten dizugu behean aipatutako deialdia hurrengo mugarrira iritsi dela:

- Mugarria: ${sgi.getFieldValue(CSP_HITO_TIPO)}
- Deialdia: ${sgi.getFieldValue(CSP_CONVOCATORIA_TITULO)}
- Data eta ordua: ${sgi.formatDate(CSP_HITO_FECHA, "SHORT")}, ${sgi.formatTime(CSP_HITO_FECHA, "SHORT)}

<#if CSP_HITO_OBSERVACIONES?has_content && && CSP_HITO_OBSERVACIONES != "[]">
Mugarrian ohar hauek adierazi dira:
${CSP_HITO_OBSERVACIONES}
</#if>

Jaso agur bero bat.
Ikerketa Kudeatzeko Zuzendaritza
convocatorias.dgi@ehu.eus
</#macro>
<#list languagePriorities as renderLang>
<@.vars["render${renderLang?capitalize}"] />
<#if renderLang?has_next>

-------------------------------------------------------------------------------

</#if>
</#list>