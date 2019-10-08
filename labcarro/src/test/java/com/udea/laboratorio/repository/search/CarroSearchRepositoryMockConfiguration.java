package com.udea.laboratorio.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link CarroSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class CarroSearchRepositoryMockConfiguration {

    @MockBean
    private CarroSearchRepository mockCarroSearchRepository;

}
