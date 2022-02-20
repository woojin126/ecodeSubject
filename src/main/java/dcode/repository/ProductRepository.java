package dcode.repository;

import dcode.domain.entity.Product;
import dcode.domain.entity.Promotion;
import dcode.model.response.ProductAmountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Repository
public class ProductRepository {

    //NamedParameterJdbcTemplate는 ?대신 :변수명 을 이용하여 처리함으로써 순서에 강제를 받지 않는다.
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Product getProduct(int id) {

        String query = "SELECT * FROM `product` WHERE id = :id ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        return namedParameterJdbcTemplate.queryForObject(
                query,
                params,
                (rs, rowNum) -> Product.builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
                        .price(rs.getInt("price"))
                        .build()
        );
    }

    public List<Promotion> getProductAmountAndUseCoupon(int productId, int[] couponIds) {
        String query = "";
        query += "SELECT p.id , p.promotion_type , p.name, p.discount_type, p.discount_value, p.use_started_at, p.use_ended_at ";
        query +=  "FROM `promotion_products` pp ";
        query += ", `promotion` p ";
        query += "WHERE p.id = pp.promotion_id ";
        query += "AND pp.product_id = :productId "; // 해당 상품이 프로모션을 가지고있는지
        query += "AND p.use_started_at <= :now "; //쿠폰 사용 가능한 기간인지 확인
        query += "AND p.use_ended_at >= :now "; //쿠폰 사용 가능한 기간인지 확인
        if (couponIds.length > 0) { // 쿠폰이 있는지 없는지 확인
            query += "AND pp.promotion_id IN (:couponIds) ";
        }
        // 체인으로 연결할 수 있는 더 편리한 addValue 메서드를 제공하는 MapSqlParameterSource
        MapSqlParameterSource params = new MapSqlParameterSource();

        /*배열을 리스트형태로 변환
         primitive 타입을 List에 저장할수 없어서 boxed를 통해 wrapper타입으로 변환
        */
        List<Integer> couponIdList= Arrays.stream(couponIds).boxed().collect(Collectors.toList());
        params.addValue("productId", productId);
        params.addValue("now", LocalDate.now());
        params.addValue("couponIds", couponIdList);

        return namedParameterJdbcTemplate.query(
                query ,
                params,
                (rs, rowNum) -> Promotion.builder()
                        .id(rs.getInt("id"))
                        .promotion_type(rs.getString("promotion_type"))
                        .name(rs.getString("name"))
                        .discount_type(rs.getString("discount_type"))
                        .discount_value(rs.getInt("discount_value"))
                        .use_started_at(rs.getDate("use_started_at"))
                        .use_ended_at(rs.getDate("use_ended_at"))
                        .build()
        );
    }
}
