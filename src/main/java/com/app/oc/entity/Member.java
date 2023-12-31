package com.app.oc.entity;

import com.app.oc.dto.mypage.MemberDto;
import com.app.oc.dto.mypage.MemberRequestDto;
import com.app.oc.dto.mypage.RequestMemberDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 회원 엔티티
 */
@Entity
@Table(name = "member")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 무분별한 객체 생성대해 한번 더 체크
public class Member {

    @Id
    @Column(name = "memberID")
    private String memberId;
    private String nickname;
    private String password;
    private String phoneNm;

    // 주소
    @Embedded
    private Address address;

    @OneToMany(mappedBy = "shopId")
    private List<ShoppingMal> shoppingMals = new ArrayList<>();

    private String gender;
    private Integer length;
    private Integer weight;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @OneToMany(mappedBy = "attens_seq")
    private List<AttenShop> aiShop = new ArrayList<>();

    @Builder
    public Member(MemberDto member) {
        this.nickname = member.getNickname();
        this.password = member.getPassword();
        this.phoneNm = member.getPhoneNm();
        this.address = member.getAddress();
        this.gender = member.getGender();
        this.length = member.getLength();
        this.weight = member.getWeight();
        this.role = member.getRole();
    }

    @Builder
    public Member(MemberRequestDto member) {
        this.memberId = member.getMemberId();
        this.nickname = member.getNickname();
        this.password = member.getPassword();
        this.phoneNm = member.getPhoneNm();
        this.address = member.getAddress();
        this.gender = member.getGender();
        this.length = member.getLength();
        this.weight = member.getWeight();
        this.role = MemberRole.BUYER;
    }

    /**
     * member수정
     *
     * @param member
     * @param address
     */
    public Member update(RequestMemberDto member, Address address) {
        this.nickname = member.getNickname();
        this.phoneNm = member.getPhoneNm();
        this.address = address;
        this.gender = member.getGender();
        this.length = member.getLength();
        this.weight = member.getWeight();
        return this;
    }

    /**
     * 비밀번호 수정
     * 
     * @param npwd
     */
    public void updatePwd(String npwd) {
        this.password = npwd;
    }

}