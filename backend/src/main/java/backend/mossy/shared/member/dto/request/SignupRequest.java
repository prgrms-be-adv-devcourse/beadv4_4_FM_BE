package backend.mossy.shared.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SignupRequest(
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotBlank String name,
        @NotBlank String nickname,
        @NotBlank String phoneNum,
        @NotBlank String address,
        @NotBlank String rrn, //주민번호 (암호화전)
        @NotNull BigDecimal longitude,
        @NotNull BigDecimal latitude
) {
}
