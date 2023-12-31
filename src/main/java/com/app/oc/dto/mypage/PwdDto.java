package com.app.oc.dto.mypage;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PwdDto {
    @NotNull(message = "기존 비밀번호를 입력해주세요")
    private String pwd;
    
    @NotNull(message = "새 비밀번호를 입력해주세요")
    private String n_pwd;
}

