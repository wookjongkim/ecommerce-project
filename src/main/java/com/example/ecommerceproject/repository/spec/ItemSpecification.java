package com.example.ecommerceproject.repository.spec;

import com.example.ecommerceproject.constant.ItemSellStatus;
import com.example.ecommerceproject.domain.model.Item;
import java.time.LocalDateTime;
import javax.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class ItemSpecification {

  // root는 쿼리의 루트 엔티티를 참조(속성에 access하려면 root.get("속성 이름")과 같은 형식 사용
  // query는 쿼리를 정의하는 인터페이스로 보통 select, group by, order by 등 조작을 할때 사용
  // builder는 CriteriaBuilder로 쿼리의 조건을 생성하는데 사용되는 팩토리 클래스
  // equals, not equals, and,or,like등의 조건을 생성

  public static Specification<Item> withSellerId(Long sellerId) {
    return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("sellerId"),
        sellerId));
  }

  public static Specification<Item> withCreatedDateBetween(LocalDateTime startDate,
      LocalDateTime endDate) {
    return ((root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("createdAt"),
        startDate, endDate));
  }

  public static Specification<Item> withPriceBetween(int minPrice, int maxPrice) {
    return ((root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("price"), minPrice,
        maxPrice));
  }

  public static Specification<Item> withSaleStatus(ItemSellStatus itemSellStatus) {
    return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("saleStatus"),
        itemSellStatus));
  }

  public static Specification<Item> withQuantityOrder(String order) {
    return ((root, query, criteriaBuilder) -> {
      root.join("stock", JoinType.INNER);
      if ("desc".equals(order)) {
        query.orderBy(criteriaBuilder.desc(root.get("stock").get("quantity")));
      } else {
        // asc인 경우
        query.orderBy(criteriaBuilder.asc(root.get("stock").get("quantity")));
      }
      return null;
    });
  }
}
