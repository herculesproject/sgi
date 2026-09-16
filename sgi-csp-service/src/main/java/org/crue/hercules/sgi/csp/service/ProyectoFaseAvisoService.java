package org.crue.hercules.sgi.csp.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.crue.hercules.sgi.csp.converter.ComConverter;
import org.crue.hercules.sgi.csp.dto.ProyectoFaseAvisoInput;
import org.crue.hercules.sgi.csp.dto.com.EmailOutput;
import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.crue.hercules.sgi.csp.dto.tp.SgiApiInstantTaskOutput;
import org.crue.hercules.sgi.csp.exceptions.ProyectoFaseNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SentAvisoNotUpdatableException;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFaseAviso;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.ProyectoFase;
import org.crue.hercules.sgi.csp.model.ProyectoFaseAviso;
import org.crue.hercules.sgi.csp.repository.ProyectoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoFaseAvisoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoFaseRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoEquipoSpecifications;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiComService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiSgpService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiTpService;
import org.crue.hercules.sgi.csp.util.ComGenericEmailTextHelper;
import org.crue.hercules.sgi.csp.util.ComRecipientHelper;
import org.crue.hercules.sgi.framework.problem.exception.ProblemException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProyectoFaseAvisoService {

  private static final String MSG_KEY_ENTITY = "entity";
  private static final String MSG_KEY_DATE = "date";
  private static final String MSG_MODEL_FECHA_ENVIO = "org.crue.hercules.sgi.csp.model.FechaEnvio.message";
  private static final String MSG_ENTITY_FECHA_ANTERIOR = "org.springframework.util.Assert.entity.fecha.anterior.message";

  private final SgiApiComService emailService;
  private final SgiApiTpService sgiApiTaskService;
  private final SgiApiSgpService personaService;
  private final ProyectoEquipoRepository proyectoEquipoRepository;
  private final ProyectoFaseAvisoRepository proyectoFaseAvisoRepository;
  private final ProyectoFaseRepository proyectoFaseRepository;

  /**
   * Obtiene el listado de destinatarios adicionales a los que enviar el email
   * generado por una fase en base al {@link ConvocatoriaFaseAviso} relacionado
   * 
   * @param proyectoFaseId identificador de {@link ProyectoFase}
   * @return listado de {@link Recipient}
   */
  public List<Recipient> getDeferredRecipients(Long proyectoFaseId) {
    ProyectoFase fase = proyectoFaseRepository.findById(proyectoFaseId)
        .orElseThrow(() -> new ProyectoFaseNotFoundException(proyectoFaseId));
    List<String> solicitantes = new ArrayList<>();

    solicitantes.addAll(getAditionalSolicitantesIfNeeded(fase.getProyectoId(),
        fase.getProyectoFaseAviso1()));
    solicitantes.addAll(getAditionalSolicitantesIfNeeded(fase.getProyectoId(),
        fase.getProyectoFaseAviso2()));

    List<String> solicitantesDistinct = solicitantes.stream().distinct().toList();

    if (!CollectionUtils.isEmpty(solicitantesDistinct)) {
      return ComConverter.toRecipients(personaService.findAllByIdIn(solicitantesDistinct));
    }

    return new LinkedList<>();
  }

  private List<String> getAditionalSolicitantesIfNeeded(Long proyectoId, ProyectoFaseAviso aviso) {
    List<String> solicitantes = new LinkedList<>();
    if (aviso != null && Boolean.TRUE.equals(aviso.getIncluirIpsProyecto())) {
      solicitantes.addAll(proyectoEquipoRepository.findAll(
          ProyectoEquipoSpecifications
              .byProyectoActivoAndProyectoConvocatoriaIdWithIpsActivos(proyectoId))
          .stream().map(ProyectoEquipo::getPersonaRef).toList());
    }
    return solicitantes;
  }

  @Transactional
  public ProyectoFaseAviso create(Long proyectoFaseId, ProyectoFaseAvisoInput avisoInput) {
    if (avisoInput == null) {
      return null;
    }

    Instant now = Instant.now();

    Assert.isTrue(avisoInput.getFechaEnvio().isAfter(now),
        () -> ProblemMessage.builder().key(
            MSG_ENTITY_FECHA_ANTERIOR)
            .parameter(
                MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(
                    MSG_MODEL_FECHA_ENVIO))
            .parameter(
                MSG_KEY_DATE, now
                    .toString())
            .build());

    Long emailId = this.emailService.createProyectoFaseEmail(
        proyectoFaseId,
        avisoInput.getAsunto(), avisoInput.getContenido(),
        avisoInput.getDestinatarios().stream()
            .map(destinatario -> new Recipient(destinatario.getNombre(), destinatario.getEmail()))
            .toList());
    Long taskId = null;
    try {
      taskId = this.sgiApiTaskService.createSendEmailTask(
          emailId,
          avisoInput.getFechaEnvio());
    } catch (Exception ex) {
      log.warn("Error creando tarea programada. Se elimina el email");
      this.emailService.deleteEmail(emailId);
      throw ex;
    }

    ProyectoFaseAviso aviso = new ProyectoFaseAviso();
    aviso.setComunicadoRef(emailId.toString());
    aviso.setTareaProgramadaRef(taskId.toString());
    aviso.setIncluirIpsProyecto(avisoInput.getIncluirIpsProyecto());
    log.debug("create - proyectoFaseId: {}, accion: CREADO, comunicadoRef: {}, tareaProgramadaRef: {}, fechaEnvio: {}",
        proyectoFaseId, emailId, taskId, avisoInput.getFechaEnvio());
    return proyectoFaseAvisoRepository.save(aviso);
  }

  /**
   * Elimina el aviso de una {@link ProyectoFase}, su comunicado y su tarea
   * programada si no ha sido enviado todavia.
   *
   * @param avisoInput        aviso entrante
   * @param proyectoFaseAviso aviso persistido
   * @param avisoEnviado      excepcion a lanzar si el aviso ya ha sido enviado
   * @return <code>true</code> Si se puede eliminar, <code>false</code> en
   *         cualquier otro caso
   */
  @Transactional
  public boolean deleteAvisoIfPossible(ProyectoFaseAvisoInput avisoInput,
      ProyectoFaseAviso proyectoFaseAviso, Supplier<ProblemException> avisoEnviado) {
    if (avisoInput != null || proyectoFaseAviso == null) {
      return false;
    }

    // Comprobamos que se puede borrar el aviso.
    SgiApiInstantTaskOutput task = sgiApiTaskService
        .findInstantTaskById(Long.parseLong(proyectoFaseAviso.getTareaProgramadaRef()));
    if (!task.getInstant().isAfter(Instant.now())) {
      throw avisoEnviado.get();
    }

    sgiApiTaskService.deleteTask(Long.parseLong(proyectoFaseAviso.getTareaProgramadaRef()));
    emailService.deleteEmail(Long.parseLong(proyectoFaseAviso.getComunicadoRef()));
    proyectoFaseAvisoRepository.delete(proyectoFaseAviso);

    log.debug("deleteAvisoIfPossible - avisoId: {}, comunicadoRef: {}, tareaProgramadaRef: {}",
        proyectoFaseAviso.getId(), proyectoFaseAviso.getComunicadoRef(),
        proyectoFaseAviso.getTareaProgramadaRef());
    return true;
  }

  /**
   * Actualiza el comunicado y la tarea programada del aviso de una
   * {@link ProyectoFase} si aun no se ha enviado. Si ya se ha enviado, sólo
   * se permite guardar la fase si los datos del aviso no cambian.
   *
   * @param avisoInput        aviso entrante
   * @param proyectoFaseAviso aviso persistido
   * @param proyectoFaseId    identificador de la {@link ProyectoFase} del aviso
   * @throws SentAvisoNotUpdatableException si el aviso ya ha sido enviado y
   *                                        alguno de sus datos ha cambiado
   */
  @Transactional
  public void updateAvisoIfNeeded(ProyectoFaseAvisoInput avisoInput, ProyectoFaseAviso proyectoFaseAviso,
      Long proyectoFaseId) {
    if (avisoInput == null || proyectoFaseAviso == null) {
      log.debug("updateAvisoIfNeeded - proyectoFaseId: {}, accion: SIN_AVISO", proyectoFaseId);
      return;
    }

    SgiApiInstantTaskOutput task = sgiApiTaskService
        .findInstantTaskById(Long.parseLong(proyectoFaseAviso.getTareaProgramadaRef()));

    List<Recipient> destinatarios = avisoInput.getDestinatarios().stream()
        .map(destinatario -> new Recipient(destinatario.getNombre(), destinatario.getEmail()))
        .toList();

    if (!task.getInstant().isAfter(Instant.now())) {
      if (hasAvisoChanged(avisoInput, destinatarios, proyectoFaseAviso, task)) {
        throw new SentAvisoNotUpdatableException();
      }

      log.debug(
          "updateAvisoIfNeeded - proyectoFaseId: {}, accion: OMITIDO (aviso ya enviado, sin cambios), comunicadoRef: {}, fechaEnvio: {}",
          proyectoFaseId, proyectoFaseAviso.getComunicadoRef(), task.getInstant());
      return;
    }

    this.emailService.updateProyectoFaseEmail(
        Long.parseLong(proyectoFaseAviso.getComunicadoRef()), proyectoFaseId,
        avisoInput.getAsunto(), avisoInput.getContenido(), destinatarios);

    this.sgiApiTaskService.updateSendEmailTask(
        Long.parseLong(proyectoFaseAviso.getTareaProgramadaRef()),
        Long.parseLong(proyectoFaseAviso.getComunicadoRef()),
        avisoInput.getFechaEnvio());

    proyectoFaseAviso.setIncluirIpsProyecto(avisoInput.getIncluirIpsProyecto());
    proyectoFaseAvisoRepository.save(proyectoFaseAviso);
    log.debug("updateAvisoIfNeeded - proyectoFaseId: {}, accion: ACTUALIZADO, comunicadoRef: {}, fechaEnvio: {}",
        proyectoFaseId, proyectoFaseAviso.getComunicadoRef(), avisoInput.getFechaEnvio());
  }

  /**
   * Comprueba si los datos del aviso son distintos de los que existian,
   * tanto en CSP como en el comunicado almacenado en el modulo COM.
   *
   * @param avisoInput        aviso entrante
   * @param destinatarios     destinatarios entrantes
   * @param proyectoFaseAviso aviso persistido
   * @param task              tarea programada del aviso persistido
   * @return <code>true</code> si alguno de los datos del aviso ha cambiado
   */
  private boolean hasAvisoChanged(ProyectoFaseAvisoInput avisoInput, List<Recipient> destinatarios,
      ProyectoFaseAviso proyectoFaseAviso, SgiApiInstantTaskOutput task) {
    if (!Objects.equals(avisoInput.getFechaEnvio(), task.getInstant())
        || !Objects.equals(avisoInput.getIncluirIpsProyecto(), proyectoFaseAviso.getIncluirIpsProyecto())) {
      return true;
    }

    EmailOutput comunicado = this.emailService
        .findGenericEmailTextById(Long.parseLong(proyectoFaseAviso.getComunicadoRef()));

    return !Objects.equals(avisoInput.getAsunto(), ComGenericEmailTextHelper.getSubject(comunicado))
        || !Objects.equals(avisoInput.getContenido(), ComGenericEmailTextHelper.getContent(comunicado))
        || !ComRecipientHelper.haveSameRecipients(destinatarios, comunicado.getRecipients());
  }

}
