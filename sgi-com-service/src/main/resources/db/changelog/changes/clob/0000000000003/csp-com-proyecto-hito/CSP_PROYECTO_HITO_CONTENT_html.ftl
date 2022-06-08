<#ftl output_format="HTML" auto_esc=false>
<#assign date = CSP_HITO_FECHA?datetime.iso>
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Con fecha <b>${date?string("dd/MM/yyyy")}</b> a las <b>${date?string("HH:mm")}</b> se alcanzar&aacute; el hito <b>${CSP_HITO_TIPO?esc}</b> del proyecto <b>${CSP_PROYECTO_TITULO?esc}</b> <#if CSP_CONVOCATORIA_TITULO?has_content>de la convocatoria <b>${CSP_CONVOCATORIA_TITULO?esc}</b></#if>.</p>
  </body>
</html>