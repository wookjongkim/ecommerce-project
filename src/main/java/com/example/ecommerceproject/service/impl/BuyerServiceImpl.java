package com.example.ecommerceproject.service.impl;

import com.example.ecommerceproject.constant.Category;
import com.example.ecommerceproject.constant.ItemSellStatus;
import com.example.ecommerceproject.domain.dto.BuyerItemResponseDto;
import com.example.ecommerceproject.domain.dto.MemberInfoDto;
import com.example.ecommerceproject.domain.model.BuyerBalance;
import com.example.ecommerceproject.domain.model.Item;
import com.example.ecommerceproject.domain.model.Member;
import com.example.ecommerceproject.exception.BusinessException;
import com.example.ecommerceproject.exception.ErrorCode;
import com.example.ecommerceproject.repository.BuyerBalanceRepository;
import com.example.ecommerceproject.repository.ItemRepository;
import com.example.ecommerceproject.repository.MemberRepository;
import com.example.ecommerceproject.repository.spec.ItemSpecification;
import com.example.ecommerceproject.service.BuyerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BuyerServiceImpl implements BuyerService {

  private final BuyerBalanceRepository buyerBalanceRepository;
  private final MemberRepository memberRepository;
  private final ItemRepository itemRepository;

  @Transactional
  @Override
  public MemberInfoDto chargeBalance(Long buyerId, Long chargeVal) {

    Member member = memberRepository.findById(buyerId)
        .orElseThrow(() -> new BusinessException(ErrorCode.BUYER_NOT_FOUND));

    BuyerBalance buyerBalance = buyerBalanceRepository.findByMemberId(member.getId())
        .orElseThrow(() -> new BusinessException(ErrorCode.BALANCE_NOT_FOUND));

    buyerBalance.setBalance(buyerBalance.getBalance() + chargeVal);

    return MemberInfoDto.of(member, buyerBalance.getBalance());
  }

  @Override
  @Transactional(readOnly = true)
  public Page<BuyerItemResponseDto> lookupItems(String component, int minPrice, int maxPrice,
      String priceOrder, ItemSellStatus itemSellStatus, Category category, Pageable pageable) {

    Specification<Item> spec = Specification
        .where(ItemSpecification.withComponent(component))
        .and(ItemSpecification.withPriceBetween(minPrice, maxPrice))
        .and(ItemSpecification.withSaleStatus(itemSellStatus))
        .and(ItemSpecification.withCategory(category));

    Page<Item> items = itemRepository.findAll(spec, pageable);

    return items.map(BuyerItemResponseDto::of);
  }
}
