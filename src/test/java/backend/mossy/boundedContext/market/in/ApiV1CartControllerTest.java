package backend.mossy.boundedContext.market.in;

import backend.mossy.boundedContext.market.domain.Cart;
import backend.mossy.boundedContext.market.domain.CartItem;
import backend.mossy.boundedContext.market.domain.MarketUser;
import backend.mossy.boundedContext.market.domain.Product;
import backend.mossy.boundedContext.market.out.CartRepository;
import backend.mossy.boundedContext.market.out.MarketUserRepository;
import backend.mossy.boundedContext.market.out.ProductRepository;
import backend.mossy.shared.member.domain.user.UserStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ApiV1CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MarketUserRepository marketUserRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    private MarketUser testUser;
    private Cart testCart;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testUser = MarketUser.builder()
                .id(999L)
                .email("carttest@test.com")
                .name("장바구니테스트")
                .address("서울시 강남구")
                .phoneNum("010-1234-5678")
                .nickname("테스터")
                .password("test1234")
                .rrnEncrypted("encrypted-rrn-999")
                .profileImage("http://example.com/profile.jpg")
                .status(UserStatus.ACTIVE)
                .build();
        marketUserRepository.save(testUser);

        testCart = Cart.createCart(testUser);
        cartRepository.save(testCart);

        testProduct = Product.builder()
                .userId(testUser.getId())
                .categoryId(1L)
                .name("테스트 상품")
                .description("테스트 상품 설명")
                .weight(BigDecimal.valueOf(1.0))
                .price(BigDecimal.valueOf(10000))
                .quantity(100)
                .build();
        productRepository.save(testProduct);
    }

    @Test
    @WithMockUser
    @DisplayName("장바구니에 아이템 추가 성공")
    void addCartItem_Success() throws Exception {
        // given
        Long productId = testProduct.getId();
        int quantity = 2;

        Map<String, Object> request = Map.of(
                "productId", productId,
                "quantity", quantity
        );

        // when & then
        mockMvc.perform(post("/api/v1/cart/items")
                        .param("userId", String.valueOf(testUser.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200"))
                .andExpect(jsonPath("$.msg").value("상품이 장바구니에 추가되었습니다."));

        // DB 검증
        Cart updatedCart = cartRepository.findByBuyerId(testUser.getId()).get();
        List<CartItem> items = updatedCart.getItems();

        assertThat(items).hasSize(1);
        assertThat(items.get(0).getProductId()).isEqualTo(productId);
        assertThat(items.get(0).getQuantity()).isEqualTo(quantity);
        assertThat(updatedCart.getTotalQuantity()).isEqualTo(quantity);
    }

    @Test
    @WithMockUser
    @DisplayName("동일 상품 추가 시 수량 증가")
    void addCartItem_SameProduct_QuantityIncreased() throws Exception {
        // given
        Long productId = testProduct.getId();
        int firstQuantity = 2;
        int secondQuantity = 3;

        Map<String, Object> request = Map.of(
                "productId", productId,
                "quantity", firstQuantity
        );

        // 첫 번째 추가
        mockMvc.perform(post("/api/v1/cart/items")
                        .param("userId", String.valueOf(testUser.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // 두 번째 추가
        Map<String, Object> secondRequest = Map.of(
                "productId", productId,
                "quantity", secondQuantity
        );

        // when & then
        mockMvc.perform(post("/api/v1/cart/items")
                        .param("userId", String.valueOf(testUser.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200"));

        // DB 검증
        Cart updatedCart = cartRepository.findByBuyerId(testUser.getId()).get();
        List<CartItem> items = updatedCart.getItems();

        assertThat(items).hasSize(1);
        assertThat(items.get(0).getQuantity()).isEqualTo(firstQuantity + secondQuantity);
        assertThat(updatedCart.getTotalQuantity()).isEqualTo(firstQuantity + secondQuantity);
    }
}