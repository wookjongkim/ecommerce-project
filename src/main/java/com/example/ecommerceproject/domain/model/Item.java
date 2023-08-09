package com.example.ecommerceproject.domain.model;

import com.example.ecommerceproject.constant.Category;
import com.example.ecommerceproject.constant.ItemSellStatus;
import com.example.ecommerceproject.domain.dto.request.ItemFormRequestDto;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item extends BaseTimeEntity {

  @Id
  @Column(name = "item_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 판매자 아이디
  private Long sellerId;

  @Column(nullable = false, length = 50)
  private String itemName;

  @Column(nullable = false)
  private int price;

  @Lob
  @Column(nullable = false)
  private String itemDetail; // 상품 상세 설명

  @Enumerated(EnumType.STRING)
  private Category cateGory;

  @Enumerated(EnumType.STRING)
  private ItemSellStatus saleStatus;

  // 단방향으로 설계, Item 조회 시 Stock 엔티티도 함께 로딩됨(Eager Loading이 @OneToOne의 default)
  @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
  @JoinColumn(name = "stock_id")
  private Stock stock;

  public static Item of(Long sellerId, ItemFormRequestDto itemFormRequestDto) {
    return Item.builder()
        .sellerId(sellerId)
        .itemName(itemFormRequestDto.getItemName())
        .price(itemFormRequestDto.getPrice())
        .itemDetail(itemFormRequestDto.getItemDetail())
        .cateGory(itemFormRequestDto.getCategory())
        .saleStatus(itemFormRequestDto.getItemSellStatus())
        .build();
  }
}
