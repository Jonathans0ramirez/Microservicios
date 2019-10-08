package com.udea.laboratorio.web.rest;

import com.udea.laboratorio.domain.Carro;
import com.udea.laboratorio.service.CarroService;
import com.udea.laboratorio.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link com.udea.laboratorio.domain.Carro}.
 */
@RestController
@RequestMapping("/api")
public class CarroResource {

    private final Logger log = LoggerFactory.getLogger(CarroResource.class);

    private static final String ENTITY_NAME = "labcarroCarro";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CarroService carroService;

    public CarroResource(CarroService carroService) {
        this.carroService = carroService;
    }

    /**
     * {@code POST  /carros} : Create a new carro.
     *
     * @param carro the carro to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new carro, or with status {@code 400 (Bad Request)} if the carro has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/carros")
    public ResponseEntity<Carro> createCarro(@Valid @RequestBody Carro carro) throws URISyntaxException {
        log.debug("REST request to save Carro : {}", carro);
        if (carro.getId() != null) {
            throw new BadRequestAlertException("A new carro cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Carro result = carroService.save(carro);
        return ResponseEntity.created(new URI("/api/carros/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /carros} : Updates an existing carro.
     *
     * @param carro the carro to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated carro,
     * or with status {@code 400 (Bad Request)} if the carro is not valid,
     * or with status {@code 500 (Internal Server Error)} if the carro couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/carros")
    public ResponseEntity<Carro> updateCarro(@Valid @RequestBody Carro carro) throws URISyntaxException {
        log.debug("REST request to update Carro : {}", carro);
        if (carro.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Carro result = carroService.save(carro);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, carro.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /carros} : get all the carros.
     *

     * @param pageable the pagination information.

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of carros in body.
     */
    @GetMapping("/carros")
    public ResponseEntity<List<Carro>> getAllCarros(Pageable pageable) {
        log.debug("REST request to get a page of Carros");
        Page<Carro> page = carroService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /carros/:id} : get the "id" carro.
     *
     * @param id the id of the carro to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the carro, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/carros/{id}")
    public ResponseEntity<Carro> getCarro(@PathVariable Long id) {
        log.debug("REST request to get Carro : {}", id);
        Optional<Carro> carro = carroService.findOne(id);
        return ResponseUtil.wrapOrNotFound(carro);
    }

    /**
     * {@code DELETE  /carros/:id} : delete the "id" carro.
     *
     * @param id the id of the carro to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/carros/{id}")
    public ResponseEntity<Void> deleteCarro(@PathVariable Long id) {
        log.debug("REST request to delete Carro : {}", id);
        carroService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/carros?query=:query} : search for the carro corresponding
     * to the query.
     *
     * @param query the query of the carro search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/carros")
    public ResponseEntity<List<Carro>> searchCarros(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Carros for query {}", query);
        Page<Carro> page = carroService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

}
