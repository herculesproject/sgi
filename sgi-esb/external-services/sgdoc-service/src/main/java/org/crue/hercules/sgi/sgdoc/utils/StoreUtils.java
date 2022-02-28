package org.crue.hercules.sgi.sgdoc.utils;

import java.io.File;
import java.time.format.DateTimeFormatter;

import org.crue.hercules.sgi.sgdoc.model.Documento;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class StoreUtils {

  public static final String SAMPLE_DATA_PREFIX = "sample-";
  private static final String PATTERN = "YYYY" + File.separator + "MM" + File.separator + "dd";
  private static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(PATTERN);

  private StoreUtils() {
    // To prevent instances
  }

  private static String getRelativePath(Documento documento) {
    return FORMATTER.format(documento.getFechaCreacion()) + File.separator + documento.getDocumentoRef();
  }

  private static String getAbsolutePath(String storePath, Documento documento) {
    return storePath + File.separator + getRelativePath(documento);
  }

  public static Resource getResource(String storePath, Documento documento) {
    if (documento.getDocumentoRef().startsWith(SAMPLE_DATA_PREFIX)) {
      return new ClassPathResource("db/changelog/changes/sample-blob/" + documento.getDocumentoRef().replace('-', '.'));
    } else {
      return new FileSystemResource(getAbsolutePath(storePath, documento));
    }
  }
}
