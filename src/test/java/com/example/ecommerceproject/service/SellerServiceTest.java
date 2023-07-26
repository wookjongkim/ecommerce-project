package com.example.ecommerceproject.service;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.ecommerceproject.constant.Category;
import com.example.ecommerceproject.constant.ItemSellStatus;
import com.example.ecommerceproject.constant.Role;
import com.example.ecommerceproject.domain.dto.ItemDetailDto;
import com.example.ecommerceproject.domain.dto.ItemFormRequestDto;
import com.example.ecommerceproject.domain.dto.ItemUpdateDto;
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

  @Test
  @DisplayName("판매자의 상품 상세 보기 테스트")
  void getItemDetail(){

    // Given
    Item item = Item.builder()
        .id(1L)
        .sellerId(1L)
        .itemName("테스트 네임1")
        .price(1000)
        .itemDetail("테스트 상품입니다.")
        .cateGory(Category.FOOD)
        .saleStatus(ItemSellStatus.SELL).build();

    when(itemRepository.findByIdAndSellerId(anyLong(), anyLong())).thenReturn(Optional.of(item));

    // When
    ItemDetailDto itemDetail = sellerService.getItem(1L, 1L);

    // Then
    assertThat(itemDetail).isNotNull();
    assertThat(itemDetail.getItemName()).isEqualTo(item.getItemName());
    assertThat(itemDetail.getPrice()).isEqualTo(item.getPrice());
    assertThat(itemDetail.getItemDetail()).isEqualTo(item.getItemDetail());
    assertThat(itemDetail.getSaleStatus()).isEqualTo(item.getSaleStatus());
    assertThat(itemDetail.getCategory()).isEqualTo(item.getCateGory());
  }

  @Test
  @DisplayName("판매자의 상품 상세 보기 실패 테스트")
  void getItemDetailFailure() {
    // Given
    when(itemRepository.findByIdAndSellerId(anyLong(), anyLong())).thenReturn(Optional.empty());

    // When
    BusinessException exception = assertThrows(BusinessException.class, () -> sellerService.getItem(1L, 1L));

    // Then
    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ITEM_NOT_MATCH);
  }

  @Test
  @DisplayName("판매자의 상품 수정 테스트")
  void editItem(){
    // Given
    Item item = Item.builder()
        .id(1L)
        .sellerId(1L)
        .itemName("테스트 네임1")
        .price(1000)
        .itemDetail("테스트 상품입니다.")
        .cateGory(Category.FOOD)
        .saleStatus(ItemSellStatus.SELL).build();

    ItemUpdateDto updateRequest = ItemUpdateDto.builder()
        .itemName("바꾼 이름1")
        .price(10001)
        .itemDetail("바꾼 설명입니다.")
        .category(Category.BEAUTY)
        .saleStatus(ItemSellStatus.SELL_STOPPED)
        .build();

    when(itemRepository.findByIdAndSellerId(anyLong(), anyLong())).thenReturn(Optional.of(item));

    // When
    ItemUpdateDto itemUpdateDto = sellerService.editItem(1L, 1L, updateRequest);

    // Then
    assertThat(itemUpdateDto).isNotNull();
    assertThat(itemUpdateDto.getItemName()).isEqualTo(updateRequest.getItemName());
    assertThat(itemUpdateDto.getPrice()).isEqualTo(updateRequest.getPrice());
    assertThat(itemUpdateDto.getItemDetail()).isEqualTo(updateRequest.getItemDetail());
    assertThat(itemUpdateDto.getSaleStatus()).isEqualTo(updateRequest.getSaleStatus());
    assertThat(itemUpdateDto.getCategory()).isEqualTo(updateRequest.getCategory());
  }
}