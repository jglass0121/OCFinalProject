package com.app.oc.dto.mypage;

import com.app.oc.entity.Address;
import com.app.oc.entity.Member;
import com.app.oc.entity.MemberRole;
import com.app.oc.dto.MyPageShoppingMal;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MyPageResponse {
    private String memberId;
    private String nickname;
    private String password;
    private String phoneNm;
    private Address address;
    private String gender;
    private Integer length;
    private Integer weight;
    private MemberRole role;
    private List<MyPageShoppingMal> myPageShoppingMal;

    public MyPageResponse(Member member) {
        memberId = member.getMemberId();
        nickname = member.getNickname();
        password = member.getPassword();
        phoneNm = member.getPhoneNm();
        address = member.getAddress();
        gender = member.getGender();
        length = member.getLength();
        weight = member.getWeight();
        role = member.getRole();
    }
}