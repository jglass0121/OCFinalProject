package com.app.oc.dto.mypage;

import com.app.oc.entity.Address;
import com.app.oc.entity.Member;
import com.app.oc.entity.MemberRole;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Data
@NoArgsConstructor
public class MemberRequestDto {

    private String memberId;
    private String nickname;
    @NotNull(message = "비밀번호를 입력해주세요")
    private String password;

    @NotNull(message = "비밀번호를 입력해주세요")
    private String phoneNm;

    private Address address;
    private String gender;
    private Integer length;
    private Integer weight;
    private MemberRole role;

    @Builder
    public MemberRequestDto(Member member) {
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

}
