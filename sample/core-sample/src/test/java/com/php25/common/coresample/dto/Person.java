package com.php25.common.coresample.dto;

import org.jetbrains.annotations.NotNull;

/**
 * @author penghuiping
 * @date 2020/9/17 17:25
 */
public class Person implements Comparable<Person> {

    private Integer id;
    private String name;

    public Person(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Person() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(@NotNull Person o) {
        return id - o.getId();
    }

    @Override
    public String toString() {
        return this.getId().toString();
    }
}
