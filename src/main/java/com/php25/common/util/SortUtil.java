package com.php25.common.util;

/**
 * @Auther: penghuiping
 * @Date: 2018/5/31 15:37
 * @Description:
 */
public class SortUtil {
    /**
     * 1. 选取数组最左边的一个数做为基准
     * 2. 初始化两个指针，分别指向数组的头(最左边)与尾(最右边)
     * 3. 先从尾指针向头指针的方向移动,当遇到比基准数小的情况停下来,然后把头指针指向的位置赋值为尾指针指向的数字
     * 4. 头指针开始向尾指针的方向移动，当遇到比基准数大的情况停下来,然后把尾指针指向的位置赋值为头指针指向的数字
     * 5. 如此反复，直到头指针与尾指针相遇,把头指针指向的位置赋值基准数字。
     * 6. 这时候基准数字右边都比基准数字大，左边的都比基准数字小
     * 7. 分别在递归以相同的步骤处理左边与右边的数字。排序完成。
     *
     * @param arr
     * @param start
     * @param end
     */
    public static <T extends Comparable> void quickSort(T arr[], int start, int end) {
        if (end <= start) return;
        T base = arr[start];
        int i = start, j = end;//初始化两个指针，分别指向数组的头与尾

        while (true) {
            while (j != i) {
                T right = arr[--j];//找到小于base的数
                if (right.compareTo(base) < 0) {
                    arr[i] = right;//当遇到比基准数小的情况停下来,然后把头指针指向的位置赋值为尾指针指向的数字
                    break;
                }
            }
            while (i != j) {
                T left = arr[++i];//找到大于base的数
                if (left.compareTo(base) > 0) {
                    arr[j] = left;//当遇到比基准数大的情况停下来,然后把尾指针指向的位置赋值为头指针指向的数字
                    break;
                }
            }

            if (i == j) {
                arr[i] = base;//如此反复，直到头指针与尾指针相遇,把头指针指向的位置赋值基准数字。
                break;
            }
        }
        quickSort(arr, start, i);//分别在递归以相同的步骤处理左边与右边的数字。排序完成。
        quickSort(arr, i + 1, end);//分别在递归以相同的步骤处理左边与右边的数字。排序完成。
    }


    /**
     * 归并排序实现
     *
     * @param arr  需要排序的数组
     * @param temp 临时数组,存放归并排序中的临时数据，避免不停的创建数组
     */
    public static <T extends Comparable> void mergeSort(T arr[], int start, int end, T temp[]) {
        if (end - start <= 1) {
            //一直以一分为二的方式分裂，直到数组中只有一个元素时候，天然有就序了，所以无需在进行下面的排序操作
            return;
        }
        int mid = start + (end - start) / 2;
        mergeSort(arr, start, mid, temp);    //左边排序
        mergeSort(arr, mid, end, temp); //右边排序
        mergeArray(arr, start, mid, end, temp); //再将二个有序数列合并
    }


    /**
     * 归并算法,把两个有序数组srcArr0,数组srcArr1,合并到数组distArr中
     *
     * @param srcArr0
     * @param srcArr1
     * @param distArr
     */
    public static <T extends Comparable> void mergeArray(T[] srcArr0, T[] srcArr1, T[] distArr) {
        int i = 0, j = 0, z = 0;
        while (i < srcArr0.length && j < srcArr1.length) {
            //两个数组必然一长一短,比如一个数组长度7，一个数组长度5，那么归并阶段在5(包括5)以下处理情况
            T tmp0 = srcArr0[i];
            T tmp1 = srcArr1[j];

            if (tmp0.compareTo(tmp1) < 0) {
                distArr[z++] = tmp0;
                i++;
            } else {
                distArr[z++] = tmp1;
                j++;
            }
        }

        //超出了5处理情况
        while (i < srcArr0.length) distArr[z++] = srcArr0[i++];

        while (j < srcArr1.length) distArr[z++] = srcArr1[j++];

    }

    /**
     * 归并算法,把一个数组，逻辑上一分为二,并且让这两个子数组都是有序数组。(注意:物理结构上依旧是一个数组)
     *
     * @param srcArr
     * @param start
     * @param mid
     * @param end
     * @param distArr
     * @param <T>
     */
    private static <T extends Comparable> void mergeArray(T[] srcArr, int start, int mid, int end, T[] distArr) {
        int i = start, j = mid, z = 0;
        while (i < mid && j < end) {
            //两个数组必然一长一短,比如一个数组长度7，一个数组长度5，那么归并阶段在5(包括5)以下处理情况
            T tmp0 = srcArr[i];
            T tmp1 = srcArr[j];

            if (tmp0.compareTo(tmp1) < 0) {
                distArr[z++] = tmp0;
                i++;
            } else {
                distArr[z++] = tmp1;
                j++;
            }
        }

        //超出了5处理情况
        while (i < mid) distArr[z++] = srcArr[i++];

        while (j < end) distArr[z++] = srcArr[j++];

        //将distArr中的元素全部拷贝到原数组中
        z = 0;
        while (start < end) {
            srcArr[start++] = distArr[z++];
        }
    }

    /**
     * 堆排序，元素下沉算法
     *
     * @param arr
     * @param elementIndex 元素的数组下标
     * @param length       元素的数组长度
     * @param max          true:最大堆，false最小堆
     * @param <T>
     */
    private static <T extends Comparable> void heapElementDown(T[] arr, int length, int elementIndex, boolean max) {
        //求出element元素的两个子节点
        int childLeft = 2 * elementIndex + 1;
        int childRight = childLeft + 1;
        int swapIndex = -1;
        //判断左右子节点那个大
        if (max) {
            //最大堆的情况
            if (childRight >= length) {
                //完全二叉树，只有左节点的情况
                if (arr[elementIndex].compareTo(arr[childLeft]) < 0) {
                    swapIndex = childLeft;
                }
            } else {
                if (arr[elementIndex].compareTo(arr[childLeft]) < 0 && arr[elementIndex].compareTo(arr[childRight]) < 0) {
                    //左右子节点都大于本节点，与两个子节点较大的子节点交换
                    if (arr[childLeft].compareTo(arr[childRight]) > 0) {
                        //左子节点较大
                        swapIndex = childLeft;
                    } else {
                        //右子节点较大
                        swapIndex = childRight;
                    }
                } else if (arr[elementIndex].compareTo(arr[childLeft]) < 0 && arr[elementIndex].compareTo(arr[childRight]) > 0) {
                    //左子节点大于本节点,右子节点小于本节点，则交换左子节点
                    swapIndex = childLeft;
                } else if (arr[elementIndex].compareTo(arr[childLeft]) > 0 && arr[elementIndex].compareTo(arr[childRight]) < 0) {
                    //左子节点小于本节点,右子节点大于本节点，则交换右子节点
                    swapIndex = childRight;
                }
            }


        } else {
            //最小堆的情况
            if (childRight >= length) {
                //完全二叉树，只有左节点的情况
                if (arr[elementIndex].compareTo(arr[childLeft]) > 0) {
                    swapIndex = childLeft;
                }
            } else {
                if (arr[elementIndex].compareTo(arr[childLeft]) > 0 && arr[elementIndex].compareTo(arr[childRight]) > 0) {
                    //左右子节点都小于本节点，与两个子节点较小的子节点交换
                    if (arr[childLeft].compareTo(arr[childRight]) < 0) {
                        //左子节点较小
                        swapIndex = childLeft;
                    } else {
                        //右子节点较小
                        swapIndex = childRight;
                    }
                } else if (arr[elementIndex].compareTo(arr[childLeft]) < 0 && arr[elementIndex].compareTo(arr[childRight]) > 0) {
                    //左子节点大于本节点,右子节点小于本节点，则交换右子节点
                    swapIndex = childRight;
                } else if (arr[elementIndex].compareTo(arr[childLeft]) > 0 && arr[elementIndex].compareTo(arr[childRight]) < 0) {
                    //左子节点小于本节点,右子节点大于本节点，则交换左子节点
                    swapIndex = childLeft;
                }
            }
        }

        if (swapIndex > 0) {
            swap(arr, elementIndex, swapIndex);
            if (swapIndex < length / 2) {
                heapElementDown(arr, length, swapIndex, max);
            }
        }
    }


    /**
     * 堆排序
     *
     * @param arr
     * @param max true:最大堆;false:最小堆
     * @param <T>
     */
    public static <T extends Comparable> void heapSort(T[] arr, boolean max) {
        heapCreation(arr, max);
        //只处理到只剩最后两个元素
        for (int i = arr.length - 1; i > 1; i--) {
            swap(arr, 0, i);
            //此时堆顶元素移动到了数组的最后一个位置，并且将固定不动，所以数组大小会-1
            heapElementDown(arr, i, 0, max);
        }
        //最后一个元素没有处理，只需要进行交换操作，无需在进行元素下沉，因为后面的元素都是有序的。
        swap(arr, 0, 1);
    }


    /**
     * 把一个无序数组构建成一个最小/最大堆
     *
     * @param arr
     * @param max true:最大堆;false:最小堆
     * @param <T>
     */
    public static <T extends Comparable> void heapCreation(T[] arr, boolean max) {
        int i = 0;
        for (i = arr.length / 2 - 1; i >= 0; i--)//((n-1)*2)+1 =n/2-1:0~(n/2-1)为非叶子节点
        {
            heapElementDown(arr, arr.length, i, max);
        }
    }

    /**
     * 交换数组指定下标的两个值
     *
     * @param arr
     * @param a
     * @param b
     * @param <T>
     */
    private static <T extends Comparable> void swap(T[] arr, int a, int b) {
        T tmp = arr[a];
        arr[a] = arr[b];
        arr[b] = tmp;
    }
}
