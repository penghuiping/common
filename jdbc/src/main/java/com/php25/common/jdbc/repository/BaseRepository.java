package com.php25.common.jdbc.repository;

import com.php25.common.core.specification.SearchParamBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Created by penghuiping on 2016/4/3.
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends PagingAndSortingRepository<T, ID> {

    public List<T> findAllEnabled();

    Optional<T> findOne(@Nullable SearchParamBuilder var1);

    List<T> findAll(@Nullable SearchParamBuilder var1);

    Page<T> findAll(@Nullable SearchParamBuilder var1, Pageable var2);

    List<T> findAll(@Nullable SearchParamBuilder var1, Sort var2);

    long count(@Nullable SearchParamBuilder var1);

}
