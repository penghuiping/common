package com.php25.common.db.sql.fragment;

/**
 * @author penghuiping
 * @date 2022/1/1 21:12
 */
public abstract class Fragments {

    public static DefaultFragment from(Class<?> entity) {
        return Fragments.from(entity, null);
    }

    public static DefaultFragment from(Class<?> entity, String alias) {
        return new DefaultFragment().from(entity, alias);
    }
}
