<#assign data = CSP_COM_CAMBIO_ESTADO_VALIDADA_SOL_TIPO_RRHH_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
Estimado/a investigador/a,

Le informamos de que su solicitud abajo referenciada ha sido validada por el/la tutor/a.

- Fecha de la validación: ${sgi.formatDate(data.fechaEstado, "SHORT")}
- Solicitud: ${data.codigoInternoSolicitud} 
- Convocatoria: <#if data.tituloConvocatoria?has_content>${sgi.getFieldValue(data.tituloConvocatoria)}<#/else>-</#if>

Reciba un cordial saludo,
Dirección de gestión de la investigación
convocatorias.dgi@ehu.eus
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear researcher,

Please be advised that your below application has been validated by the tutor.

- Date of validation: ${sgi.formatDate(data.fechaEstado, "SHORT")}
- Application:  ${data.codigoInternoSolicitud} 
- Call:  <#if data.tituloConvocatoria?has_content>${sgi.getFieldValue(data.tituloConvocatoria)}<#/else>-</#if>

Yours sincerely,
Research Management Directorate
convocatorias.dgi@ehu.eus
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:

Honen bidez jakinarazten dizugu tutoreak baliozkotu egin duela behean aipatutako zure eskaera. 

- Baliozkotze data: ${sgi.formatDate(data.fechaEstado, "SHORT")}
- Eskaera: ${data.codigoInternoSolicitud} 
- Deialdia: <#if data.tituloConvocatoria?has_content>${sgi.getFieldValue(data.tituloConvocatoria)}<#/else>-</#if>

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