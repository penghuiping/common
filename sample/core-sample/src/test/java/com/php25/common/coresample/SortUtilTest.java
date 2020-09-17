package com.php25.common.coresample;

import com.google.common.collect.Lists;
import com.php25.common.core.util.SortUtil;
import org.junit.Test;

/**
 * @Auther: penghuiping
 * @Date: 2018/6/1 09:27
 * @Description:
 */
public class SortUtilTest {

    @Test
    public void sort() throws Exception {
        Long[] arr = {2L, 1110L, 7L, 8L, 3L, 31L, 9L, 23L, 30L};
        Long[] arrSort = new Long[arr.length];
        SortUtil.quickSort(arr, 0, arr.length);
        System.out.println(Lists.newArrayList(arr));
        SortUtil.mergeSort(arr, 0, arr.length, arrSort);
        System.out.println(Lists.newArrayList(arr));
        SortUtil.heapSort(arr, true);
        System.out.println(Lists.newArrayList(arr));
    }


}
