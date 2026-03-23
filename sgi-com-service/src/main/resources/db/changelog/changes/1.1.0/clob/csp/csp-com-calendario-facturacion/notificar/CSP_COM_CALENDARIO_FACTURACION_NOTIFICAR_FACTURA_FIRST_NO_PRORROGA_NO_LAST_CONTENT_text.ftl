<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_FIRST_NO_PRORROGA_NO_LAST_DATA?eval />
<#macro renderEs>
<#setting locale="es">
Estimado/a investigador/a,

Una vez firmado el contrato asociado al/los proyecto/s abajo referenciado/s más abajo, es necesario que confirme si puede ser emitida la factura que se indica:

- Empresa/s: ${data.entidadesFinanciadoras?join(", ")}
- Título del contrato: ${sgi.getFieldValue(data.tituloProyecto)}
- Código/s de proyecto/s asociado/s: ${data.codigosSge?join(", ")}
- N.º de previsión: ${data.numPrevision}

Es necesario que valide la factura desde la aplicación, accediendo mediante el siguiente enlace: ${data.enlaceAplicacion}

En espera de su respuesta, reciba un cordial saludo,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear Researcher,

Having signed the contract associated with the below project(s), you need to confirm whether the relevant invoice can be issued:

- Company(ies): ${data.entidadesFinanciadoras?join(", ")}
- Title of the contract: ${sgi.getFieldValue(data.tituloProyecto)}
- Associated project code(s): ${data.codigosSge?join(", ")}
- Forecast no.: ${data.numPrevision}

You need to confirm whether the relevant invoice can be issued, by following this link: ${data.enlaceAplicacion}

We look forward to hearing from you. Kind regards,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:

Behean aipatutako proiektuekin lotutako kontratua sinatu ostean, adierazita dagoen faktura jaulki daitekeela baieztatu behar duzu:

- Enpresak: ${data.entidadesFinanciadoras?join(", ")}
- Kontratuaren izenburua: ${sgi.getFieldValue(data.tituloProyecto)}
- Lotutako proiektuen kodeak: ${data.codigosSge?join(", ")}
- Aurreikuspen zk.: ${data.numPrevision}

Adierazita dagoen faktura jaulki daitekeela baieztatu behar duzu, esteka honen bidez: ${data.enlaceAplicacion}

Zure erantzunaren zain, jaso agur bero bat.
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>

<#macro renderCa><#setting locale="ca">
Benvolgut/da investigador/a,

Un cop signat el contracte assocat al/els projecte/s baix referenciat/s més avall, cal que confirmi si poden ser emesa: ${data.enlaceAplicacion}

- Empresa/es: ${data.entidadesFinanciadoras?join(", ")}
- Títol del contracte: ${sgi.getFieldValue(data.tituloProyecto)}
- Codi/s de projecte/s associat/s: ${data.codigosSge?join(", ")}
- Núm.: ${data.numPrevision}

Rebi una salutació cordial,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>

<#list languagePriorities as renderLang>
<@.vars["render${renderLang?capitalize}"] />
<#if renderLang?has_next>

-------------------------------------------------------------------------------

</#if>
</#list>