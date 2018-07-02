package com.php25.common;

import com.php25.common.collection.BinaryTreeMap;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: penghuiping
 * @Date: 2018/6/25 15:51
 * @Description:
 */
public class CollectionTest {

    private static final Logger logger = LoggerFactory.getLogger(CollectionTest.class);


    @Test
    public void test() {
        BinaryTreeMap<String, String> treeMap = new BinaryTreeMap<>();

        for (int i = 0; i < 10000; i++) {
            treeMap.put("key" + i, "hello" + i);
        }

        logger.info(treeMap.toString());

        logger.info(treeMap.get("key15"));
        logger.info(treeMap.get("key16"));
        logger.info(treeMap.get("key12"));
    }
}
