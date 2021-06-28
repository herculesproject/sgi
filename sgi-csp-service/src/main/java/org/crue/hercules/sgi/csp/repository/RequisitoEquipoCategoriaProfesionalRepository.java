package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.RequisitoEquipoCategoriaProfesional;
import org.crue.hercules.sgi.csp.repository.custom.CustomRequisitoEquipoCategoriaProfesionalRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RequisitoEquipoCategoriaProfesionalRepository
    extends JpaRepository<RequisitoEquipoCategoriaProfesional, Long>,
    JpaSpecificationExecutor<RequisitoEquipoCategoriaProfesional>, CustomRequisitoEquipoCategoriaProfesionalRepository {
}
