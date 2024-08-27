package org.swdc.mariadb.test;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "entuser")
public class EntUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer age;

    private Double source;

    private Float nextAim;

    private LocalDate createdOn;

    public Float getNextAim() {
        return nextAim;
    }

    public void setNextAim(Float nextAim) {
        this.nextAim = nextAim;
    }

    public Double getSource() {
        return source;
    }

    public void setSource(Double source) {
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public Integer getAge() {
        return age;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public LocalDate getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDate createdOn) {
        this.createdOn = createdOn;
    }

    @Override
    public String toString() {
        return "EntUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", source=" + source +
                ", nextAim=" + nextAim +
                ", createdOn=" + createdOn +
                '}';
    }
}
