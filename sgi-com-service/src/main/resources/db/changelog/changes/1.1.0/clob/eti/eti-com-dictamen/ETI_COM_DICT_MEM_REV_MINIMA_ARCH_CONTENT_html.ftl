<#ftl output_format="HTML">
<#assign data = ETI_COM_DICT_MEM_REV_MINIMA_ARCH_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
<p>Estimado/a investigador/a,</p>
<p>Ante la ausencia de respuesta a las correcciones solicitadas por el comité abajo indicado, le informamos de que la situación de su solicitud pasará a archivada. En caso de querer retomar la solicitud, deberá comenzar una nueva.</p>
<p>
- Comité: ${data.comiteCodigo}<br>
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
<p>Dear researcher,</p>
<p>In the absence of a response to the corrections requested by the below committee, please be advised that your application will be filed. Should you wish to resume the application, you will need to start a new one.</p>
<p>
- Committee: ${data.comiteCodigo}<br>
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
<p>Ikertzaile agurgarria:</p>
<p>Behean aipatutako batzordeak eskatutako zuzenketek erantzunik jaso ez dutenez, zure eskaera artxibatu egingo dela jakinarazten dizugu. Berriro heldu nahi badiozu eskaerari, beste eskaera bat egin beharko duzu.</p>
<p>
- Batzordea: ${data.comiteCodigo}<br>
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

<#macro renderCa>
<#setting locale="ca">
<p>Benvolgut/da investigador/a,</p>
<p>Davant l'absència de resposta a les correccions sol·licitades pel comitè indicat a continuació, us informem que la situació de la vostra sol·licitud passarà a arxivada. En cas de voler reprendre la sol·licitud, n'haureu de començar una de nova.</p>
<p>
- Comitè: ${data.comiteCodigo}<br>
- Tipus d'activitat: ${sgi.getFieldValue(data.tipoActividad)}<br>
- Títol: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}<br>
- Referència CEID: ${data.referenciaMemoria}
</p>
<p>
Rebi una salutació cordial,<br>
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