package com.php25.common.db.core.sql.fragment;

/**
 * @author penghuiping
 * @date 2022/1/1 21:12
 */
public abstract class Fragments {

    public static DefaultFragment from(Class<?> entity) {
        return new DefaultFragment().from(entity);
    }

    public static DefaultFragment from(Class<?> entity, String alias) {
        return new DefaultFragment().from(entity, alias);
    }
}
