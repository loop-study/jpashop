package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

//내장된다는 뜻
@Embeddable
@Getter
public class Address {

    private String city;
    private String street;
    private String zipcode;
}
