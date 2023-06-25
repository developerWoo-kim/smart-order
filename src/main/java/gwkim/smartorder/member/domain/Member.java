package gwkim.smartorder.member.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gwkim.smartorder.order.domain.Cart;
import gwkim.smartorder.order.domain.Order;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


import static javax.persistence.GenerationType.*;

@Entity
@Getter
@NoArgsConstructor
public class Member {
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;     // 회원 아이디

    private String name; // 회원 이름

    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private List<Order> orderList = new ArrayList<>();

    @JsonIgnore
    @OneToOne(mappedBy = "member")
    private Cart cart;

    @Builder
    public Member(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    //== 연관 관계 메서드 ==//
    public void addCart(Cart cart) {
        this.cart = cart;
    }
}
