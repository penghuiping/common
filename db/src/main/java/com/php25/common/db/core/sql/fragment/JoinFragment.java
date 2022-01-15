package com.php25.common.db.core.sql.fragment;

import com.php25.common.core.util.StringUtil;
import com.php25.common.db.core.sql.DbConstant;
import com.php25.common.db.mapper.JdbcModelCacheManager;
import com.php25.common.db.mapper.ModelMeta;

/**
 * @author penghuiping
 * @date 2022/1/1 21:31
 */
public class JoinFragment extends BaseFragment {
    private final Class<?> entity;
    private final String alias;

    public JoinFragment(Class<?> entity, String alias) {
        this.entity = entity;
        this.alias = alias;
    }

    @Override
    public String printSql() {
        ModelMeta modelMeta = JdbcModelCacheManager.getModelMeta(entity);
        if (StringUtil.isNotBlank(alias)) {
            return String.format("%s %s %s", DbConstant.JOIN, modelMeta.getLogicalTableName(), alias);
        } else {
            return String.format("%s %s", DbConstant.JOIN, modelMeta.getLogicalTableName());
        }
    }
}


