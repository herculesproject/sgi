<#assign data = ETI_COM_DICT_EVA_SEG_REV_MINIMA_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
<p>Estimado/a investigador/a,</p>
<p>Una vez evaluadas las revisiones mínimas solicitadas abajo indicada, le informamos que ya puede descargar el informe correspondiente en la aplicación <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a>.</p>
<p>
- Tipo de actividad: ${sgi.getFieldValue(data.tipoActividad)}<br>
- Título: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}<br>
- Referencia CEID: ${data.referenciaMemoria}<br>
- Comité: ${data.comiteCodigo}
</p>
<p>
Reciba un cordial saludo,<br>
Servicio de Ética<br>
ceid@ehu.eus
</p>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>Dear Researcher,</p>
<p>Having evaluated the minimum revisions requested as set out below, please be advised that you can now download the relevant report in the application <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a>.</p>
<p>
- Activity type: ${sgi.getFieldValue(data.tipoActividad)}<br>
- Title: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}<br>
- CEID Reference: ${data.referenciaMemoria}<br>
- Committee: ${data.comiteCodigo}
</p>
<p>
Yours sincerely,<br>
Ethics Service<br>
ceid@ehu.eus
</p>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>Ikertzaile agurgarria:</p>
<p>Honen bidez jakinarazten dizugu eskatutako gutxieneko berrikuspenak ebaluatu direla eta dagokion txostena aplikazio honetan deskarga dezakezula: <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a>.</p>
<p>
- Jarduera mota: ${sgi.getFieldValue(data.tipoActividad)}<br>
- Izenburua: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}<br>
- IIEB erreferentzia: ${data.referenciaMemoria}<br>
- Batzordea: ${data.comiteCodigo}
</p>
<p>
Jaso agur bero bat.<br>
Etika Zerbitzua<br>
ceid@ehu.eus
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