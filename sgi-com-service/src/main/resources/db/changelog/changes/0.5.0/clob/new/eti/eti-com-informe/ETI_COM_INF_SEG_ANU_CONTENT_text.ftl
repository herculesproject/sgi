<#assign data = ETI_COM_INF_SEG_ANU_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
Estimado/a investigador/a,

Le recordamos que una vez que ha pasado un año desde la fecha de obtención del informe favorable de la actividad abajo mencionada, será necesario que realice el informe de seguimiento anual correspondiente, a través de la aplicación ${data.enlaceAplicacion}.

- Tipo de Actividad: ${sgi.getFieldValue(data.tipoActividad)}
- Título: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}
- Referencia CEID: ${data.referenciaMemoria}

Reciba un cordial saludo,
Servicio de Ética
ceid@ehu.eus
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear researcher,

We remind you that, once a year has passed since the date you were issued a favourable report of the below activity, the relevant annual follow-up report needs to be carried out, using the application ${data.enlaceAplicacion}.

- Activity type: ${sgi.getFieldValue(data.tipoActividad)}
- Title: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}
- CEID Reference: ${data.referenciaMemoria}

Yours sincerely,
Ethics Service
ceid@ehu.eus
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:

Gogoratu behean aipatutako jarduerak aldeko txostena jaso zuenetik urtebete igarotakoan dagokion urteko jarraipen txostena egin beharko duzula, aplikazio honen bidez: ${data.enlaceAplicacion}.

- Jarduera mota: ${sgi.getFieldValue(data.tipoActividad)}
- Izenburua: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}
- IIEB erreferentzia: ${data.referenciaMemoria}

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