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
import com.example.ecommerceproject.domain.dto.response.ItemDetailDto;
import com.example.ecommerceproject.domain.dto.request.ItemFormRequestDto;
import com.example.ecommerceproject.domain.dto.response.ItemUpdateDto;
import com.example.ecommerceproject.domain.dto.response.SellerItemResponseDto;
import com.example.ecommerceproject.domain.dto.request.WithdrawDto;
import com.example.ecommerceproject.domain.dto.response.WithdrawResponseDto;
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
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
class SellerServiceTest {

  @Mock
  private ItemRepository itemRepository;

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private StockRepository stockRepository;

  @Mock
  private SellerRevenueRepository sellerRevenueRepository;

  @InjectMocks
  private SellerService sellerService;

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

  @Test
  @DisplayName("판매자의 상품 제고 추가 테스트")
  void addItemStock(){
    // Given
    Stock stock = Stock.builder()
        .id(1L)
        .quantity(100).build();

    Item item = Item.builder()
        .id(1L)
        .sellerId(1L)
        .itemName("테스트 네임1")
        .price(1000)
        .itemDetail("테스트 상품입니다.")
        .cateGory(Category.FOOD)
        .stock(stock)
        .saleStatus(ItemSellStatus.SELL).build();


    when(itemRepository.findByIdAndSellerId(anyLong(), anyLong())).thenReturn(Optional.of(item));
    when(stockRepository.findByIdForUpdate(anyLong())).thenReturn(Optional.of(stock));

    // When
    SellerItemResponseDto itemResponseDto = sellerService.addStock(1L, 1L, 100);

    // Then
    assertThat(itemResponseDto).isNotNull();
    assertThat(itemResponseDto.getQuantity()).isEqualTo(200);
  }

  @Test
  @DisplayName("판매자가 본인의 수익보다 더 큰 금액을 출금하려할 시 에러 발생")
  void withdraw_withInsufficientRevenue(){
    // Given
    Long sellerId = 1L;
    Long revenue = 5000L;

    SellerRevenue mockSellerRevenue = SellerRevenue
        .builder()
        .revenue(revenue)
        .build();

    when(sellerRevenueRepository.findByMemberId(sellerId))
        .thenReturn(Optional.of(mockSellerRevenue));

    // 판매자의 수익보다 1000원 더 큰 금액
    WithdrawDto withdrawDto = new WithdrawDto(revenue + 1000L);

    // When
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      sellerService.withdrawBalance(sellerId, withdrawDto);
    });

    // Then
    assertEquals(ErrorCode.INSUFFICIENT_REVENUE, exception.getErrorCode());
  }

  @Test
  @DisplayName("판매자가 본인의 수익 내에서 금액을 정상적으로 출금")
  void withdraw_withSufficientRevenue(){
    //Given
    Long sellerId = 1L;
    Long revenue = 5000L;

    SellerRevenue mockSellerRevenue = SellerRevenue
        .builder()
        .revenue(revenue)
        .build();

    when(sellerRevenueRepository.findByMemberId(sellerId))
        .thenReturn(Optional.of(mockSellerRevenue));

    // 수익보다 1000원 작은 금액 출금
    WithdrawDto withdrawDto = new WithdrawDto(revenue - 1000L);

    // When
    WithdrawResponseDto response = sellerService.withdrawBalance(sellerId, withdrawDto);

    // Then
    assertEquals(1000L, response.getBalance());
  }
}