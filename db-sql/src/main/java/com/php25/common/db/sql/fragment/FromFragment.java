package com.php25.common.db.sql.fragment;

import com.php25.common.core.util.StringUtil;
import com.php25.common.db.mapper.JdbcModelCacheManager;
import com.php25.common.db.mapper.ModelMeta;
import com.php25.common.db.sql.constant.DbConstant;

/**
 * @author penghuiping
 * @date 2022/1/1 21:15
 */
public class FromFragment extends BaseFragment {
    private final Class<?> entity;
    private final String alias;

    public FromFragment(Class<?> entity, String alias) {
        this.entity = entity;
        this.alias = alias;
    }

    @Override
    public String printSql() {
        ModelMeta modelMeta = JdbcModelCacheManager.getModelMeta(entity);
        if (StringUtil.isNotBlank(alias)) {
            return String.format("%s %s %s", DbConstant.FROM, modelMeta.getLogicalTableName(), alias);
        } else {
            return String.format("%s %s", DbConstant.FROM, modelMeta.getLogicalTableName());
        }
    }
}
