package backend.mossy;

import backend.mossy.boundedContext.market.domain.Cart;
import backend.mossy.boundedContext.market.domain.MarketUser;
import backend.mossy.boundedContext.market.in.MarketEventListener;
import backend.mossy.boundedContext.market.out.CartRepository;
import backend.mossy.boundedContext.market.out.MarketUserRepository;
import backend.mossy.shared.market.dto.MarketUserDto;
import backend.mossy.shared.market.event.MarketUserCreatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
class MarketEventListenerTest {
    @Autowired
    private MarketEventListener listener;
    @Autowired
    private MarketUserRepository marketUserRepository;
    @Autowired
    private CartRepository cartRepository;

    @Test
    void testHandleMarketUserCreatedEvent_dbCheck() {
        // given
        MarketUserDto marketUserDto = MarketUserDto.builder()
                .id(1L)
                .email("test@test.com")
                .name("테스트")
                .build();

        MarketUser user = MarketUser.builder()
                .id(1L)
                .email("test@test.com")
                .name("테스트")
                .build();

        marketUserRepository.save(user);

        MarketUserCreatedEvent event = new MarketUserCreatedEvent(marketUserDto);

        // when
        listener.handle(event);

        // then
        List<Cart> carts = cartRepository.findAll();
    }
}