<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_DATA:
  { 
    "tituloProyecto": [{"lang":"es", "value":"Proyecto 1"}],
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "entidadesFinanciadoras": ["nombre entidad 1, nombre entidad 2"]
    "tipoFacturacion": "Sin Requisitos",
    "apellidosDestinatario": "Matias Palas",
    "enlaceAplicacion": "http://sgi.treelogic.com"
  } 
-->
<#macro renderEs>
<#setting locale="es">
Estimado/a investigador/a,

Siguiendo el calendario de facturación del contrato referenciado más abajo, está prevista la emisión de la factura que se indica. Para poder realizar la emisión de dicha factura, es necesario que nos indique si ha realizado las entregas previstas.

- Empresa/s: ${data.entidadesFinanciadoras?join(", ")}
- Título del contrato: ${sgi.getFieldValue(data.tituloProyecto)}
- Código/s de proyecto/s asociado/s: ${data.codigosSge?join(", ")}
- N.º de previsión: ${data.numPrevision}
- Tipo facturación: ${sgi.getFieldValue(data.tipoFacturacion)}

Es necesario que valide la factura desde la aplicación, accediendo mediante el siguiente enlace: ${data.enlaceAplicacion}

Reciba un cordial saludo,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear Researcher,

In accordance with the below contract invoicing schedule, the following invoice is expected to be issued. In order to be able to issue such invoice, it is necessary to indicate whether you have made the expected deliveries.

- Company(ies): ${data.entidadesFinanciadoras?join(", ")}
- Title of the contract:  ${sgi.getFieldValue(data.tituloProyecto)}
- Associated project code(s): ${data.codigosSge?join(", ")}
- Forecast no.: ${data.numPrevision}
- Invoicing type: ${sgi.getFieldValue(data.tipoFacturacion)}

You need to confirm whether the relevant invoice can be issued, by following this link: ${data.enlaceAplicacion}

Yours sincerely,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:

Behean aipatutako kontratuaren fakturazio egutegiari jarraituz, adierazitako faktura jaulkitzea aurreikusita dago. Faktura jaulki ahal izateko, aurreikusita zeuden entregak egin dituzun esan behar diguzu.

- Enpresak: ${data.entidadesFinanciadoras?join(", ")}
- Kontratuaren izenburua: ${sgi.getFieldValue(data.tituloProyecto)}
- Lotutako proiektuen kodeak: ${data.codigosSge?join(", ")}
- Aurreikuspen zk.: ${data.numPrevision}
- Fakturazio mota: ${sgi.getFieldValue(data.tipoFacturacion)}

Adierazita dagoen faktura jaulki daitekeela baieztatu behar duzu, esteka honen bidez: ${data.enlaceAplicacion}

Jaso agur bero bat.
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>

<#macro renderCa>
<#setting locale="ca">
Benvolgut/da investigador/a,

Seguint el calendari de facturació del contracte referenciat més avall, està prevista l'emissió de la factura. Per poder fer l'emissió d'aquesta factura, cal que ens indiqueu si heu fet els lliuraments previstos.

- Empresa/es: ${data.entidadesFinanciadoras?join(", ")}
- Títol del contracte:${sgi.getFieldValue(data.tituloProyecto)}
- Codi/s de projecte/s associat/s: ${data.codigosSge?join(", ")}
- Núm.: ${data.numPrevision}
- Tipus facturació: ${sgi.getFieldValue(data.tipoFacturacion)}

Cal que confirmis si es pot emetre la factura corresponent, seguint aquest enllaç: ${data.enlaceAplicacion}

Tot esperant la vostra resposta, rebeu una cordial salutació,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado  
Email unidad responsable
</#macro>

<#list languagePriorities as renderLang>
<@.vars["render${renderLang?capitalize}"] />
<#if renderLang?has_next>

-------------------------------------------------------------------------------

</#if>
</#list>