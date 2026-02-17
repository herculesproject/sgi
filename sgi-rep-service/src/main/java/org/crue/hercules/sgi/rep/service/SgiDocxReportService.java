package org.crue.hercules.sgi.rep.service;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.framework.spring.context.i18n.SgiLocaleContextHolder;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.report.data.ReportData;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.crue.hercules.sgi.rep.util.CustomSpELRenderDataCompute;
import org.crue.hercules.sgi.rep.util.SgiHtmlRenderPolicy;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrGeneral;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.data.DocxRenderData;
import com.deepoove.poi.data.Includes;
import com.deepoove.poi.data.PictureRenderData;
import com.deepoove.poi.data.PictureType;
import com.deepoove.poi.data.Pictures;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;

import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de generación de informes
 */
@Slf4j
public abstract class SgiDocxReportService {

  private final SgiApiConfService sgiApiConfService;

  protected SgiDocxReportService(SgiApiConfService sgiApiConfService) {
    this.sgiApiConfService = sgiApiConfService;

  }

  /**
   * Obtiene el report
   * 
   * @param reportPath ruta del report
   * @return MasterReport
   */
  @Deprecated
  protected InputStream getReportDefinitionStream(String reportPath) {
    try {
      byte[] reportDefinition = sgiApiConfService.getResource(reportPath);
      return new ByteArrayInputStream(reportDefinition);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException(e);
    }
  }

  /**
   * Obtiene la imágen de la cabecera del informe
   * 
   * @return PictureRenderData
   */
  protected PictureRenderData getHeaderLogo() {
    byte[] imgByte = sgiApiConfService.getResource("rep-common-header-logo");

    return Pictures.ofBytes(imgByte, PictureType.JPEG).fitSize().create();
  }

  public byte[] scale(byte[] fileData, int width, int height) throws NotFoundException {
    ByteArrayInputStream in = new ByteArrayInputStream(fileData);
    try {
      BufferedImage img = ImageIO.read(in);
      if (height == 0) {
        height = (width * img.getHeight()) / img.getWidth();
      }
      if (width == 0) {
        width = (height * img.getWidth()) / img.getHeight();
      }
      Image scaledImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
      BufferedImage imageBuff = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      imageBuff.getGraphics().drawImage(scaledImage, 0, 0, new Color(0, 0, 0), null);

      ByteArrayOutputStream buffer = new ByteArrayOutputStream();

      ImageIO.write(imageBuff, "jpg", buffer);

      return buffer.toByteArray();
    } catch (IOException e) {
      throw new NotFoundException("scale not fou", e);
    }
  }

  @Deprecated
  protected XWPFDocument compileReportData(InputStream is, Map<String, Object> dataReport) {

    Configure config = Configure.builder()
        .useSpringEL(false)
        .setRenderDataComputeFactory(
            model -> {
              try {
                return new CustomSpELRenderDataCompute(dataReport, model, false);
              } catch (SecurityException e) {
                log.error(e.getMessage(), e);
                return null;
              }
            })
        .addPlugin('<', new SgiHtmlRenderPolicy())
        .build();

    return compileReportData(is, config, dataReport);
  }

  @Deprecated
  protected XWPFDocument compileReportData(InputStream is, Configure config, Map<String, Object> dataReport) {
    return XWPFTemplate.compile(is, config).render(dataReport).getXWPFDocument();
  }

  protected XWPFDocument build(ReportData data) {
    byte[] reportDefinition = sgiApiConfService.getResource(data.getInforme().getResourceName());

    Configure config = Configure.builder()
        .useSpringEL(false)
        .setRenderDataComputeFactory(
            model -> {
              try {
                return new CustomSpELRenderDataCompute(data.getData(), model, false);
              } catch (SecurityException e) {
                log.error(e.getMessage(), e);
                return null;
              }
            })
        .addPlugin('<', new SgiHtmlRenderPolicy())
        .build();
    return XWPFTemplate.compile(
        new ByteArrayInputStream(reportDefinition), config).render(data.getData()).getXWPFDocument();
  }

  protected DocxRenderData buildSubReport(ReportData data) {
    byte[] reportDefinition = sgiApiConfService.getResource(data.getInforme().getResourceName());
    return Includes.ofStream(new ByteArrayInputStream(reportDefinition))
        .setRenderModel(data.getData()).create();
  }

  protected byte[] buildPDF(ReportData data) throws IOException {
    XWPFDocument docx = build(data);

    applyStyleLineSpacing(docx);

    ByteArrayOutputStream outputPdf = new ByteArrayOutputStream();
    PdfOptions pdfOptions = createCustomPdfOptions();

    PdfConverter.getInstance().convert(docx, outputPdf, pdfOptions);

    return outputPdf.toByteArray();
  }

  protected PdfOptions createCustomPdfOptions() {
    PdfOptions pdfOptions = PdfOptions.create();
    Language lang = SgiLocaleContextHolder.getLanguage();
    byte[] fontBytes = sgiApiConfService
        .getResource("rep-font-" + lang.getCode());
    if (ObjectUtils.isNotEmpty(fontBytes)) {
      String nameFont = "custom-font-" + lang.getCode() + ".otf";
      try {
        BaseFont baseFont = BaseFont.createFont(nameFont, BaseFont.IDENTITY_H,
            BaseFont.EMBEDDED, true, fontBytes, null);

        pdfOptions.fontProvider((familyName, encoding, size, style, color) -> new Font(baseFont, size, style, color));
      } catch (Exception e) {
        // Si falla la fuente se coge la de por defecto del sistema
        log.warn(e.getMessage(), e);
      }
    }
    return pdfOptions;
  }

  /**
   * Aplica al párrafo el interlineado definido en su estilo de Word.
   *
   * <p>
   * Para cada párrafo del documento, busca su estilo (o el estilo base mediante
   * {@code basedOn}), obtiene el valor de interlineado definido en <w:spacing>
   * del estilo y lo convierte al formato utilizado por Apache POI, aplicándolo
   * mediante {@link XWPFParagraph#setSpacingBetween(double)}.
   * </p>
   *
   * @param document Documento DOCX al que se aplicará el interlineado.
   */

  private void applyStyleLineSpacing(XWPFDocument document) {
    if (document == null)
      return;

    for (XWPFParagraph paragraph : document.getParagraphs()) {
      String styleId = paragraph.getStyle();
      if (styleId != null) {
        XWPFStyle style = document.getStyles().getStyle(styleId);
        if (style != null) {
          String basedOnId = style.getCTStyle().getBasedOn() != null
              ? style.getCTStyle().getBasedOn().getVal()
              : null;
          if (basedOnId != null) {
            style = document.getStyles().getStyle(basedOnId);
          }
        }

        if (style != null) {
          CTPPrGeneral pPr = style.getCTStyle().getPPr();
          if (pPr != null && pPr.isSetSpacing() && pPr.getSpacing() != null && pPr.getSpacing().isSetLine()) {
            Object lineObj = pPr.getSpacing().getLine();
            double lineSpacing = 1.0;

            if (lineObj instanceof BigInteger lineBi) {
              lineSpacing = lineBi.doubleValue() / 240.0;
            } else {
              try {
                lineSpacing = new BigInteger(lineObj.toString()).doubleValue() / 240.0;
              } catch (NumberFormatException e) {
                log.error("applyStyleLineSpacing() - Error: ", e);
              }
            }
            paragraph.setSpacingBetween(lineSpacing);
          }
        }
      }
    }
  }

}
