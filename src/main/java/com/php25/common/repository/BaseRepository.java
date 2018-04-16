package com.php25.common.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;
import java.util.List;

/**
 * Created by penghuiping on 2016/4/3.
 */
@NoRepositoryBean
public interface BaseRepository<T,ID extends Serializable> extends PagingAndSortingRepository<T, ID>, JpaSpecificationExecutor<T> {

    public List<T> findAllEnabled();

}
