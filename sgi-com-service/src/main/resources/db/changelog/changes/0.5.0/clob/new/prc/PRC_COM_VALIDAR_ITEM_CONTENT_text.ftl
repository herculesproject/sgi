<#assign data = PRC_COM_VALIDAR_ITEM_DATA?eval />
<#macro renderEs>
<#setting locale="es">
Estimado/a investigador/a,

Le informamos de que dispone de un nuevo ítem que precisa de su validación para poder realizar su baremación:

- Tipo de ítem: ${data.nombreEpigrafe}
- Título/nombre: ${data.tituloItem}
- Fecha: ${data.fechaItem?datetime.iso?string("dd/MM/yyyy")}

Reciba un cordial saludo,
Dirección de gestión de la investigación
convocatorias.dgi@ehu.eus
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear researcher,

Please be advised that you have a new item that needs to be validated in order to carry out your ranking:

- Type of item: ${data.nombreEpigrafe}
- Title/name: ${data.tituloItem}
- Date: ${data.fechaItem?datetime.iso?string("dd/MM/yyyy")}

Yours sincerely,
Research Management Directorate
convocatorias.dgi@ehu.eus
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:

Honen bidez jakinarazten dizugu item berri bat dagoela eta balioztatu egin behar duzula, baremazioa egin ahal izateko:

- Item mota: ${data.nombreEpigrafe}
- Izenburua/izena: ${data.tituloItem}
- Data: ${data.fechaItem?datetime.iso?string("dd/MM/yyyy")}

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