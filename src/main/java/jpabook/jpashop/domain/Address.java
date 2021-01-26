package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable //내장된다는 뜻
@Getter
public class Address {

    private String city;
    private String street;
    private String zipcode;

    //생성자를 protected 로 해준다. jpa 특성이다. public 도 가능하지만 더 안전하게 protected 로 간다
    protected Address() {
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
