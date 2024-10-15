package org.crue.hercules.sgi.rep.report.data.objects;

import java.util.ArrayList;
import java.util.List;

import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.rep.dto.eti.ApartadoOutput;
import org.crue.hercules.sgi.rep.dto.eti.BloqueOutput;
import org.crue.hercules.sgi.rep.dto.eti.ComentarioDto;

import lombok.Getter;

@Getter
public class BloqueApartadoObject {
  private String nombre;
  private List<Apartado> apartados = new ArrayList<>();

  public BloqueApartadoObject(BloqueOutput dto, Language lang) {
    this.nombre = dto.getNombre();
    if (dto != null) {
      this.apartados = dto.getApartados().stream().map(a -> new Apartado(a, lang)).toList();
    }
  }

  @Getter
  private class Apartado {
    private String titulo;
    private List<Comentario> comentarios = new ArrayList<>();
    private List<Apartado> apartadosHijos = new ArrayList<>();

    private Apartado(ApartadoOutput dto, Language lang) {
      this.titulo = dto.getTitulo();
      if (dto.getComentarios() != null) {
        this.comentarios = dto.getComentarios().stream().map(c -> new Comentario(c, lang)).toList();
      }
      if (dto.getApartadosHijos() != null) {
        this.apartadosHijos = dto.getApartadosHijos().stream().map(a -> new Apartado(a, lang)).toList();
      }
    }

    @Getter
    private class Comentario {
      private String texto;
      private String sexoPersonaCreated;
      private String personaCreated;

      private Comentario(ComentarioDto dto, Language lang) {
        this.texto = I18nHelper.getValueForLanguage(dto.getTexto(), lang);
        this.sexoPersonaCreated = dto.getSexoPersonaCreated();
        this.personaCreated = dto.getPersonaCreated();
      }
    }
  }
}
