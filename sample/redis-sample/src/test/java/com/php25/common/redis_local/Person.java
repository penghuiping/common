package com.php25.common.redis_local;

import com.google.common.base.Objects;

/**
 * @author penghuiping
 * @date 2021/3/2 20:16
 */
public class Person {

    private Integer age;

    private String name;

    public Person() {
    }

    public Person(Integer age, String name) {
        this.age = age;
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equal(age, person.age) && Objects.equal(name, person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(age, name);
    }
}
