package dev.snowdrop.vertx.postgres.it;

import org.springframework.data.annotation.Id;

public class Person {

    @Id
    private String id;

    private String name;

    private int age;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}
