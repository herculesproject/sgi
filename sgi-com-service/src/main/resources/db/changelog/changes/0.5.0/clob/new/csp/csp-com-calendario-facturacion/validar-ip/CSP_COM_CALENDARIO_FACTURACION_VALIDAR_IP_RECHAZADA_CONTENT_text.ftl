<#assign data = CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_RECHAZADA_DATA?eval_json />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_RECHAZADA_DATA:
  { 
    "nombreApellidosValidador": "Manolo Gutierrez Fernandez",
    "tituloProyecto": [{"lang":"es", "value":"Proyecto 1"}],
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "motivoRechazo": [{"lang":"es", "value":"Motivo rechazo"}]
  } 
-->
<#macro renderEs>
<#setting locale="es">
Estimado/a investigador/a,

Le informamos de que tiene el visto bueno para la emisión de la factura abajo referenciada.

- Investigador/a responsable: ${data.nombreApellidosValidador}
- Título del contrato: ${sgi.getFieldValue(data.tituloProyecto)}
- Asociado al/los proyectos con código/s: ${data.codigosSge?join(", ")}
- Nº de previsión: ${data.numPrevision}
- Motivo: ${sgi.getFieldValue(data.motivoRechazo)}

Reciba un cordial saludo,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable

</#macro>
<#macro renderEn>
<#setting locale="en">
Dear Researcher,

Please be advised that you have the approval to issue the invoice below.

- Responsible researcher: ${data.nombreApellidosValidador}
- Title of the contract: ${sgi.getFieldValue(data.tituloProyecto)}
- Associated to project(s) with code(s):  ${data.codigosSge?join(", ")}
- Forecast no.: ${data.numPrevision}
- Reason: ${sgi.getFieldValue(data.motivoRechazo)}

Yours sincerely,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:

Honen bidez jakinarazten dizugu ez duzula oniritzirik behean aipatutako faktura jaulkitzeko.

- Ikertzaile arduraduna: ${data.nombreApellidosValidador}
- Kontratuaren izenburua: ${sgi.getFieldValue(data.tituloProyecto)}
- Lotutako proiektuen kodeak: ${data.codigosSge?join(", ")}
- Aurreikuspen zk.: ${data.numPrevision}
- Zergatia: ${sgi.getFieldValue(data.motivoRechazo)}

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