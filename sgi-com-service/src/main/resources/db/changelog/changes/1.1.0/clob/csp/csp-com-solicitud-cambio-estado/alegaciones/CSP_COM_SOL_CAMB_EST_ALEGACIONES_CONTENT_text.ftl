<#assign data = CSP_COM_SOL_CAMB_EST_ALEGACIONES_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
Le informamos de que se ha registrado una alegación a la resolución referida a continuación:
- Convocatoria: ${sgi.getFieldValue(data.tituloConvocatoria)} <#if data.fechaPublicacionConvocatoria??>(${sgi.formatDate(data.fechaPublicacionConvocatoria, "SHORT")})</#if>
- Solicitud de referencia: ${data.codigoInternoSolicitud}
- Fecha provisional: <#if data.fechaProvisionalConvocatoria??>${sgi.formatDate(data.fechaProvisionalConvocatoria, "SHORT")}<#else>-</#if>
- Persona que ha registrado la alegación: ${data.nombreApellidosSolicitante}
</#macro>
<#macro renderEn>
<#setting locale="en">
Please be advised that an allegation regarding the following decision has been registered:
- Call: ${sgi.getFieldValue(data.tituloConvocatoria)} <#if data.fechaPublicacionConvocatoria??>(${sgi.formatDate(data.fechaPublicacionConvocatoria, "SHORT")})</#if>
- Reference request: ${data.codigoInternoSolicitud}
- Provisional date: <#if data.fechaProvisionalConvocatoria??>${sgi.formatDate(data.fechaProvisionalConvocatoria, "SHORT")}<#else>-</#if>
- Person who registered the allegation: ${data.nombreApellidosSolicitante}
</#macro>
<#macro renderEu>
<#setting locale="eu">
Honen bidez jakinarazten dizugu jarraian adierazitako ebazpenaren aurkako alegazio bat erregistratu dela: 
- Deialdia: ${sgi.getFieldValue(data.tituloConvocatoria)} <#if data.fechaPublicacionConvocatoria??>(${sgi.formatDate(data.fechaPublicacionConvocatoria, "SHORT")})</#if>
- Erreferentziazko eskaera: ${data.codigoInternoSolicitud}
- Behin-behineko data: <#if data.fechaProvisionalConvocatoria??>${sgi.formatDate(data.fechaProvisionalConvocatoria, "SHORT")}<#else>-</#if>
- Alegazioa nork erregistratu duen: ${data.nombreApellidosSolicitante}
</#macro>

<#macro renderCa>
<#setting locale="ca">
Benvolgut/da investigador/a,

Us informem que s'ha registrat una al·legació a la resolució referida a continuació:
- Convocatòria: ${sgi.getFieldValue(data.tituloConvocatoria)} <#if data.fechaPublicacionConvocatoria??>(${sgi.formatDate(data.fechaPublicacionConvocatoria, "SHORT")})</#if>
- Sol·licitud de referència: ${data.codigoInternoSolicitud}
- Data provisional: <#if data.fechaProvisionalConvocatoria??>${sgi.formatDate(data.fechaProvisionalConvocatoria, "SHORT")}<#else>-</#if>
- Persona que ha registrat l'al·legació: ${data.nombreApellidosSolicitante}

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