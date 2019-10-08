package com.udea.laboratorio.repository;
import com.udea.laboratorio.domain.Carro;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Carro entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CarroRepository extends JpaRepository<Carro, Long> {

}
