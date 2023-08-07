package com.example.ecommerceproject.service.impl;

import com.example.ecommerceproject.constant.ItemSellStatus;
import com.example.ecommerceproject.domain.dto.ItemDetailDto;
import com.example.ecommerceproject.domain.dto.ItemFormRequestDto;
import com.example.ecommerceproject.domain.dto.ItemUpdateDto;
import com.example.ecommerceproject.domain.dto.SellerItemResponseDto;
import com.example.ecommerceproject.domain.dto.WithdrawDto;
import com.example.ecommerceproject.domain.dto.WithdrawResponseDto;
import com.example.ecommerceproject.domain.model.Item;
import com.example.ecommerceproject.domain.model.Member;
import com.example.ecommerceproject.domain.model.SellerRevenue;
import com.example.ecommerceproject.domain.model.Stock;
import com.example.ecommerceproject.exception.BusinessException;
import com.example.ecommerceproject.exception.ErrorCode;
import com.example.ecommerceproject.repository.ItemRepository;
import com.example.ecommerceproject.repository.MemberRepository;
import com.example.ecommerceproject.repository.SellerRevenueRepository;
import com.example.ecommerceproject.repository.StockRepository;
import com.example.ecommerceproject.repository.spec.ItemSpecification;
import com.example.ecommerceproject.service.SellerService;
import com.example.ecommerceproject.util.ValidUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {

  private final MemberRepository memberRepository;
  private final ItemRepository itemRepository;
  private final StockRepository stockRepository;
  private final SellerRevenueRepository sellerRevenueRepository;

  // @Transactional 사용 이유
  // 만약 트랜잭션을 사용하지 않았다면, 각각의 DB 작업(INSERT, UPDATE 등등)은 별개의 트랜잭션으로 취급됨
  // 즉, 각 작업마다 별도의 커밋이 발생하고, 이전 작업에 대한 롤백이 불가능 해지는 것!!

  // addItem에서는 새로운 상품을 등록하고, 이에 대한 재고를 등록함
  // 만약 상품 등록은 성공했지만, 재고 등록에서 에러가 발생한다면???, 상품 등록은 이미 커밋되었기에 롤백이 불가능함
  // 이를 방지하기 위해 사용, 동시성 문제 해결하는 것 X
  @Override
  @Transactional
  public String addItem(Long sellerId, ItemFormRequestDto itemFormRequestDto) {
    Member member = memberRepository.findById(sellerId)
        .orElseThrow(() -> new BusinessException(ErrorCode.SELLER_NOT_FOUND));

    if (!ValidUtil.isSeller(member)) {
      // 판매자가 아닌 경우 상품 등록 불가
      throw new BusinessException(ErrorCode.UNAUTHORIZED_REQUEST);
    }

    // 상품 생성
    Item item = Item.of(sellerId, itemFormRequestDto);

    // 재고 정보 생성
    Stock stock = Stock.of(itemFormRequestDto.getStockNumber());

    // 연관관계 맺어줌
    item.setStock(stock);

    // 상품 저장시 재고도 같이 저장 됨(OneToOne 단방향 맺어둠, PERSIST 옵션을 적용하였기에 가능하다는 점 꼭 인지!)
    itemRepository.save(item);

    return "상품 등록이 완료되었습니다!";
  }

  // 여기 readOnly
  @Override
  // readOnly 사용시, Hibernate와 같은 JPA 구현체는 내부적으로 데이터 변경을 체크하는 작업을 최소화하거나 생략하여 성능 향상
  @Transactional(readOnly = true)
  public Page<SellerItemResponseDto> getItems(Long sellerId, LocalDate startDate, LocalDate endDate,
      int minPrice,
      int maxPrice, ItemSellStatus itemSellStatus, String quantityOrder, Pageable pageable) {

    LocalDateTime startDateTime = startDate.atStartOfDay();
    LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

    Specification<Item> spec = Specification
        .where(ItemSpecification.withSellerId(sellerId))
        .and(ItemSpecification.withCreatedDateBetween(startDateTime, endDateTime))
        .and(ItemSpecification.withPriceBetween(minPrice, maxPrice))
        .and(ItemSpecification.withSaleStatus(itemSellStatus))
        .and(ItemSpecification.withQuantityOrder(quantityOrder));

    Page<Item> items = itemRepository.findAll(spec, pageable);

    return items.map(SellerItemResponseDto::of);
  }

  @Override
  @Transactional(readOnly = true)
  public ItemDetailDto getItem(Long sellerId, Long itemId) {
    Item item = itemRepository.findByIdAndSellerId(sellerId, itemId)
        .orElseThrow(() -> new BusinessException(ErrorCode.ITEM_NOT_MATCH));

    return ItemDetailDto.of(item);
  }

  @Override
  @Transactional
  public ItemUpdateDto editItem(Long sellerId, Long itemId, ItemUpdateDto itemUpdateDto) {
    Item item = itemRepository.findByIdAndSellerId(sellerId, itemId)
        .orElseThrow(() -> new BusinessException(ErrorCode.ITEM_NOT_MATCH));

    if (itemUpdateDto.getItemName() != null) {
      item.setItemName(itemUpdateDto.getItemName());
    }

    if (itemUpdateDto.getPrice() != null) {
      item.setPrice(itemUpdateDto.getPrice());
    }

    if (itemUpdateDto.getItemDetail() != null) {
      item.setItemDetail(itemUpdateDto.getItemDetail());
    }

    if (itemUpdateDto.getSaleStatus() != null) {
      item.setSaleStatus(itemUpdateDto.getSaleStatus());
    }

    if (itemUpdateDto.getCategory() != null) {
      item.setCateGory(itemUpdateDto.getCategory());
    }
//    변경된 엔티티 저장 따로 할 필요 X
//    트랜잭션(@Transactional) 범위 내에서 가져온 (ex: findByIdAndSellerId) JPA 엔티티는 영속성 컨텍스트에 의해 관리됨
//    이렇게 관리되는 엔티티에 대한 변경 사항은 트랜잭션 종료 시점에 JPA에 의해 자동으로 DB에 반영(더티 체킹)
//    itemRepository.save(item);

    return ItemUpdateDto.of(item);
  }

  @Override
  @Transactional
  public SellerItemResponseDto addStock(Long sellerId, Long itemId, int addNum) {
    Item item = itemRepository.findByIdAndSellerId(itemId, sellerId)
        .orElseThrow(() -> new BusinessException(ErrorCode.ITEM_NOT_MATCH));

    Stock stock = stockRepository.findByIdForUpdate(item.getStock().getId())
        .orElseThrow(() -> new BusinessException(ErrorCode.STOCK_NOT_FOUND));

    stock.setQuantity(stock.getQuantity() + addNum);

//    findById 사용시 해당 ID를 가진 엔티티가 검색되고, 이 엔티티는 자동으로 영속성 컨텍스트에 포함됨
//    이렇게 영속화된 Stock을 변경하면, 이 변경사항은 영속성 컨텍스트에 의해 추적되고, 트랜잭션 종료될때 DB에 반영
//    stockRepository.save(stock);

    return SellerItemResponseDto.of(item);
  }

  @Override
  @Transactional
  public WithdrawResponseDto withdrawBalance(Long sellerId, WithdrawDto withdrawDto) {

    // 이 과정에서 sellerRevenue에 대해 락이 걸림
    SellerRevenue sellerRevenue = sellerRevenueRepository.findByMemberId(sellerId)
        .orElseThrow(() -> new BusinessException(ErrorCode.SELLER_REVENUE_NOT_FOUND));

    Long withdrawAmount = withdrawDto.getAmount();

    if (sellerRevenue.getRevenue() < withdrawAmount) {
      throw new BusinessException(ErrorCode.INSUFFICIENT_REVENUE);
    }

    sellerRevenue.setRevenue(sellerRevenue.getRevenue() - withdrawAmount);

    return WithdrawResponseDto.of(sellerRevenue.getRevenue());
  }
}
