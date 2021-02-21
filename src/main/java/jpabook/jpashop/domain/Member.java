package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded
    private Address address;

    //mappedBy order의 fk 지정해줌.
    // @JsonIgnore // 외부 노출 안 시켜줌, json 반환 안함, ***최악의 방법!!! 해당 엔티티를 사용하는 모든 곳에서 조회 못함;;;;
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();
}
