<#macro renderEs>
<#setting locale="es">
Estimado/a miembro del Comité,

Mediante la presente comunicación, se le convoca a la reunión del ${ETI_COMITE}, que se celebrará: 

- Día: ${sgi.formatDate(ETI_CONVOCATORIA_REUNION_FECHA_EVALUACION, "FULL")}
- Hora: ${ETI_CONVOCATORIA_REUNION_HORA_INICIO}:${ETI_CONVOCATORIA_REUNION_MINUTO_INICIO} h (primera convocatoria); <#if ETI_CONVOCATORIA_REUNION_HORA_INICIO_SEGUNDA??>${ETI_CONVOCATORIA_REUNION_HORA_INICIO_SEGUNDA}:${ETI_CONVOCATORIA_REUNION_MINUTO_INICIO_SEGUNDA} h (segunda convocatoria)</#if>
- Tipo de convocatoria: ${sgi.getFieldValue(ETI_CONVOCATORIA_REUNION_TIPO_CONVOCATORIA)}
- Lugar: <#if ETI_CONVOCATORIA_REUNION_VIDEOCONFERENCIA?string == "true">Videoconferencia<#else>${sgi.getFieldValue(ETI_CONVOCATORIA_REUNION_LUGAR)}</#if>

El orden del día de la reunión será el siguiente:

${sgi.getFieldValue(ETI_CONVOCATORIA_REUNION_ORDEN_DEL_DIA)}

Reciba un cordial saludo,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear Committee Member,

You are hereby requested to attend the meeting of the ${ETI_COMITE}, to be held on: 

- Day: ${sgi.formatDate(ETI_CONVOCATORIA_REUNION_FECHA_EVALUACION, "FULL")}
- Time: ${ETI_CONVOCATORIA_REUNION_HORA_INICIO}:${ETI_CONVOCATORIA_REUNION_MINUTO_INICIO} h (first call); <#if ETI_CONVOCATORIA_REUNION_HORA_INICIO_SEGUNDA??>${ETI_CONVOCATORIA_REUNION_HORA_INICIO_SEGUNDA}:${ETI_CONVOCATORIA_REUNION_MINUTO_INICIO_SEGUNDA} h (second call)</#if>
- Type of call: ${sgi.getFieldValue(ETI_CONVOCATORIA_REUNION_TIPO_CONVOCATORIA)}
- Location: <#if ETI_CONVOCATORIA_REUNION_VIDEOCONFERENCIA?string == "true">Videoconference<#else>${sgi.getFieldValue(ETI_CONVOCATORIA_REUNION_LUGAR)}</#if>

The agenda of the meeting will be as follows:

${sgi.getFieldValue(ETI_CONVOCATORIA_REUNION_ORDEN_DEL_DIA)}

Yours sincerely,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEu>
<#setting locale="eu">
Batzordekide agurgarria:

Komunikazio honen bidez ${ETI_COMITE}ren bilerarako deialdia helarazten dizugu. Bilerari buruzko datuak: 

- Eguna: ${sgi.formatDate(ETI_CONVOCATORIA_REUNION_FECHA_EVALUACION, "FULL")}
- Ordua: ${ETI_CONVOCATORIA_REUNION_HORA_INICIO}:${ETI_CONVOCATORIA_REUNION_MINUTO_INICIO} h (lehenengo deialdia); <#if ETI_CONVOCATORIA_REUNION_HORA_INICIO_SEGUNDA??>${ETI_CONVOCATORIA_REUNION_HORA_INICIO_SEGUNDA}:${ETI_CONVOCATORIA_REUNION_MINUTO_INICIO_SEGUNDA} h (bigarren deialdia)</#if>
- Deialdi mota: ${sgi.getFieldValue(ETI_CONVOCATORIA_REUNION_TIPO_CONVOCATORIA)}
- Lekua: <#if ETI_CONVOCATORIA_REUNION_VIDEOCONFERENCIA?string == "true">Bideokonferentzia<#else>${sgi.getFieldValue(ETI_CONVOCATORIA_REUNION_LUGAR)}</#if>

Hau izango da bilerako gai zerrenda:

${sgi.getFieldValue(ETI_CONVOCATORIA_REUNION_ORDEN_DEL_DIA)}

Jaso agur bero bat.
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>

<#macro renderCa>
<#setting locale="ca">
Estimat/da membre del Comitè,

Us convoquem a la reunió del ${ETI_COMITE}, que se celebrarà: 

- Dia: ${sgi.formatDate(data.fechaEvaluacion, "FULL")}
- Hora: ${data.horaInicio}:${data.minutoInicio} h (primera convocatòria); <#if data.horaInicioSegunda??>${data.horaInicioSegunda}:${data.minutoInicioSegunda} h (segona convocatòria)</#if>
- Tipus de convocatòria: ${sgi.getFieldValue(data.tipoConvocatoria)}
- Lloc: <#if data.videoconferencia?string == "true">Videoconferència<#else>${sgi.getFieldValue(data.lugar)}</#if>

L'ordre del dia de la reunió serà el següent:

${sgi.getFieldValue(data.ordenDelDia)}

Rebi una cordial salutació,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>

<#list languagePriorities as renderLang>
<@.vars["render${renderLang?capitalize}"] />
<#if renderLang?has_next>

-------------------------------------------------------------------------------

</#if>
</#list>