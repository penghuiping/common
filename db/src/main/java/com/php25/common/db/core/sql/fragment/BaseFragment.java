package com.php25.common.db.core.sql.fragment;

import java.util.List;

/**
 * @author penghuiping
 * @date 2022/1/9 21:43
 */
public abstract class BaseFragment implements Fragment {

    @Override
    public List<Object> params() {
        return null;
    }
}
