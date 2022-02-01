package org.crue.hercules.sgi.prc.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.apache.commons.collections4.ListUtils;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.prc.converter.ProduccionCientificaConverter;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiCreateInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiCreateInput.TipoEstadoProduccionCientifica;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiFullOutput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.AcreditacionInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.AutorInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.CampoProduccionCientificaInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.IndiceImpactoInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.ProyectoInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiOutput;
import org.crue.hercules.sgi.prc.exceptions.ProduccionCientificaNotFoundException;
import org.crue.hercules.sgi.prc.model.Acreditacion;
import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.model.BaseEntity;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica.CodigoCVN;
import org.crue.hercules.sgi.prc.model.ConfiguracionCampo;
import org.crue.hercules.sgi.prc.model.ConfiguracionCampo.TipoFormato;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.IndiceImpacto;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.Proyecto;
import org.crue.hercules.sgi.prc.model.ValorCampo;
import org.crue.hercules.sgi.prc.repository.AcreditacionRepository;
import org.crue.hercules.sgi.prc.repository.AutorGrupoRepository;
import org.crue.hercules.sgi.prc.repository.AutorRepository;
import org.crue.hercules.sgi.prc.repository.CampoProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.ConfiguracionCampoRepository;
import org.crue.hercules.sgi.prc.repository.EstadoProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.IndiceImpactoRepository;
import org.crue.hercules.sgi.prc.repository.ProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.ProyectoRepository;
import org.crue.hercules.sgi.prc.repository.ValorCampoRepository;
import org.crue.hercules.sgi.prc.repository.predicate.ProduccionCientificaPredicateResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Repository para gestionar el API de {@link ProduccionCientifica} procedente
 * del CVN.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class ProduccionCientificaApiService {

  private final Validator validator;

  private final ProduccionCientificaRepository produccionCientificaRepository;
  private final EstadoProduccionCientificaRepository estadoProduccionCientificaRepository;
  private final CampoProduccionCientificaRepository campoProduccionCientificaRepository;
  private final ValorCampoRepository valorCampoRepository;
  private final AutorRepository autorRepository;
  private final AutorGrupoRepository autorGrupoRepository;
  private final AcreditacionRepository acreditacionRepository;
  private final IndiceImpactoRepository indiceImpactoRepository;
  private final ProyectoRepository proyectoRepository;
  private final ConfiguracionCampoRepository configuracionCampoRepository;
  private final ProduccionCientificaConverter produccionCientificaConverter;

  @Autowired
  public ProduccionCientificaApiService(
      Validator validator,
      ProduccionCientificaRepository produccionCientificaRepository,
      EstadoProduccionCientificaRepository estadoProduccionCientificaRepository,
      CampoProduccionCientificaRepository campoProduccionCientificaRepository,
      ValorCampoRepository valorCampoRepository,
      AutorRepository autorRepository,
      AutorGrupoRepository autorGrupoRepository,
      AcreditacionRepository acreditacionRepository,
      IndiceImpactoRepository indiceImpactoRepository,
      ProyectoRepository proyectoRepository,
      ConfiguracionCampoRepository configuracionCampoRepository,
      ProduccionCientificaConverter converter) {
    this.validator = validator;
    this.produccionCientificaRepository = produccionCientificaRepository;
    this.estadoProduccionCientificaRepository = estadoProduccionCientificaRepository;
    this.campoProduccionCientificaRepository = campoProduccionCientificaRepository;
    this.valorCampoRepository = valorCampoRepository;
    this.autorRepository = autorRepository;
    this.autorGrupoRepository = autorGrupoRepository;
    this.acreditacionRepository = acreditacionRepository;
    this.indiceImpactoRepository = indiceImpactoRepository;
    this.proyectoRepository = proyectoRepository;
    this.configuracionCampoRepository = configuracionCampoRepository;
    this.produccionCientificaConverter = converter;
  }

  /**
   * Guardar un nuevo {@link ProduccionCientifica} y sus entidades relacionadas
   *
   * @param produccionCientificaApiInput la entidad
   *                                     {@link ProduccionCientificaApiCreateInput}
   *                                     a guardar.
   * @return la entidad {@link ProduccionCientificaApiFullOutput} persistida con
   *         sus entidades relacionadas.
   */
  @Transactional
  public ProduccionCientificaApiFullOutput create(ProduccionCientificaApiCreateInput produccionCientificaApiInput) {

    log.debug("create(ProduccionCientificaApiCreateInput produccionCientificaApiInput) - start");

    ProduccionCientifica produccionCientifica = ProduccionCientifica.builder()
        .epigrafeCVN(produccionCientificaApiInput.getEpigrafeCVN())
        .produccionCientificaRef(produccionCientificaApiInput.getProduccionCientificaRef())
        .build();

    validateProduccionCientifica(produccionCientifica, BaseEntity.Create.class);

    ProduccionCientifica produccionCientificaCreate = produccionCientificaRepository.save(produccionCientifica);

    EstadoProduccionCientifica estadoProduccionCientifica = addEstadoProduccionCientificaFromCreate(
        produccionCientificaCreate.getId(),
        produccionCientificaApiInput);

    produccionCientificaCreate.setEstado(estadoProduccionCientifica);

    validateProduccionCientifica(produccionCientifica, BaseEntity.Update.class);
    ProduccionCientifica produccionCientificaUpdate = produccionCientificaRepository.save(produccionCientificaCreate);

    List<CampoProduccionCientificaInput> campos = addCamposProduccionCientifica(produccionCientificaCreate.getId(),
        produccionCientificaApiInput.getCampos());
    List<AutorInput> autores = addAutores(produccionCientificaCreate.getId(),
        produccionCientificaApiInput.getAutores());
    List<IndiceImpactoInput> indicesImpacto = addIndicesImpacto(produccionCientificaCreate.getId(),
        produccionCientificaApiInput.getIndicesImpacto());
    List<AcreditacionInput> acreditaciones = addAcreditaciones(produccionCientificaCreate.getId(),
        produccionCientificaApiInput.getAcreditaciones());
    List<ProyectoInput> proyectos = addProyectos(produccionCientificaCreate.getId(),
        produccionCientificaApiInput.getProyectos());

    ProduccionCientificaApiFullOutput output = produccionCientificaConverter.convert(produccionCientificaUpdate);
    output.setEstado(produccionCientifica.getEstado().getEstado());
    output.setCampos(campos);
    output.setAutores(autores);
    output.setIndicesImpacto(indicesImpacto);
    output.setAcreditaciones(acreditaciones);
    output.setProyectos(proyectos);

    log.debug("create(ProduccionCientificaApiInput produccionCientificaApiInput) - end");
    return output;
  }

  private void validateProduccionCientifica(ProduccionCientifica produccionCientifica, Class<?> classGroup) {
    Set<ConstraintViolation<ProduccionCientifica>> result = validator.validate(produccionCientifica, classGroup);
    if (!result.isEmpty()) {
      throw new ConstraintViolationException(result);
    }
  }

  /**
   * Actualiza un {@link ProduccionCientifica} y sus entidades relacionadas
   *
   * @param produccionCientificaApiInput la entidad {@link ProduccionCientifica} a
   *                                     modificar.
   * @param produccionCientificaRef      produccionCientificaRef
   * @return la entidad {@link ProduccionCientifica} persistida con sus entidades
   *         relacionadas.
   */
  @Transactional
  public ProduccionCientificaApiFullOutput update(ProduccionCientificaApiInput produccionCientificaApiInput,
      String produccionCientificaRef) {

    log.debug("update(produccionCientificaApiInput, produccionCientificaRef) - start");

    ProduccionCientifica produccionCientificaUpdate = produccionCientificaRepository
        .findByProduccionCientificaRef(produccionCientificaRef)
        .orElseThrow(() -> new ProduccionCientificaNotFoundException(produccionCientificaRef));

    validateProduccionCientificaUpdate(produccionCientificaUpdate);

    Long produccionCientificaId = produccionCientificaUpdate.getId();
    updateEstado(produccionCientificaApiInput, produccionCientificaUpdate);

    List<CampoProduccionCientificaInput> campos = updateCampos(produccionCientificaApiInput.getCampos(),
        produccionCientificaId);

    List<AutorInput> autores = new ArrayList<>();
    if (produccionCientificaUpdate.getEstado().getEstado().equals(TipoEstadoProduccion.PENDIENTE)) {
      autores = updateAutores(produccionCientificaApiInput.getAutores(), produccionCientificaId);
    }

    List<IndiceImpactoInput> indicesImpacto = updateIndicesImpacto(produccionCientificaApiInput.getIndicesImpacto(),
        produccionCientificaId);

    List<AcreditacionInput> acreditaciones = updateAcreditaciones(produccionCientificaApiInput.getAcreditaciones(),
        produccionCientificaId);

    List<ProyectoInput> proyectos = updateProyectos(produccionCientificaApiInput.getProyectos(),
        produccionCientificaId);

    ProduccionCientificaApiFullOutput output = produccionCientificaConverter.convert(produccionCientificaUpdate);
    output.setEstado(produccionCientificaUpdate.getEstado().getEstado());
    output.setCampos(campos);
    output.setAutores(autores);
    output.setIndicesImpacto(indicesImpacto);
    output.setAcreditaciones(acreditaciones);
    output.setProyectos(proyectos);

    log.debug("update(produccionCientificaApiInput, produccionCientificaRef) - end");
    return output;
  }

  private void validateProduccionCientificaUpdate(ProduccionCientifica produccionCientificaUpdate) {
    Assert.isTrue(
        (produccionCientificaUpdate.getEstado().getEstado().equals(TipoEstadoProduccion.VALIDADO) ||
            produccionCientificaUpdate.getEstado().getEstado().equals(TipoEstadoProduccion.RECHAZADO)),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder()
            .key("org.crue.hercules.sgi.prc.exceptions.EstadoNotValidProduccionCientificaException.message").build());

    validateConfiguracionBaremoIsNull(produccionCientificaUpdate);
  }

  private void validateConfiguracionBaremoIsNull(ProduccionCientifica produccionCientificaUpdate) {
    Assert.isNull(
        produccionCientificaUpdate.getConvocatoriaBaremacionId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(
            "org.crue.hercules.sgi.prc.exceptions.ConvocatoriaBaremacionIdNotNullProduccionCientificaException.message")
            .build());
  }

  private void updateEstado(ProduccionCientificaApiInput produccionCientificaApiInput,
      ProduccionCientifica produccionCientificaUpdate) {
    Long produccionCientificaId = produccionCientificaUpdate.getId();

    boolean addEstadoPendiente = produccionCientificaUpdate.getEstado().getEstado()
        .equals(TipoEstadoProduccion.RECHAZADO);

    if (produccionCientificaUpdate.getEstado().getEstado().equals(TipoEstadoProduccion.VALIDADO)) {
      addEstadoPendiente = checkCamposValidacionAdicional(produccionCientificaApiInput, produccionCientificaId)
          || checkAutoresValidacionAdicional(produccionCientificaApiInput, produccionCientificaId);
    }

    if (addEstadoPendiente) {
      TipoEstadoProduccion tipoEstado = EstadoProduccionCientifica.TipoEstadoProduccion.PENDIENTE;
      EstadoProduccionCientifica estadoProduccionCientificaNew = addEstado(produccionCientificaId, tipoEstado);
      produccionCientificaUpdate.setEstado(estadoProduccionCientificaNew);
      produccionCientificaRepository.save(produccionCientificaUpdate);
    }
  }

  private boolean checkCamposValidacionAdicional(ProduccionCientificaApiInput produccionCientificaApiInput,
      Long produccionCientificaId) {
    return ListUtils.emptyIfNull(produccionCientificaApiInput.getCampos()).stream()
        .anyMatch(campoInput -> campoProduccionCientificaRepository
            .findByCodigoCVNAndProduccionCientificaId(campoInput.getCodigoCVN(), produccionCientificaId)
            .map(campoSearch -> hasValidacionAdicional(campoSearch.getCodigoCVN())
                && !valoresCampoEqualsValoresCampoInput(campoSearch, campoInput))
            .orElse(false));
  }

  private boolean valoresCampoEqualsValoresCampoInput(CampoProduccionCientifica campo,
      CampoProduccionCientificaInput campoInput) {

    List<ValorCampo> valores = valorCampoRepository.findAllByCampoProduccionCientificaId(campo.getId());

    if (valores.size() != campoInput.getValores().size()) {
      return false;
    }

    ConfiguracionCampo configuracionCampo = configuracionCampoRepository.findByCodigoCVN(campo.getCodigoCVN())
        .orElse(null);
    TipoFormato tipoFormato = null != configuracionCampo ? configuracionCampo.getTipoFormato() : null;

    for (int i = 0; i < valores.size(); i++) {
      ValorCampo valorCampo = valores.get(i);
      String valorString = campoInput.getValores().get(i);
      if (!valorCampoEqualsValorInput(valorCampo, valorString, tipoFormato)) {
        return false;
      }
    }
    return true;
  }

  private boolean checkAutoresValidacionAdicional(ProduccionCientificaApiInput produccionCientificaApiInput,
      Long produccionCientificaId) {
    return !ListUtils.emptyIfNull(produccionCientificaApiInput.getAutores()).stream()
        .filter(autorInput -> autorRepository.findAllByProduccionCientificaId(produccionCientificaId).stream()
            .noneMatch(autor -> autorEqualsAutorInput(autor, autorInput)))
        .collect(Collectors.toList()).isEmpty();
  }

  private boolean autorEqualsAutorInput(Autor autor, AutorInput autorInput) {
    return Objects.equals(autor.getFirma(), autorInput.getFirma()) &&
        Objects.equals(autor.getPersonaRef(), autorInput.getPersonaRef()) &&
        Objects.equals(autor.getNombre(), autorInput.getNombre()) &&
        Objects.equals(autor.getApellidos(), autorInput.getApellidos());
  }

  private boolean valorCampoEqualsValorInput(ValorCampo valorCampo, String valorCampoUpdate, TipoFormato tipoFormato) {
    String valorCampoUpdateFormated = formatValorByTipoFormato(valorCampoUpdate, tipoFormato);
    return Objects.equals(valorCampoUpdateFormated, valorCampo.getValor());
  }

  private String formatValorByTipoFormato(String valor, TipoFormato tipoFormato) {
    if (null != tipoFormato && tipoFormato.equals(TipoFormato.NUMERO)) {
      BigDecimal bd = new BigDecimal(valor).setScale(2, RoundingMode.HALF_UP);
      DecimalFormat df = new DecimalFormat("000000000000000.00");
      return df.format(bd.doubleValue());
    }
    return valor;
  }

  private boolean hasValidacionAdicional(CodigoCVN codigoCVN) {
    return configuracionCampoRepository.findByCodigoCVN(codigoCVN)
        .map(ConfiguracionCampo::getValidacionAdicional)
        .orElse(false);
  }

  private EstadoProduccionCientifica addEstado(Long produccionCientificaId, TipoEstadoProduccion tipoEstado) {
    EstadoProduccionCientifica estadoProduccionCientifica = EstadoProduccionCientifica.builder()
        .produccionCientificaId(produccionCientificaId)
        .estado(tipoEstado)
        .build();
    estadoProduccionCientifica.setFecha(Instant.now());
    estadoProduccionCientifica = estadoProduccionCientificaRepository.save(estadoProduccionCientifica);
    return estadoProduccionCientifica;
  }

  private List<CampoProduccionCientificaInput> updateCampos(List<CampoProduccionCientificaInput> campos,
      Long produccionCientificaId) {
    List<CampoProduccionCientificaInput> camposInputUpdate = new ArrayList<>();
    List<CampoProduccionCientificaInput> camposInputAdd = new ArrayList<>();

    ListUtils.emptyIfNull(campos).stream().forEach(campoInput -> {
      CampoProduccionCientifica campo = campoProduccionCientificaRepository
          .findByCodigoCVNAndProduccionCientificaId(campoInput.getCodigoCVN(), produccionCientificaId).orElse(null);

      if (null != campo) {
        if (!valoresCampoEqualsValoresCampoInput(campo, campoInput)) {
          camposInputUpdate.add(updateCampo(campo, campoInput));
        }
      } else {
        camposInputAdd.add(campoInput);
      }
    });

    List<CampoProduccionCientificaInput> camposSave = addCamposProduccionCientifica(produccionCientificaId,
        camposInputAdd);
    return Stream.concat(camposInputUpdate.stream(), camposSave.stream()).collect(Collectors.toList());
  }

  private CampoProduccionCientificaInput updateCampo(CampoProduccionCientifica campo,
      CampoProduccionCientificaInput campoInput) {

    CampoProduccionCientificaInput campoUpdate = new CampoProduccionCientificaInput();
    valorCampoRepository.deleteInBulkByCampoProduccionCientificaId(campo.getId());
    List<String> valoresSave = addValoresCampos(campo, campoInput.getValores());

    campoUpdate.setCodigo(campo.getCodigoCVN().getInternValue());
    campoUpdate.setValores(valoresSave);
    return campoUpdate;
  }

  private void deleteCamposAndValores(Long produccionCientificaId) {
    List<CampoProduccionCientifica> entitiesToDelete = campoProduccionCientificaRepository
        .findAllByProduccionCientificaId(produccionCientificaId);

    entitiesToDelete.stream()
        .forEach(entity -> valorCampoRepository.deleteInBulkByCampoProduccionCientificaId(entity.getId()));

    campoProduccionCientificaRepository.deleteInBulkByProduccionCientificaId(produccionCientificaId);
  }

  private List<AutorInput> updateAutores(List<AutorInput> autores, Long produccionCientificaId) {
    deleteAutoresAndGrupos(produccionCientificaId);

    return addAutores(produccionCientificaId, autores);
  }

  private void deleteAutoresAndGrupos(Long produccionCientificaId) {
    List<Autor> entitiesToDelete = autorRepository
        .findAllByProduccionCientificaId(produccionCientificaId);

    entitiesToDelete.stream()
        .forEach(entity -> autorGrupoRepository.deleteInBulkByAutorId(entity.getId()));

    autorRepository.deleteInBulkByProduccionCientificaId(produccionCientificaId);
  }

  private List<AcreditacionInput> updateAcreditaciones(List<AcreditacionInput> acreditaciones,
      Long produccionCientificaId) {
    acreditacionRepository.deleteInBulkByProduccionCientificaId(produccionCientificaId);

    return addAcreditaciones(produccionCientificaId, acreditaciones);
  }

  private List<IndiceImpactoInput> updateIndicesImpacto(List<IndiceImpactoInput> indicesImpacto,
      Long produccionCientificaId) {
    indiceImpactoRepository.deleteInBulkByProduccionCientificaId(produccionCientificaId);

    return addIndicesImpacto(produccionCientificaId, indicesImpacto);
  }

  private List<ProyectoInput> updateProyectos(List<ProyectoInput> proyectos, Long produccionCientificaId) {
    proyectoRepository.deleteInBulkByProduccionCientificaId(produccionCientificaId);

    return addProyectos(produccionCientificaId, proyectos);
  }

  private EstadoProduccionCientifica addEstadoProduccionCientificaFromCreate(Long idProduccionCientifica,
      ProduccionCientificaApiCreateInput produccionCientificaApiInput) {

    TipoEstadoProduccion tipoEstado = null != produccionCientificaApiInput.getEstado()
        && produccionCientificaApiInput.getEstado().equals(TipoEstadoProduccionCientifica.VALIDADO)
            ? EstadoProduccionCientifica.TipoEstadoProduccion.VALIDADO
            : EstadoProduccionCientifica.TipoEstadoProduccion.PENDIENTE;

    return addEstado(idProduccionCientifica, tipoEstado);
  }

  private List<CampoProduccionCientificaInput> addCamposProduccionCientifica(Long idProduccionCientifica,
      List<CampoProduccionCientificaInput> camposInput) {
    List<CampoProduccionCientificaInput> campos = new ArrayList<>();

    ListUtils.emptyIfNull(camposInput).stream().forEach(campo -> {
      CampoProduccionCientifica campoProduccionCientifica = CampoProduccionCientifica.builder()
          .produccionCientificaId(idProduccionCientifica)
          .codigoCVN(campo.getCodigoCVN())
          .build();

      campoProduccionCientifica = campoProduccionCientificaRepository.save(campoProduccionCientifica);

      List<String> valores = addValoresCampos(campoProduccionCientifica, campo.getValores());

      CampoProduccionCientificaInput campoInput = new CampoProduccionCientificaInput();
      campoInput.setCodigo(campoProduccionCientifica.getCodigoCVN().getInternValue());
      campoInput.setValores(valores);
      campos.add(campoInput);
    });

    return campos;
  }

  private List<String> addValoresCampos(CampoProduccionCientifica campo, List<String> valores) {
    List<String> valoresCamposSave = new ArrayList<>();

    ConfiguracionCampo configuracionCampo = configuracionCampoRepository.findByCodigoCVN(campo.getCodigoCVN())
        .orElse(null);
    TipoFormato tipoFormato = null != configuracionCampo ? configuracionCampo.getTipoFormato() : null;

    AtomicInteger fieldIndex = new AtomicInteger();
    ListUtils.emptyIfNull(valores).stream().forEach(valor -> {
      ValorCampo valorCampoNew = ValorCampo.builder()
          .campoProduccionCientificaId(campo.getId())
          .orden(fieldIndex.getAndIncrement() + 1)
          .valor(formatValorByTipoFormato(valor, tipoFormato))
          .build();

      Set<ConstraintViolation<ValorCampo>> result = validator.validate(valorCampoNew, BaseEntity.Create.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      valorCampoNew = valorCampoRepository.save(valorCampoNew);
      valoresCamposSave.add(valorCampoNew.getValor());
    });

    return valoresCamposSave;
  }

  private List<AutorInput> addAutores(Long idProduccionCientifica, List<AutorInput> autores) {
    List<AutorInput> autoresSave = new ArrayList<>();

    ListUtils.emptyIfNull(autores).stream().forEach(autor -> {
      // TODO validar personaRef y grupos de autores

      Autor autorNew = Autor.builder()
          .produccionCientificaId(idProduccionCientifica)
          .firma(autor.getFirma())
          .personaRef(autor.getPersonaRef())
          .nombre(autor.getNombre())
          .apellidos(autor.getApellidos())
          .orcidId(autor.getOrcidId())
          .orden(autor.getOrden())
          .ip(Objects.isNull(autor.getIp()) ? Boolean.FALSE : autor.getIp())
          .build();

      Set<ConstraintViolation<Autor>> result = validator.validate(autorNew, BaseEntity.Create.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      autorNew = autorRepository.save(autorNew);
      autoresSave.add(produccionCientificaConverter.convertAutor(autorNew));
    });

    return autoresSave;
  }

  private List<IndiceImpactoInput> addIndicesImpacto(Long idProduccionCientifica,
      List<IndiceImpactoInput> indicesImpacto) {
    List<IndiceImpactoInput> indicesImpactoSave = new ArrayList<>();

    ListUtils.emptyIfNull(indicesImpacto).stream().forEach(indiceImpacto -> {
      IndiceImpacto indiceImpactoNew = IndiceImpacto.builder()
          .produccionCientificaId(idProduccionCientifica)
          .fuenteImpacto(indiceImpacto.getFuenteImpacto())
          .ranking(indiceImpacto.getRanking())
          .anio(indiceImpacto.getAnio())
          .otraFuenteImpacto(indiceImpacto.getOtraFuenteImpacto())
          .numeroRevistas(indiceImpacto.getNumeroRevistas())
          .revista25(
              Objects.isNull(indiceImpacto.getRevista25()) ? Boolean.FALSE : indiceImpacto.getRevista25())
          .build();

      Set<ConstraintViolation<IndiceImpacto>> result = validator.validate(indiceImpactoNew, BaseEntity.Create.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      indiceImpactoNew = indiceImpactoRepository.save(indiceImpactoNew);
      indicesImpactoSave.add(produccionCientificaConverter.convertIndiceImpacto(indiceImpactoNew));
    });

    return indicesImpactoSave;
  }

  private List<AcreditacionInput> addAcreditaciones(Long idProduccionCientifica,
      List<AcreditacionInput> acreditaciones) {
    List<AcreditacionInput> acreditacionesSave = new ArrayList<>();

    ListUtils.emptyIfNull(acreditaciones).stream().forEach(acreditacion -> {
      Acreditacion acreditacionNew = Acreditacion.builder()
          .produccionCientificaId(idProduccionCientifica)
          .documentoRef(acreditacion.getDocumentoRef())
          .url(acreditacion.getUrl())
          .build();

      Set<ConstraintViolation<Acreditacion>> result = validator.validate(acreditacionNew, BaseEntity.Create.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      acreditacionNew = acreditacionRepository.save(acreditacionNew);
      acreditacionesSave.add(produccionCientificaConverter.convertAcreditacion(acreditacionNew));
    });

    return acreditacionesSave;
  }

  private List<ProyectoInput> addProyectos(Long idProduccionCientifica,
      List<ProyectoInput> proyectos) {
    List<ProyectoInput> proyectosSave = new ArrayList<>();

    ListUtils.emptyIfNull(proyectos).stream().forEach(proyecto -> {

      Proyecto proyectoNew = Proyecto.builder()
          .produccionCientificaId(idProduccionCientifica)
          .proyectoRef(proyecto.getProyectoRef())
          .build();

      Set<ConstraintViolation<Proyecto>> result = validator.validate(proyectoNew, BaseEntity.Create.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      proyectoNew = proyectoRepository.save(proyectoNew);
      proyectosSave.add(produccionCientificaConverter.convertProyecto(proyectoNew));
    });

    return proyectosSave;
  }

  @Transactional
  public void delete(String produccionCientificaRef) {
    ProduccionCientifica produccionCientifica = produccionCientificaRepository
        .findByProduccionCientificaRef(produccionCientificaRef)
        .orElseThrow(() -> new ProduccionCientificaNotFoundException(produccionCientificaRef));

    validateConfiguracionBaremoIsNull(produccionCientifica);

    // TODO Falta validar que el epigrafeCVN sea uno de los que llegan por el CVN
    // (en la
    // tabla ConfiguracionBaremo el tipo de Fuente es CVN o CVN_OTRO_SISTEMA)

    Long produccionCientificaId = produccionCientifica.getId();

    deleteCamposAndValores(produccionCientificaId);
    deleteAutoresAndGrupos(produccionCientificaId);
    acreditacionRepository.deleteInBulkByProduccionCientificaId(produccionCientificaId);
    indiceImpactoRepository.deleteInBulkByProduccionCientificaId(produccionCientificaId);
    proyectoRepository.deleteInBulkByProduccionCientificaId(produccionCientificaId);
    produccionCientificaRepository.updateEstadoNull(produccionCientificaId);
    estadoProduccionCientificaRepository.deleteInBulkByProduccionCientificaId(produccionCientificaId);
    produccionCientificaRepository.deleteById(produccionCientificaId);
  }

  public List<ProduccionCientificaApiOutput> findByEstadoValidadoOrRechazadoByFechaModificacion(String query) {
    return produccionCientificaConverter.convertProduccionCientificaEstadoResumen(
        produccionCientificaRepository.findByEstadoValidadoOrRechazadoByFechaModificacion(
            SgiRSQLJPASupport.toSpecification(query, ProduccionCientificaPredicateResolver.getInstance())));
  }

}
