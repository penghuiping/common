package com.php25.common.db.core.sql.fragment;

import com.php25.common.core.util.StringUtil;
import com.php25.common.db.core.manager.JdbcModelManager;
import com.php25.common.db.core.manager.ModelMeta;
import com.php25.common.db.core.sql.DbConstant;

/**
 * @author penghuiping
 * @date 2022/1/1 21:15
 */
public class FromFragment implements Fragment {
    private final Class<?> entity;
    private final String alias;

    public FromFragment(Class<?> entity, String alias) {
        this.entity = entity;
        this.alias = alias;
    }

    @Override
    public String toString() {
        ModelMeta modelMeta = JdbcModelManager.getModelMeta(entity);
        if (StringUtil.isNotBlank(alias)) {
            return String.format("%s %s %s", DbConstant.FROM, modelMeta.getLogicalTableName(), alias);
        } else {
            return String.format("%s %s", DbConstant.FROM, modelMeta.getLogicalTableName());
        }
    }
}
