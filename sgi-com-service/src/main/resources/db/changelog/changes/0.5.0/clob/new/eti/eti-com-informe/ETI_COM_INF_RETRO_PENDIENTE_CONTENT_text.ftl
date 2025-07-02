<#assign data = ETI_COM_INF_RETRO_PENDIENTE_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
Recordatorio de solicitud de evaluación retrospectiva

Estimado/a investigador/a,

Le recordamos que, tal y como se refleja en la autorización del órgano autonómico  correspondiente, para la realización del proyecto mencionado más abajo, será necesario que realice la correspondiente evaluación retrospectiva, a través del formulario que puede encontrar en la web: ${data.enlaceAplicacion}.

- Tipo de actividad: ${sgi.getFieldValue(data.tipoActividad)}
- Título: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}
- Referencia: ${data.referenciaMemoria}

Reciba un cordial saludo,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEn>
<#setting locale="en">
Retrospective Evaluation Request Reminder

Dear Researcher,

We remind you that, as reflected in the authorisation of the relevant Provincial Government, in order to carry out the project mentioned below, a retrospective evaluation will be necessary. This you can do using the form that you can find on the website: ${data.enlaceAplicacion}.

- Activity type: ${sgi.getFieldValue(data.tipoActividad)}
- Title: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}
- Reference: ${data.referenciaMemoria}

Yours sincerely,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEu>
<#setting locale="eu">
Atzera begirako ebaluazioaren eskaeraren gogorarazpena

Ikertzaile agurgarria:

Gogoratu behean aipatutako proiektua gauzatzeko dagokion atzera begirako ebaluazioa egin beharko duzula, dagokion foru aldundiaren baimenean jasota dagoenez; webguneko formularioa erabil dezakezu:  ${data.enlaceAplicacion}.

- Jarduera mota: ${sgi.getFieldValue(data.tipoActividad)}
- Izenburua: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}
- Erreferentzia: ${data.referenciaMemoria}

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