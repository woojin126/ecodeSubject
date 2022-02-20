package dcode.model.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductAmountResponse {
    private String name; //상품명

    private int originPrice; //상품 기존 가격
    private int discountPrice; //총 할인 금액
    private int finalPrice; //확정 상품 가격

    @Builder
    public ProductAmountResponse(String name, int originPrice, int discountPrice, int finalPrice) {
        this.name = name;
        this.originPrice = originPrice;
        this.discountPrice = discountPrice;
        this.finalPrice = finalPrice;
    }

    public static ProductAmountResponse of(String name, int originPrice, Double discount) throws Exception {
        ProductAmountResponse productAmountResponse = new ProductAmountResponse();
        productAmountResponse.setName(name); // 상품 이름
        productAmountResponse.setOriginPrice(originPrice); // 상품 기본 가격
        if (originPrice - discount >= 10000) { // 할인된 최소 상품가격은 10,000 이상
            productAmountResponse.setDiscountPrice(discount.intValue()); // 총 할인금액
            productAmountResponse.setFinalPrice(((originPrice - discount.intValue()) / 1000) * 1000); // 최종 상품 금액은 천단위 절삭합니다.
        } else {
            throw new Exception("할인된 최소 상품가격은 10,000 이상어이야 합니다."); //만원 미만일시 에러
        }

        return productAmountResponse;
    }
}