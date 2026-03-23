<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_DATA:
  { 
    "tituloProyecto": [{"lang":"es", "value":"Proyecto 1"}],
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "entidadesFinanciadoras": ["nombre entidad 1, nombre entidad 2"],
    "tipoFacturacion": "Sin Requisitos",
    "apellidosDestinatario": "Matias Palas",
    "enlaceAplicacion": "http://sgi.treelogic.com"
  } 
-->
<#macro renderEs>
<#setting locale="es">
Estimado/a investigador/a,

Una vez firmado el contrato referenciado a continuación, se debe emitir la correspondiente factura. Este proceso está condicionado a la necesaria entrega de resultados. Para ello necesitamos que nos confirme que los trabajos han finalizado.

- Empresa/s: ${data.entidadesFinanciadoras?join(", ")}
- Título del contrato: ${sgi.getFieldValue(data.tituloProyecto)}
- Código/s de proyecto/s asociado/s: ${data.codigosSge?join(", ")}
- N.º de previsión: ${data.numPrevision}
- Entrega: ${sgi.getFieldValue(data.tipoFacturacion)}

En relación a los trabajos que ha realizado en el marco de este contrato, envíenos por favor un correo electrónico o informe de conclusión sobre los servicios que ha prestado a la/s empresa para conocer su opinión, grado de ejecución y cumplimiento de los trabajos finalizados.

Es necesario que valide la factura desde la aplicación, accediendo mediante el siguiente enlace: ${data.enlaceAplicacion}

Reciba un cordial saludo,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear Researcher,

Once the contract below has been signed, the relevant invoice is to be issued. This process is conditional on the necessary delivery of results. For this we need you to confirm that the work has been completed.

- Company(ies): ${data.entidadesFinanciadoras?join(", ")}
- Title of the contract: ${sgi.getFieldValue(data.tituloProyecto)}
- Associated project code(s):  ${data.codigosSge?join(", ")}
- Forecast no.: ${data.numPrevision}
- Delivery: ${sgi.getFieldValue(data.tipoFacturacion)}

In relation to the work you have performed under this contract, please send us an e-mail or completion report on the services you have provided to the company(ies) in order to get your opinion, degree of implementation and completion of the work completed.

You need to confirm whether the relevant invoice can be issued, by following this link: ${data.enlaceAplicacion}

Yours sincerely,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:

Behean aipatutako kontratua sinatu ostean, dagokion faktura jaulki behar da. Prozesu hori gauzatzeko ezinbestekoa da emaitzak entregatzea. Horretarako, lanak amaituta daudela baieztatu behar diguzu.

- Enpresak: ${data.entidadesFinanciadoras?join(", ")}
- Kontratuaren izenburua: ${sgi.getFieldValue(data.tituloProyecto)}
- Lotutako proiektuen kodeak: ${data.codigosSge?join(", ")}
- Aurreikuspen zk.: ${data.numPrevision}
- Entrega: ${sgi.getFieldValue(data.tipoFacturacion)}

Kontratu horren baitan egin dituzun lanei dagokienez, mesedez, bidal iezaguzu enpresei emandako zerbitzuei buruzko mezu elektroniko edo amaiera txosten bat, amaitutako lanen gauzatze eta betetze maila ezagutzeko, baita haien gaineko iritzia ere.

Adierazita dagoen faktura jaulki daitekeela baieztatu behar duzu, esteka honen bidez: ${data.enlaceAplicacion}

Jaso agur bero bat.
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>

<#macro renderCa>
<#setting locale="ca">
Estimat/da investigador/a,

Un cop signat el contracte referenciat a continuació, s'ha d'emetre la corresponent factura. Aquest procés està condicionat al lliurament necessari de resultats. Per això necessitem que ens confirmi que els treballs han finalitzat.

- Empresa/es: ${data.entidadesFinanciadoras?join(", ")}
- Títol del contracte:${sgi.getFieldValue(data.tituloProyecto)}
- Codi/s de projecte/s associat/s: ${data.codigosSge?join(", ")}
- Núm.: ${data.numPrevision}
- Lliurament: ${sgi.getFieldValue(data.tipoFacturacion)}

Pel que fa als treballs que ha realitzat en el marc d'aquest contracte, envieu-nos si us plau un correu electrònic o informe de conclusió sobre els serveis que ha prestat a l'empresa per conèixer la seva opinió, grau d'execució i compliment dels treballs finalitzats.

Cal que valideu la factura des de l'aplicació, accedint mitjançant l'enllaç següent: ${data.enlaceAplicacion}

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