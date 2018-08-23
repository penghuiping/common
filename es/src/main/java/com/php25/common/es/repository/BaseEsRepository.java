package com.php25.common.es.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * elasticsearch Repository层的基础接口
 *
 * @author penghuiping
 * @date 2016-10-13
 *
 */
@NoRepositoryBean
public interface BaseEsRepository<T, ID extends Serializable> extends ElasticsearchRepository<T, ID> {
}
