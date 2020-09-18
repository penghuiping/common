package com.php25.common.coresample;

import com.google.common.collect.Lists;
import com.php25.common.core.util.SortUtil;
import com.php25.common.coresample.dto.Person;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: penghuiping
 * @Date: 2018/6/1 09:27
 * @Description:
 */
public class SortUtilTest {

    private Person[] persons;

    private static final Logger log = LoggerFactory.getLogger(SortUtilTest.class);

    @Before
    public void before() {
        Person person1 = new Person(1, "person1");
        Person person2 = new Person(2, "person2");
        Person person3 = new Person(3, "person3");
        Person person4 = new Person(4, "person4");
        Person person5 = new Person(5, "person5");
        Person person6 = new Person(6, "person6");
        persons = new Person[]{person2, person1, person5, person3, person6, person4};
    }

    @Test
    public void quickSort() {
        SortUtil.quickSort(persons, 0, persons.length);
        log.info("排序后persons:{}", Lists.newArrayList(persons));
    }

    @Test
    public void mergeSort() {
        Person[] tmp = new Person[persons.length];
        SortUtil.mergeSort(persons, 0, persons.length, tmp);
        log.info("排序后persons:{}", Lists.newArrayList(tmp));
    }

    @Test
    public void heapSort() {
        SortUtil.heapSort(persons, true);
        log.info("排序后persons:{}", Lists.newArrayList(persons));
    }


}
