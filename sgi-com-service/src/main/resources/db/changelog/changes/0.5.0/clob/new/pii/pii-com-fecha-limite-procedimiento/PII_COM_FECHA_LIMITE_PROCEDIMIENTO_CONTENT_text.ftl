<#assign data = PII_COM_FECHA_LIMITE_PROCEDIMIENTO_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
Estimado/a investigador/a

Le informamos de que proximamente termina el plazo para la realización por su parte de las siguientes acciones:

- Trámite a solicitar: ${sgi.getFieldValue(data.tipoProcedimiento)}
- Acciones a tomar: ${sgi.getFieldValue(data.accionesATomar)}
- Fecha de finalización del plazo:  ${sgi.formatDate(data.fechaLimite, "SHORT")}

Reciba un cordial saludo,
Servicio de Gestión de Patentes
iproperty.otri@ehu.eus
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear Researcher

Please be advised that the period in which you can carry out the following actions is about to expire:

- Procedure to be requested: ${sgi.getFieldValue(data.tipoProcedimiento)}
- Actions to be taken: ${sgi.getFieldValue(data.accionesATomar)}
- Date of expiry of the period: ${sgi.formatDate(data.fechaLimite, "SHORT")}

Yours sincerely,
Patent Management Service
iproperty.otri@ehu.eus
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:

Honen bidez jakinarazten dizugu laster amaituko dela ekintza hauek egiteko duzun epea:

- Eskatu beharreko izapidea: ${sgi.getFieldValue(data.tipoProcedimiento)}
- Egin beharreko ekintzak: ${sgi.getFieldValue(data.accionesATomar)}
- Epearen amaiera data: ${sgi.formatDate(data.fechaLimite, "SHORT")}

Jaso agur bero bat.
Patenteak Kudeatzeko Zerbitzua
iproperty.otri@ehu.eus
</#macro>
<#list languagePriorities as renderLang>
<@.vars["render${renderLang?capitalize}"] />
<#if renderLang?has_next>

-------------------------------------------------------------------------------

</#if>
</#list>