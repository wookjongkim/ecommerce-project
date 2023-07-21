package com.example.ecommerceproject.service;

import com.example.ecommerceproject.domain.dto.ItemFormDto;
import com.example.ecommerceproject.domain.model.Item;
import com.example.ecommerceproject.domain.model.Member;
import com.example.ecommerceproject.domain.model.Stock;
import com.example.ecommerceproject.exception.BusinessException;
import com.example.ecommerceproject.exception.ErrorCode;
import com.example.ecommerceproject.repository.ItemRepository;
import com.example.ecommerceproject.repository.MemberRepository;
import com.example.ecommerceproject.util.ValidUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemService {

  private final MemberRepository memberRepository;
  private final ItemRepository itemRepository;

  // @Transactional 사용 이유
  // 만약 트랜잭션을 사용하지 않았다면, 각각의 DB 작업(INSERT, UPDATE 등등)은 별개의 트랜잭션으로 취급됨
  // 즉, 각 작업마다 별도의 커밋이 발생하고, 이전 작업에 대한 롤백이 불가능 해지는 것!!

  // addItem에서는 새로운 상품을 등록하고, 이에 대한 재고를 등록함
  // 만약 상품 등록은 성공했지만, 재고 등록에서 에러가 발생한다면???, 상품 등록은 이미 커밋되었기에 롤백이 불가능함
  // 이를 방지하기 위해 사용, 동시성 문제 해결하는 것 X
  @Transactional
  public String addItem(ItemFormDto itemFormDto) {

    // 사용자 조회및 권한 체크
    Member member = memberRepository.findById(itemFormDto.getSellerId())
        .orElseThrow(() -> new BusinessException(ErrorCode.SELLER_NOT_FOUND));

    if(!ValidUtil.isSeller(member)){
      // 판매자가 아닌 경우 상품 등록 불가
      throw new BusinessException(ErrorCode.UNAUTHORIZED_REQUEST);
    }

    // 상품 생성
    Item item = Item.makeItem(itemFormDto);

    // 재고 정보 생성
    Stock stock = Stock.makeStock(itemFormDto.getStockNumber());
    item.setStock(stock);

    // 상품 저장시 재고도 같이 저장 됨(OneToOne 단방향 맺어둠)
    itemRepository.save(item);

    return "상품 등록이 완료되었습니다!";
  }
}
