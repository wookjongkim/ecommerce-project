package com.example.ecommerceproject.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.ecommerceproject.constant.Category;
import com.example.ecommerceproject.constant.ItemSellStatus;
import com.example.ecommerceproject.constant.Role;
import com.example.ecommerceproject.domain.dto.ItemFormRequestDto;
import com.example.ecommerceproject.domain.model.Item;
import com.example.ecommerceproject.domain.model.Member;
import com.example.ecommerceproject.exception.BusinessException;
import com.example.ecommerceproject.exception.ErrorCode;
import com.example.ecommerceproject.repository.ItemRepository;
import com.example.ecommerceproject.repository.MemberRepository;
import com.example.ecommerceproject.service.impl.SellerServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SellerServiceTest {

  @Mock
  private ItemRepository itemRepository;

  @Mock
  private MemberRepository memberRepository;

  @InjectMocks
  private SellerServiceImpl sellerService;

  @Test
  @DisplayName("판매자의 상품 등록 테스트")
  void addItem(){

    // Given
    Member member = Member.builder()
        .id(1L)
        .name("SELLER")
        .role(Role.SELLER)
        .build();

    ItemFormRequestDto itemFormRequestDto = ItemFormRequestDto.builder()
        .sellerId(member.getId())
        .itemName("Test Name")
        .price(1000)
        .itemDetail("Test Detail")
        .category(Category.BEAUTY)
        .itemSellStatus(ItemSellStatus.SELL)
        .stockNumber(10)
        .build();

    Item item = Item.of(1L, itemFormRequestDto);

    when(memberRepository.findById(any())).thenReturn(Optional.of(member));
    when(itemRepository.save(any())).thenReturn(item);

    // when
    String result = sellerService.addItem(1L, itemFormRequestDto);

    // then
    assertEquals("상품 등록이 완료되었습니다!", result);
    verify(memberRepository, times(1)).findById(any());
    verify(itemRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("판매자가 아닌 구매자가 상품을 등록하려는 경우 에러")
  void addItem_nonSeller(){

    // Given
    Member member = Member.builder()
        .id(1L)
        .name("BUYER")
        .role(Role.BUYER)
        .build();

    ItemFormRequestDto itemFormRequestDto = ItemFormRequestDto.builder()
        .itemName("Test Name")
        .price(1000)
        .itemDetail("Test Detail")
        .category(Category.BEAUTY)
        .itemSellStatus(ItemSellStatus.SELL)
        .stockNumber(10)
        .build();

    Item item = Item.of(1L, itemFormRequestDto);

    when(memberRepository.findById(any())).thenReturn(Optional.of(member));

    // when
    BusinessException exception = assertThrows(BusinessException.class,
        () -> sellerService.addItem(1L, itemFormRequestDto));

    // then
    assertEquals(ErrorCode.UNAUTHORIZED_REQUEST, exception.getErrorCode());
  }
}