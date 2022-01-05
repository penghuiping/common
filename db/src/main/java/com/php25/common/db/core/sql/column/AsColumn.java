package com.php25.common.db.core.sql.column;

import com.php25.common.core.util.StringUtil;

/**
 * @author penghuiping
 * @date 2021/12/28 17:41
 */
public class AsColumn {
    private Column column;
    private String alias;

    private AsColumn() {

    }

    static AsColumn of(Column column, String alias) {
        AsColumn asColumn = new AsColumn();
        asColumn.column = column;
        asColumn.alias = alias;
        return asColumn;
    }

    static AsColumn of(Column column) {
        AsColumn asColumn = new AsColumn();
        asColumn.column = column;
        return asColumn;
    }

    @Override
    public String toString() {
        if (StringUtil.isNotBlank(alias)) {
            return String.format("%s as %s", column.toString(), alias);
        } else {
            return column.toString();
        }
    }
}
