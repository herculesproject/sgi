<#macro renderEs>
<#setting locale="es">
Estimado/a investigador/a,

Le informamos de que próximamente se abrirá la siguiente fase de la convocatoria abajo referenciada:

- Convocatoria: ${sgi.getFieldValue(CSP_CONV_FASE_TITULO)}
- Fase: ${sgi.getFieldValue(CSP_CONV_TIPO_FASE)}
- Apertura: ${sgi.formatDate(CSP_CONV_FASE_FECHA_INICIO, "SHORT")} a las ${sgi.formatTime(CSP_CONV_FASE_FECHA_INICIO, "SHORT")}
- Cierre: ${sgi.formatDate(CSP_CONV_FASE_FECHA_FIN, "SHORT")} a las ${sgi.formatTime(CSP_CONV_FASE_FECHA_FIN, "SHORT")}

En dicha fase se han indicado las siguientes observaciones:
${sgi.getFieldValue(CSP_CONV_FASE_OBSERVACIONES)}

Reciba un cordial saludo,
Dirección de gestión de la investigación
convocatorias.dgi@ehu.eus
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear Researcher,

Please be advised that the next stage of the below call will be launched shortly:

- Call: ${sgi.getFieldValue(CSP_CONV_FASE_TITULO)}
- Phase: ${sgi.getFieldValue(CSP_CONV_TIPO_FASE)}
- Opening: ${sgi.formatDate(CSP_CONV_FASE_FECHA_INICIO, "SHORT")} at ${sgi.formatTime(CSP_CONV_FASE_FECHA_INICIO, "SHORT")}
- Closing: ${sgi.formatDate(CSP_CONV_FASE_FECHA_FIN, "SHORT")} at ${sgi.formatTime(CSP_CONV_FASE_FECHA_FIN, "SHORT")}

The following remarks were made in this phase:
${sgi.getFieldValue(CSP_CONV_FASE_OBSERVACIONES)}

Yours sincerely,
Research Management Directorate
convocatorias.dgi@ehu.eus
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:

Honen bidez jakinarazten dizugu laster hasiko dela behean aipatutako deialdiaren hurrengo fasea:

- Deialdia: ${sgi.getFieldValue(CSP_CONV_FASE_TITULO)}
- Fasea: ${sgi.getFieldValue(CSP_CONV_TIPO_FASE)}
- Hasiera: ${sgi.formatDate(CSP_CONV_FASE_FECHA_INICIO, "SHORT")}, ${sgi.formatTime(CSP_CONV_FASE_FECHA_INICIO, "SHORT")}
- Amaiera: ${sgi.formatDate(CSP_CONV_FASE_FECHA_FIN, "SHORT")}, ${sgi.formatTime(CSP_CONV_FASE_FECHA_FIN, "SHORT")}

Fase horretan ohar hauek adierazi dira:
${sgi.getFieldValue(CSP_CONV_FASE_OBSERVACIONES)}

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