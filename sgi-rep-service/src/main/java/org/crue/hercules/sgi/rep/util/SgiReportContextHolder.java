package org.crue.hercules.sgi.rep.util;

import java.util.TimeZone;

import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.framework.spring.context.i18n.SgiLocaleContextHolder;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class SgiReportContextHolder {

  private static final String REPORT_CONTEXT_KEY = "SgiReportContext";

  private SgiReportContextHolder() {
  }

  private static void resetReportContext() {
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    if (requestAttributes != null) {
      requestAttributes.removeAttribute(REPORT_CONTEXT_KEY, RequestAttributes.SCOPE_REQUEST);
    }
  }

  private static void setReportContext(@Nullable ReportContext reportContext) {
    if (reportContext == null) {
      resetReportContext();
    } else {
      RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
      if (requestAttributes != null) {
        requestAttributes.setAttribute(REPORT_CONTEXT_KEY, reportContext, RequestAttributes.SCOPE_REQUEST);
      }
    }
  }

  @Nullable
  private static ReportContext getReportContext() {
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    if (requestAttributes != null) {
      return (ReportContext) requestAttributes.getAttribute(REPORT_CONTEXT_KEY, RequestAttributes.SCOPE_REQUEST);
    }

    return null;
  }

  public static void setLanguage(@Nullable Language language) {
    ReportContext reportContext;

    if (language != null) {
      reportContext = new DefaultReportContext(language);
    } else {
      reportContext = null;
    }
    setReportContext(reportContext);
  }

  public static Language getLanguage() {
    return getLanguage(getReportContext());
  }

  private static Language getLanguage(@Nullable ReportContext reportContext) {
    if (reportContext != null) {
      Language language = reportContext.getLanguage();
      if (language != null) {
        return language;
      }
    }
    return SgiLocaleContextHolder.getLanguage();
  }

  public static TimeZone getTimeZone() {
    return SgiConfigProperties.get().getTimeZone();
  }

}
