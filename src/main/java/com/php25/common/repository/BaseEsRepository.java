package com.php25.common.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * Created by penghuiping on 16/10/13.
 */
@NoRepositoryBean
public interface BaseEsRepository<T, ID extends Serializable> extends ElasticsearchRepository<T, ID> {
}
