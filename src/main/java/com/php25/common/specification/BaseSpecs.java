package com.php25.common.specification;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.php25.common.util.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by penghuiping on 16/4/12.
 */
public class BaseSpecs {
    protected static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> Specification<T> getSpecs(final String json) {
        if (StringUtil.isBlank(json)) return null;
        try {
            final List<SearchParam> searchParams = objectMapper.readValue(json, new TypeReference<List<SearchParam>>() {
            });

            return new Specification<T>() {
                @Override
                public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                    Predicate p = null;
                    for (SearchParam s : searchParams) {
                        try {
                            if (null == p)
                                p = s.toPredicate(root, criteriaQuery, criteriaBuilder);
                            else
                                p = criteriaBuilder.and(p, s.toPredicate(root, criteriaQuery, criteriaBuilder));
                        } catch (Exception e) {
                            Logger.getLogger(BaseSpecs.class).error(e);
                        }
                    }
                    return p;
                }
            };

        } catch (IOException e) {
            Logger.getLogger(BaseSpecs.class).error(e);
            return null;
        }
    }

    public static <T> Specification<T> getSpecs(final String json, final MoreSpecification<T> moreSpecification) {
        final List<SearchParam> searchParams;
        if (StringUtil.isBlank(json)) {
            searchParams = new ArrayList<SearchParam>();
        } else {
            try {
                searchParams = objectMapper.readValue(json, new TypeReference<List<SearchParam>>() {
                });
            } catch (IOException e) {
                Logger.getLogger(BaseSpecs.class).error(e);
                return null;
            }
        }
        ;

        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate p = null;
                for (SearchParam s : searchParams) {
                    try {
                        if (null == p)
                            p = s.toPredicate(root, criteriaQuery, criteriaBuilder);
                        else
                            p = criteriaBuilder.and(p, s.toPredicate(root, criteriaQuery, criteriaBuilder));
                    } catch (Exception e) {
                        Logger.getLogger(BaseSpecs.class).error(e);
                    }
                }

                if (null == p) p = criteriaBuilder.conjunction();

                p = criteriaBuilder.and(p, moreSpecification.toPredicate(root, criteriaQuery, criteriaBuilder));
                return p;
            }
        };


    }

    public interface MoreSpecification<T> {
        Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder);
    }
}
