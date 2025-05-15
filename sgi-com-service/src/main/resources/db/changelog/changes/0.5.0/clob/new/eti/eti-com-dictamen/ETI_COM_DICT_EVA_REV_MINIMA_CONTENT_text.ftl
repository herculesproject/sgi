<#assign data = ETI_COM_DICT_EVA_REV_MINIMA_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
Estimado/a investigador,

Una vez evaluada su solicitud, le informamos que ya puede descargar el informe correspondiente en la aplicación: ${data.enlaceAplicacion}.
- Tipo de actividad: ${sgi.getFieldValue(data.tipoActividad)}
- Título: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}
- Referencia CEID: ${data.referenciaMemoria}
- Comité: ${data.comiteCodigo}

Reciba un cordial saludo,
Servicio de Ética
ceid@ehu.eus
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear researcher,

Having evaluated your application, please be advised that you can now download the relevant report in the application: ${data.enlaceAplicacion}.
- Activity type: ${sgi.getFieldValue(data.tipoActividad)}
- Title:  ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}
- CEID Reference: ${data.referenciaMemoria}
- Committee: ${data.comiteCodigo}

Yours sincerely,
Ethics Service
ceid@ehu.eus
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:

Honen bidez jakinarazten dizugu zure eskaera ebaluatu dela eta dagokion txostena aplikazio honetan deskarga dezakezula: ${data.enlaceAplicacion}.
- Jarduera mota: ${sgi.getFieldValue(data.tipoActividad)}
- Izenburua: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}
- IIEB erreferentzia: ${data.referenciaMemoria}
- Batzordea: ${data.comiteCodigo}

Jaso agur bero bat.
Etika Zerbitzua
ceid@ehu.eus
</#macro>
<#list languagePriorities as renderLang>
<@.vars["render${renderLang?capitalize}"] />
<#if renderLang?has_next>

-------------------------------------------------------------------------------

</#if>
</#list>