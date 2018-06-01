package com.php25.common;

import com.google.common.collect.Lists;
import com.php25.common.util.DigestUtil;
import com.php25.common.util.SortUtil;
import org.junit.Test;

/**
 * @Auther: penghuiping
 * @Date: 2018/6/1 09:27
 * @Description:
 */
public class UtilTest {


    @Test
    public void test() {

    }

    @Test
    public void sha1() throws Exception {
        byte[] arr = DigestUtil.SHA("hello world");
        System.out.println(new String(DigestUtil.bytes2hex(arr)));
    }

    @Test
    public void sort() throws Exception {
        Long[] arr = {2l, 1110l, 7l, 8l, 3l, 31l, 9l, 23l, 30l};
        Long[] arrSort = new Long[arr.length];
        SortUtil.quickSort(arr, 0, arr.length);
        System.out.println(Lists.newArrayList(arr));
        SortUtil.mergeSort(arr, 0, arr.length, arrSort);
        System.out.println(Lists.newArrayList(arr));
        SortUtil.heapSort(arr, true);
        System.out.println(Lists.newArrayList(arr));
    }
}
