package com.app.oc.dto;

import com.app.oc.entity.*;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MyPageShoppingMal {
    private Long shopId;
    private String style;
    private String email;
    private String shopName;
    private Address address;
    private String shopTel;
    private Approval approval;

    public MyPageShoppingMal(ShoppingMal myShop) {
        shopId = myShop.getShopId();
        style = myShop.getStyle();
        email = myShop.getEmail();
        shopName = myShop.getShopName();
        address = myShop.getAddress();
        shopTel = myShop.getShopTel();
        approval = myShop.getApproval();
    }
}