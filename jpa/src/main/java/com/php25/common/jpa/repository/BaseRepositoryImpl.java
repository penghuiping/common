package com.php25.common.jpa.repository;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;

/**
 * database Repository层的基础实现类
 *
 * @author penghuiping
 * @date 2016-04-04
 *
 */
@NoRepositoryBean
public class BaseRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseRepository<T, ID> {

    private EntityManager entityManager;

    public BaseRepositoryImpl(JpaEntityInformation entityInformation, EntityManager em) {
        super(entityInformation, em);
        this.entityManager = em;
    }

    @Override
    public List<T> findAllEnabled() {
        List<T> result = entityManager.createQuery("from " + getDomainClass().getName() + " where enable=1").getResultList();
        return result;
    }
}