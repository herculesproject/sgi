<#assign data = CSP_COM_SOL_CAMB_EST_ALEGACIONES_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
<p>Estimado/a investigador/a,</p>
<p>Le informamos de que se ha registrado una alegación a la resolución referida a continuación:</p>
<p>
- Convocatoria: ${sgi.getFieldValue(data.tituloConvocatoria)} <#if data.fechaPublicacionConvocatoria??>(${sgi.formatDate(data.fechaPublicacionConvocatoria, "SHORT")})</#if><br>
- Solicitud de referencia: ${data.codigoInternoSolicitud}<br>
- Fecha provisional: <#if data.fechaProvisionalConvocatoria??>${sgi.formatDate(data.fechaProvisionalConvocatoria, "SHORT")}<#else>-</#if><br>
- Persona que ha registrado la alegación: ${data.nombreApellidosSolicitante}
</p>
<p>
Reciba un cordial saludo,<br>
Dirección de gestión de la investigación<br>
convocatorias.dgi@ehu.eus
</p>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>Dear Researcher,</p>
<p>Please be advised that an allegation regarding the following decision has been registered:</p>
<p>
- Call: ${sgi.getFieldValue(data.tituloConvocatoria)} <#if data.fechaPublicacionConvocatoria??>(${sgi.formatDate(data.fechaPublicacionConvocatoria, "SHORT")})</#if><br>
- Reference request: ${data.codigoInternoSolicitud}<br>
- Provisional date: <#if data.fechaProvisionalConvocatoria??>${sgi.formatDate(data.fechaProvisionalConvocatoria, "SHORT")}<#else>-</#if><br>
- Person who registered the allegation: ${data.nombreApellidosSolicitante}
</p>
<p>
Yours sincerely,<br>
Research Management Directorate<br>
convocatorias.dgi@ehu.eus
</p>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>Ikertzaile agurgarria:</p>
<p>Honen bidez jakinarazten dizugu jarraian adierazitako ebazpenaren aurkako alegazio bat erregistratu dela:</p>
<p>
- Deialdia: ${sgi.getFieldValue(data.tituloConvocatoria)} <#if data.fechaPublicacionConvocatoria??>(${sgi.formatDate(data.fechaPublicacionConvocatoria, "SHORT")})</#if><br>
- Erreferentziazko eskaera: ${data.codigoInternoSolicitud}<br>
- Behin-behineko data: <#if data.fechaProvisionalConvocatoria??>${sgi.formatDate(data.fechaProvisionalConvocatoria, "SHORT")}<#else>-</#if><br>
- Alegazioa nork erregistratu duen: ${data.nombreApellidosSolicitante}
</p>
<p>
Jaso agur bero bat.<br>
Ikerketa Kudeatzeko Zuzendaritza<br>
convocatorias.dgi@ehu.eus
</p>
</#macro>
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
<#list languagePriorities as renderLang>
<@.vars["render${renderLang?capitalize}"] />
<#if renderLang?has_next>
<hr>
</#if>
</#list>
</body>
</html>