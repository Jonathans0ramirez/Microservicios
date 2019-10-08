package com.udea.laboratorio.repository.search;
import com.udea.laboratorio.domain.Carro;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Carro} entity.
 */
public interface CarroSearchRepository extends ElasticsearchRepository<Carro, Long> {
}
