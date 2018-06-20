package com.php25.common;

import com.google.common.collect.Lists;
import com.php25.common.util.SortUtil;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: penghuiping
 * @Date: 2018/6/1 09:27
 * @Description:
 */
public class SortUtilTest {


    @Test
    public void test() {
        List<Integer> lists = Lists.newArrayList(1, null, 2).stream().filter(a -> null != a).collect(Collectors.toList());
        System.out.println(lists.toString());
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
