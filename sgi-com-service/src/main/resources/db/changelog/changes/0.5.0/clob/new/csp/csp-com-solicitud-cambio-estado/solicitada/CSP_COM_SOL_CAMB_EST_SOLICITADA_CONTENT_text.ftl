<#assign data = CSP_COM_SOL_CAMB_EST_SOLICITADA_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
Estimado/a investigador/a,
Le informamos de que ha sido registrada correctamente la solicitud referida a continuación:
- Convocatoria: ${sgi.getFieldValue(data.tituloConvocatoria)} <#if data.fechaPublicacionConvocatoria??>(${sgi.formatDate(data.fechaPublicacionConvocatoria, "SHORT")})</#if>
- Solicitud presentada por: ${data.nombreApellidosSolicitante}
- Fecha de presentación: ${sgi.formatDate(data.fechaCambioEstadoSolicitud, "SHORT")}

Reciba un cordial saludo,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear Researcher,
Please be advised that the following application has been successfully registered:
- Call: ${sgi.getFieldValue(data.tituloConvocatoria)} <#if data.fechaPublicacionConvocatoria??>(${sgi.formatDate(data.fechaPublicacionConvocatoria, "SHORT")})</#if>
- Request submitted by: ${data.nombreApellidosSolicitante}
- Submission date: ${sgi.formatDate(data.fechaCambioEstadoSolicitud, "SHORT")}

Yours sincerely,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:
Honen bidez jakinarazten dizugu honako eskaera hau zuzen erregistratu dela:
- Deialdia: ${sgi.getFieldValue(data.tituloConvocatoria)} <#if data.fechaPublicacionConvocatoria??>(${sgi.formatDate(data.fechaPublicacionConvocatoria, "SHORT")})</#if>
- Eskaera nork aurkeztu duen: ${data.nombreApellidosSolicitante}
- Aurkezpen data: ${sgi.formatDate(data.fechaCambioEstadoSolicitud, "SHORT")}

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