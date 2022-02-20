package dcode.service;

import dcode.domain.entity.Product;
import dcode.model.request.ProductInfoRequest;
import dcode.model.response.ProductAmountResponse;
import dcode.repository.ProductRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
@Rollback
public class ProductServiceTest {

    @Autowired
    ProductService productService;

    @Autowired
    ProductRepository productRepository;
    //---- 상품1 ----//
    ProductInfoRequest haveCoupon;
    ProductInfoRequest haveCode;
    ProductInfoRequest haveCodeAndCoupon;
    //---- 상품2 ----//
    ProductInfoRequest haveCoupon1;
    ProductInfoRequest haveCode1;
    ProductInfoRequest haveCodeAndCoupon1;
    @BeforeEach
    public void setup(){
        //---- 상품1 ----//
        haveCoupon = ProductInfoRequest.builder()
                .productId(1)
                .couponIds(new int[]{1})
                .build();

         haveCode = ProductInfoRequest.builder()
                .productId(1)
                .couponIds(new int[]{2})
                .build();

         haveCodeAndCoupon = ProductInfoRequest.builder()
                .productId(1)
                .couponIds(new int[]{1,2})
                .build();

        //---- 상품2 ----//
        haveCoupon1 = ProductInfoRequest.builder()
                .productId(2)//테스트코드를 위해 2번상품 data.sql에 추가
                .couponIds(new int[]{1})
                .build();

        haveCode1 = ProductInfoRequest.builder()
                .productId(2)//테스트코드를 위해 2번상품 data.sql에 추가
                .couponIds(new int[]{2})
                .build();

        haveCodeAndCoupon1 = ProductInfoRequest.builder()
                .productId(2)
                .couponIds(new int[]{1,2})
                .build();
    }


    @Nested
    @DisplayName("쿠폰 및 코드 검증")
    class Coupon {

        @Nested
        @DisplayName("성공 케이스")
        class success {

            @Test
            @DisplayName("COUPON 적용")
            void test_1() throws Exception {
                ProductAmountResponse productAmount = productService.getProductAmount(haveCoupon);
                Assertions.assertEquals(100000, productAmount.getDiscountPrice()); //1번 쿠폰적용시 100000원 할인 금액 확인
                Assertions.assertEquals(Math.round((productAmount.getOriginPrice() - productAmount.getDiscountPrice())/1000)*1000, productAmount.getFinalPrice()); // 기본가격 상품 - 할인률 = 최종 상품가격
            }

            @Test
            @DisplayName("CODE 적용")
            void test_2() throws Exception {
                ProductAmountResponse productAmount = productService.getProductAmount(haveCode);
                Assertions.assertEquals(32250, productAmount.getDiscountPrice()); //2번 코드적용시 15% 할인 금액 확인
                Assertions.assertEquals(Math.round((productAmount.getOriginPrice() - productAmount.getDiscountPrice())/1000)*1000, productAmount.getFinalPrice()); // 기본가격 상품 - 할인률 = 최종 상품가격 (절삭)
            }

            @Test
            @DisplayName("COUPON, CODE 적용")
            void test_3() throws Exception {
                ProductAmountResponse productAmount = productService.getProductAmount(haveCodeAndCoupon);
                Assertions.assertEquals(132250, productAmount.getDiscountPrice()); //쿠폰,코드 둘다 적용시 할인 금액 확인
                Assertions.assertEquals(Math.round((productAmount.getOriginPrice() - productAmount.getDiscountPrice())/1000)*1000, productAmount.getFinalPrice()); // 기본가격 상품 - 할인률 = 최종 상품가격 (절삭)
            }

        }

        @Nested
        @DisplayName("실패 케이스")
        class Fail {

            @Test
            @DisplayName("할인된 최소 상품가격은 10,000 이상어이야 합니다. (쿠폰적용시)" ) //테스트 코드를위해 data.sql에 11000원 상품 추가
            void test_1() throws Exception {
                // 11000원 상품에 15% 할인이되면 < 10000 조건으로 에러발생
                Exception exception = assertThrows(Exception.class, () -> productService.getProductAmount(haveCoupon1));
                assertEquals("할인된 최소 상품가격은 10,000 이상어이야 합니다.", exception.getMessage());
            }

            @Test
            @DisplayName("할인된 최소 상품가격은 10,000 이상어이야 합니다. (코드적용시)" ) //테스트 코드를위해 data.sql에 11000원 상품 추가
            void test_2() throws Exception {
                // 11000원 상품에 15% 할인이되면 < 10000 조건으로 에러발생
                Exception exception = assertThrows(Exception.class, () -> productService.getProductAmount(haveCode1));
                assertEquals("할인된 최소 상품가격은 10,000 이상어이야 합니다.", exception.getMessage());
            }

            @Test
            @DisplayName("할인된 최소 상품가격은 10,000 이상어이야 합니다. (쿠폰,코드 둘다 적용시)" ) //테스트 코드를위해 data.sql에 11000원 상품 추가
            void test_3() throws Exception {
                // 11000원 상품에 15% 할인이되면 < 10000 조건으로 에러발생
                Exception exception = assertThrows(Exception.class, () -> productService.getProductAmount(haveCodeAndCoupon1));
                assertEquals("할인된 최소 상품가격은 10,000 이상어이야 합니다.", exception.getMessage());
            }
        }

    }

}