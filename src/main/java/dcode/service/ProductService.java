package dcode.service;

import dcode.domain.entity.Product;
import dcode.domain.entity.Promotion;
import dcode.model.request.ProductInfoRequest;
import dcode.model.response.ProductAmountResponse;
import dcode.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductAmountResponse getProductAmount(ProductInfoRequest request) throws Exception{
        System.out.println("상품 가격 추출 로직을 완성 시켜주세요.");

        Product product = repository.getProduct(request.getProductId());
        // 사용 가능한 쿠폰만 조회
        List<Promotion> availablePromotion = repository.getProductAmountAndUseCoupon(request.getProductId(), request.getCouponIds());
        // 기본 상품 가격
        final int originPrice = product.getPrice();
        // 할인금액 계산
        Double discount = getDisCountRate(availablePromotion, originPrice);
        // 상품 이름
        String productName = product.getName();
        // 조건 최종 반환
        return ProductAmountResponse.of(productName, originPrice, discount);
    }

    // 할인가격 계산
    // 면접 질문: 여기서 if else를 사용하지않고 할인가격을 계산할 방법이 있을거같은데? 뭔지?
    private Double getDisCountRate(List<Promotion> discountPromotions, int originPrice) {
        Double discount = 0.0;
        for (Promotion promotion : discountPromotions) {
            if (promotion.getPromotion_type().equals("COUPON")) {
                discount += promotion.getDiscount_value();
            } else if (promotion.getPromotion_type().equals("CODE")) {
                discount += originPrice * ((double) promotion.getDiscount_value() / 100);
            }
        }
        return discount;
    }
}
