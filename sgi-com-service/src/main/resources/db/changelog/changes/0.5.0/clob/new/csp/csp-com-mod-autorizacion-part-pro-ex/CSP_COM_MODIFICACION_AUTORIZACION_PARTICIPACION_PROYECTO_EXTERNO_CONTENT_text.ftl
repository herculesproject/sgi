<#assign data = CSP_COM_MODIFICACION_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
Estimado/a investigador/a,

Le informamos de que ha sido registrada la solicitud de autorización de participación en el proyecto externo abajo indicado:

- Fecha de la solicitud: ${sgi.formatDate(data.fecha, "SHORT")}
- Proyecto externo: ${sgi.getFieldValue(data.tituloProyecto)}
- Persona solicitante: ${data.nombreSolicitante}

Es necesario que valide la solicitud desde la aplicación, accediendo mediante el siguiente enlace: ${data.enlaceAplicacion}.

Reciba un cordial saludo,
Dirección de gestión de la investigación
convocatorias.dgi@ehu.eus
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear Researcher,

Please be advised that your request for authorisation to participate in the below external project has been registered:

- Date of application: ${sgi.formatDate(data.fecha, "SHORT")}
- External project: ${sgi.getFieldValue(data.tituloProyecto)}
- Applicant: ${data.nombreSolicitante}

You need to validate the application from the application, by following this link: ${data.enlaceAplicacion}.

Yours sincerely,
Research Management Directorate
convocatorias.dgi@ehu.eus
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:

Honen bidez jakinarazten dizugu behean adierazitako kanpo proiektuan parte hartzeko baimen eskaera erregistratu dela: 

- Eskaera data: ${sgi.formatDate(data.fecha, "SHORT")}
- Kanpo proiektua: ${sgi.getFieldValue(data.tituloProyecto)}
- Eskatzailea: ${data.nombreSolicitante}

Eskaera balioztatu behar duzu aplikaziotik, esteka honen bidez: ${data.enlaceAplicacion}.

Jaso agur bero bat.
Ikerketa Kudeatzeko Zuzendaritza
convocatorias.dgi@ehu.eus
</#macro>
<#list languagePriorities as renderLang>
<@.vars["render${renderLang?capitalize}"] />
<#if renderLang?has_next>

-------------------------------------------------------------------------------

</#if>
</#list>