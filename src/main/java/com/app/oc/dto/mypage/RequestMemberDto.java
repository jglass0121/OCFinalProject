package com.app.oc.dto.mypage;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * Member회원 수정
 */

@Data
public class RequestMemberDto {

    private String memberId;
    
    @NotEmpty(message = "닉네임을 입력해주세요")
    private String nickname;
    private String phoneNm;

    private String postcode;
    private String address1;
    private String address2;

    private String gender;
    private Integer length;
    private Integer weight;
}
