package backend.mossy.shared.market.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class MarketMemberDto {
    private final int id;
    private final LocalDateTime createDate;
    private final LocalDateTime modifyDate;
    private final String username;
    private final String nickname;
    private final int activityScore;
}