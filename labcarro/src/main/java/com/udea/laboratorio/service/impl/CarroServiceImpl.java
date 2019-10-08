package com.udea.laboratorio.service.impl;

import com.udea.laboratorio.service.CarroService;
import com.udea.laboratorio.domain.Carro;
import com.udea.laboratorio.repository.CarroRepository;
import com.udea.laboratorio.repository.search.CarroSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link Carro}.
 */
@Service
@Transactional
public class CarroServiceImpl implements CarroService {

    private final Logger log = LoggerFactory.getLogger(CarroServiceImpl.class);

    private final CarroRepository carroRepository;

    private final CarroSearchRepository carroSearchRepository;

    public CarroServiceImpl(CarroRepository carroRepository, CarroSearchRepository carroSearchRepository) {
        this.carroRepository = carroRepository;
        this.carroSearchRepository = carroSearchRepository;
    }

    /**
     * Save a carro.
     *
     * @param carro the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Carro save(Carro carro) {
        log.debug("Request to save Carro : {}", carro);
        Carro result = carroRepository.save(carro);
        carroSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the carros.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Carro> findAll(Pageable pageable) {
        log.debug("Request to get all Carros");
        return carroRepository.findAll(pageable);
    }


    /**
     * Get one carro by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Carro> findOne(Long id) {
        log.debug("Request to get Carro : {}", id);
        return carroRepository.findById(id);
    }

    /**
     * Delete the carro by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Carro : {}", id);
        carroRepository.deleteById(id);
        carroSearchRepository.deleteById(id);
    }

    /**
     * Search for the carro corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Carro> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Carros for query {}", query);
        return carroSearchRepository.search(queryStringQuery(query), pageable);    }
}
