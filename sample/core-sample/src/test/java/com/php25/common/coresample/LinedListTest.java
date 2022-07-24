package com.php25.common.coresample;

import com.php25.common.core.mess.list.LinkedList;
import org.junit.Test;

/**
 * @author penghuiping
 * @date 2022/7/23 10:21
 */
public class LinedListTest {

    @Test
    public void test() {
        LinkedList<Long> list = new LinkedList<>();
        list.add(list.size(), 3L);
        list.add(list.size(), 5L);
        System.out.println(list);
        list.reverseBetween(0, 1);
        System.out.println(list);
    }
}
