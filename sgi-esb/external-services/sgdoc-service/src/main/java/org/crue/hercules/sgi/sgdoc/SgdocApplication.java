package org.crue.hercules.sgi.sgdoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class SgdocApplication {

  public static void main(String[] args) {
    log.debug("main(String[] args) - start");
    SpringApplication.run(SgdocApplication.class, args);
    log.debug("main(String[] args) - end");
  }

}
