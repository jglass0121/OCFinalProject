package com.app.oc.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 쇼핑몰 엔티티
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingMal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_seq")
    private Long shopId;
    private String shopLogo;
    private String style;
    private String content;
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;
    private String shopName;

    @Embedded
    private Address address;
    private String shopTel;

    @OneToMany(mappedBy = "itemId")
    private List<Item> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Approval approval;

    @Builder
    public ShoppingMal(Member member, String email, String shopName, String shopTel, Address address,
            Approval approval) {
        this.member = member;
        this.email = email;
        this.shopName = shopName;
        this.shopTel = shopTel;
        this.address = address;
        this.approval = approval;
    }

    public void addShop(Member member) {
        this.member = member;
        member.getShoppingMals().add(this);
    }
}
