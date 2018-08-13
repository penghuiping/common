package com.php25.common.sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author GavinKing
 */
public class Db {

    private Logger log = LoggerFactory.getLogger(Db.class);

    public Db() {

    }

    /**
     * 获取一个新条件
     *
     * @return
     */
    public Cnd cnd(Class cls) {
        return Cnd.of(cls);
    }


}