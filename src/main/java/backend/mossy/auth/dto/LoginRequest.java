package backend.mossy.auth.dto;

public record LoginRequest(
        String email, //나중에 이메일로 바꿀예정, 체크용 로그인
        String password
) { }
