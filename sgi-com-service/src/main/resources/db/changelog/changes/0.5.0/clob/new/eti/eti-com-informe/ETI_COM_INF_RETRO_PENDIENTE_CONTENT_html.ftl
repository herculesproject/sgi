<#ftl output_format="HTML">
<#assign data = ETI_COM_INF_RETRO_PENDIENTE_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
<p>Recordatorio de solicitud de evaluación retrospectiva</p>
<p>Estimado/a investigador/a,</p>
<p>Le recordamos que, tal y como se refleja en la autorización del órgano autonómico  correspondiente, para la realización del proyecto mencionado más abajo, será necesario que realice la correspondiente evaluación retrospectiva, a través del formulario que puede encontrar en la web: <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a>.</p>
<p>
- Tipo de actividad: ${sgi.getFieldValue(data.tipoActividad)}<br>
- Título: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}<br>
- Referencia: ${data.referenciaMemoria}
</p>
<p>
Reciba un cordial saludo,<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
</p>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>Retrospective Evaluation Request Reminder</p>
<p>Dear Researcher,</p>
<p>We remind you that, as reflected in the authorisation of the relevant Provincial Government, in order to carry out the project mentioned below, a retrospective evaluation will be necessary. This you can do using the form that you can find on the website: <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a>.</p>
<p>
- Activity type: ${sgi.getFieldValue(data.tipoActividad)}<br>
- Title: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}<br>
- Reference: ${data.referenciaMemoria}
</p>
<p>
Yours sincerely,<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
</p>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>Atzera begirako ebaluazioaren eskaeraren gogorarazpena</p>
<p>Ikertzaile agurgarria:</p>
<p>Gogoratu behean aipatutako proiektua gauzatzeko dagokion atzera begirako ebaluazioa egin beharko duzula, dagokion foru aldundiaren baimenean jasota dagoenez; webguneko formularioa erabil dezakezu:  <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a>.</p>
<p>
- Jarduera mota: ${sgi.getFieldValue(data.tipoActividad)}<br>
- Izenburua: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}<br>
- Erreferentzia: ${data.referenciaMemoria}
</p>
<p>
Jaso agur bero bat.<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
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