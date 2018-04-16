package com.php25.common.repository.impl;

import com.php25.common.repository.BaseRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;

/**
 * Created by penghuiping on 16/4/4.
 */
@NoRepositoryBean
public class BaseRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseRepository<T, ID> {

    private EntityManager entityManager;

    public BaseRepositoryImpl(JpaEntityInformation entityInformation, EntityManager em) {
        super(entityInformation, em);
        this.entityManager = em;
    }

    public List<T> findAllEnabled() {
        List<T> result = entityManager.createQuery("from " + getDomainClass().getName() + " where enable=1").getResultList();
        return result;
    }
}
