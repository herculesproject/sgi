package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.Dictamen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link Dictamen}.
 */
@Repository
public interface DictamenRepository extends JpaRepository<Dictamen, Long>, JpaSpecificationExecutor<Dictamen> {

}