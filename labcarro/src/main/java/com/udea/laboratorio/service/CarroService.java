package com.udea.laboratorio.service;

import com.udea.laboratorio.domain.Carro;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link Carro}.
 */
public interface CarroService {

    /**
     * Save a carro.
     *
     * @param carro the entity to save.
     * @return the persisted entity.
     */
    Carro save(Carro carro);

    /**
     * Get all the carros.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Carro> findAll(Pageable pageable);


    /**
     * Get the "id" carro.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Carro> findOne(Long id);

    /**
     * Delete the "id" carro.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the carro corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Carro> search(String query, Pageable pageable);
}
