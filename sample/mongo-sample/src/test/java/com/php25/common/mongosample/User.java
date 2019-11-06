package com.php25.common.mongosample;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

/**
 * @author penghuiping
 * @date 2019/11/6 11:11
 */
public class User {

    @Id
    private ObjectId id;

    private String name;

    private Integer age;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getAge() {
        return age;
    }
}
