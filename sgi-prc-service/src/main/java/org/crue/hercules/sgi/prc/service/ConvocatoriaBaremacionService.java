package org.crue.hercules.sgi.prc.service;

import java.time.Instant;

import org.crue.hercules.sgi.prc.exceptions.ConvocatoriaBaremacionNotFoundException;
import org.crue.hercules.sgi.prc.model.Baremo;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.crue.hercules.sgi.prc.model.Modulador;
import org.crue.hercules.sgi.prc.model.Rango;
import org.crue.hercules.sgi.prc.repository.BaremoRepository;
import org.crue.hercules.sgi.prc.repository.ConvocatoriaBaremacionRepository;
import org.crue.hercules.sgi.prc.repository.ModuladorRepository;
import org.crue.hercules.sgi.prc.repository.RangoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para ConvocatoriaBaremacion
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class ConvocatoriaBaremacionService {

  private final ConvocatoriaBaremacionRepository convocatoriaBaremacionRepository;
  private final ModuladorRepository moduladorRepository;
  private final RangoRepository rangoRepository;
  private final BaremoRepository baremoRepository;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public ConvocatoriaBaremacion updateFechaInicioEjecucion(Long convocatoriaBaremacionId,
      Instant fechaInicioEjecucion) {
    log.debug("updateFechaInicioEjecucion(convocatoriaBaremacionId, fechaInicioEjecucion) - start");

    return convocatoriaBaremacionRepository
        .findById(convocatoriaBaremacionId).map(convocatoria -> {
          convocatoria.setFechaInicioEjecucion(fechaInicioEjecucion);
          return convocatoriaBaremacionRepository.save(convocatoria);
        }).orElse(null);
  }

  @Transactional
  public ConvocatoriaBaremacion clone(Long convocatoriaBaremacionId, String nombre) {
    log.debug("clone(convocatoriaBaremacionId, nombre) - start");

    return convocatoriaBaremacionRepository
        .findById(convocatoriaBaremacionId).map(convocatoria -> {
          ConvocatoriaBaremacion convocatoriaBaremacionClone = ConvocatoriaBaremacion.builder()
              .nombre(nombre)
              .ultimoAnio(convocatoria.getUltimoAnio())
              .anio(convocatoria.getAnio())
              .aniosBaremables(convocatoria.getAniosBaremables())
              .importeTotal(convocatoria.getImporteTotal())
              .partidaPresupuestaria(convocatoria.getPartidaPresupuestaria())
              .build();

          ConvocatoriaBaremacion convocatoriaBaremacionNew = convocatoriaBaremacionRepository
              .save(convocatoriaBaremacionClone);
          Long convocatoriaBaremacionIdNew = convocatoriaBaremacionNew.getId();

          baremoRepository.findByConvocatoriaBaremacionId(convocatoriaBaremacionId).stream()
              .forEach(baremo -> cloneBaremo(convocatoriaBaremacionIdNew, baremo));

          moduladorRepository.findByConvocatoriaBaremacionId(convocatoriaBaremacionId).stream()
              .forEach(modulador -> cloneModulador(convocatoriaBaremacionIdNew, modulador));

          rangoRepository.findByConvocatoriaBaremacionId(convocatoriaBaremacionId).stream()
              .forEach(rango -> cloneRango(convocatoriaBaremacionIdNew, rango));

          return convocatoriaBaremacionClone;
        }).orElseThrow(() -> new ConvocatoriaBaremacionNotFoundException(convocatoriaBaremacionId));
  }

  private void cloneRango(Long convocatoriaBaremacionIdNew, Rango rango) {
    Rango rangoClone = Rango.builder()
        .convocatoriaBaremacionId(convocatoriaBaremacionIdNew)
        .tipoRango(rango.getTipoRango())
        .tipoTemporalidad(rango.getTipoTemporalidad())
        .desde(rango.getDesde())
        .hasta(rango.getHasta())
        .puntos(rango.getPuntos())
        .build();
    rangoRepository.save(rangoClone);
  }

  private void cloneModulador(Long convocatoriaBaremacionIdNew, Modulador modulador) {
    Modulador moduladorClone = Modulador.builder()
        .convocatoriaBaremacionId(convocatoriaBaremacionIdNew)
        .areaRef(modulador.getAreaRef())
        .tipo(modulador.getTipo())
        .valor1(modulador.getValor1())
        .valor2(modulador.getValor2())
        .valor3(modulador.getValor3())
        .valor4(modulador.getValor4())
        .valor5(modulador.getValor5())
        .build();
    moduladorRepository.save(moduladorClone);
  }

  private void cloneBaremo(Long convocatoriaBaremacionIdNew, Baremo baremo) {
    Baremo baremoClone = Baremo.builder()
        .configuracionBaremoId(baremo.getConfiguracionBaremoId())
        .convocatoriaBaremacionId(convocatoriaBaremacionIdNew)
        .peso(baremo.getPeso())
        .cuantia(baremo.getCuantia())
        .tipoCuantia(baremo.getTipoCuantia())
        .puntos(baremo.getPuntos())
        .build();
    baremoRepository.save(baremoClone);
  }

}
