<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_NO_REQUISITO_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_NO_REQUISITO_DATA:
  { 
    "tituloProyecto": [{"lang":"es", "value":"Proyecto 1"}],
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "entidadesFinanciadoras": ["nombre entidad 1, nombre entidad 2"]
    "tipoFacturacion": "Sin Requisitos",
    "apellidosDestinatario": "Macias Pajas"
  } 
-->
<#macro renderEs>
<#setting locale="es">
Estimado/a investigador/a,

Una vez firmado el contrato abajo referenciado, se debe emitir la corespondiente factura. Para ello necesitamos que nos confirme que los trabajos han finalizado.

- Contrato con la/s empresa/s: ${data.entidadesFinanciadoras?join(", ")}
- Titulo: ${sgi.getFieldValue(data.tituloProyecto)}
- Inverstigador/a responsable: ${data.nombreApellidosValidador}
- Código/s de proyecto/s asociado/s: ${data.codigosSge?join(", ")}

En relación a los trabajos que ha realizado en el marco de este contrato, envíenos por favor un correo electrónico o informe de conclusión sobre los servicios que ha prestado a la/s empresa para conocer su opinión, grado de ejecución y cumplimiento de los trabajos finalizados.

En espera de su respuesta, reciba un cordial saludo,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear Researcher,

Once the contract below has been signed, the relevant invoice is to be issued. For this we need you to confirm that the work has been completed.

- Contract with the company(ies): ${data.entidadesFinanciadoras?join(", ")}
- Title: ${sgi.getFieldValue(data.tituloProyecto)}
- Responsible researcher: ${data.nombreApellidosValidador}
- Associated project code(s): ${data.codigosSge?join(", ")}

In relation to the work you have performed under this contract, please send us an e-mail or completion report on the services you have provided to the company(ies) in order to get your opinion, degree of implementation and completion of the work completed.

We look forward to hearing from you. Kind regards,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:

Behean aipatutako kontratua sinatu ostean, dagokion faktura jaulki behar da. Horretarako, lanak amaituta daudela baieztatu behar diguzu. 

- Kontratua sinatu duten enpresak: ${data.entidadesFinanciadoras?join(", ")}
- Izenburua: ${sgi.getFieldValue(data.tituloProyecto)}
- Ikertzaile arduraduna: ${data.nombreApellidosValidador}
- Lotutako proiektuen kodeak: ${data.codigosSge?join(", ")}

Kontratu horren baitan egin dituzun lanei dagokienez, mesedez, bidal iezaguzu enpresei emandako zerbitzuei buruzko mezu elektroniko edo amaiera txosten bat, amaitutako lanen gauzatze eta betetze maila ezagutzeko, baita haien gaineko iritzia ere.

Zure erantzunaren zain, jaso agur bero bat.
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#list languagePriorities as renderLang>
<@.vars["render${renderLang?capitalize}"] />
<#if renderLang?has_next>

-------------------------------------------------------------------------------

</#if>
</#list>