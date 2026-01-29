package backend.mossy.boundedContext.member.in;

import backend.mossy.boundedContext.auth.in.dto.UserInfoDTO;
import backend.mossy.boundedContext.auth.infra.security.UserDetailsImpl;
import backend.mossy.boundedContext.member.out.seller.SellerRequestRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.global.rsData.RsData;
import backend.mossy.shared.member.domain.seller.SellerRequest;
import backend.mossy.shared.member.domain.seller.SellerRequestStatus;
import backend.mossy.shared.member.domain.seller.SellerStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Tag(name = "User", description = "로그인 사용자 정보 조회 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final SellerRequestRepository sellerRequestRepository;

    @Operation(
            summary = "내 정보 조회",
            description = "현재 로그인된 사용자의 기본 식별 정보(userId)를 조회"
    )
    @GetMapping("/me")
    public RsData<UserInfoDTO> me(@AuthenticationPrincipal UserDetailsImpl principal) {

        Long userId = principal.getUserId();

        Optional<SellerRequest> latestRequest =
                sellerRequestRepository.findTopByUserIdOrderByCreatedAtDesc(userId);

        SellerRequestStatus status = latestRequest
                .map(SellerRequest::getStatus)
                .orElse(null);

        UserInfoDTO dto = new UserInfoDTO(
                userId,
                principal.getUser().getNickname(),
                principal.getUser().getName(),
                status
        );

        return RsData.success("내 정보 조회 성공", dto);

    }
}
