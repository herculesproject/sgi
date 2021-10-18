package org.crue.hercules.sgi.tp.tasks;

import org.springframework.stereotype.Component;

@Component
public class EchoTask {

  public EchoTask() {
  }

  public void echo(String message) {
    System.out.println(message);
  }
}
