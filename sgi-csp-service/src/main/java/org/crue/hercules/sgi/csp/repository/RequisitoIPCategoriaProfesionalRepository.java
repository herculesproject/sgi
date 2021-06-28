package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.RequisitoIPCategoriaProfesional;
import org.crue.hercules.sgi.csp.repository.custom.CustomRequisitoIPCategoriaProfesionalRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RequisitoIPCategoriaProfesionalRepository extends JpaRepository<RequisitoIPCategoriaProfesional, Long>,
    JpaSpecificationExecutor<RequisitoIPCategoriaProfesional>, CustomRequisitoIPCategoriaProfesionalRepository {
}
