package com.example.ecommerceproject.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.ecommerceproject.constant.OrderStatus;
import com.example.ecommerceproject.constant.Role;
import com.example.ecommerceproject.domain.dto.MemberInfoDto;
import com.example.ecommerceproject.domain.model.BuyerBalance;
import com.example.ecommerceproject.domain.model.Item;
import com.example.ecommerceproject.domain.model.Member;
import com.example.ecommerceproject.domain.model.Order;
import com.example.ecommerceproject.domain.model.OrderItem;
import com.example.ecommerceproject.domain.model.SellerRevenue;
import com.example.ecommerceproject.domain.model.Stock;
import com.example.ecommerceproject.exception.BusinessException;
import com.example.ecommerceproject.exception.ErrorCode;
import com.example.ecommerceproject.repository.BuyerBalanceRepository;
import com.example.ecommerceproject.repository.ItemRepository;
import com.example.ecommerceproject.repository.MemberRepository;
import com.example.ecommerceproject.repository.OrderRepository;
import com.example.ecommerceproject.repository.SellerRevenueRepository;
import com.example.ecommerceproject.repository.StockRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
class BuyerServiceTest {

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private BuyerBalanceRepository buyerBalanceRepository;

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private StockRepository stockRepository;

  @Mock
  private SellerRevenueRepository sellerRevenueRepository;

  @Mock
  private ItemRepository itemRepository;

  @InjectMocks
  private BuyerServiceImpl buyerService;

  private Member member;
  private BuyerBalance buyerBalance;
  @BeforeEach
  void setUp(){
    member = Member.builder()
        .id(1L)
        .name("test")
        .email("test@example.com")
        .password("Test123!")
        .address("test address")
        .phoneNumber("010-1234-5678")
        .role(Role.BUYER)
        .build();

    buyerBalance = BuyerBalance.builder()
        .memberId(1L)
        .balance(0L).build();
  }

  @Test
  @DisplayName("잔액 충전 테스트")
  void chargeBalance(){
    // given
    when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
    when(buyerBalanceRepository.findByMemberId(anyLong())).thenReturn(Optional.of(buyerBalance));

    // when
    MemberInfoDto infoDto = buyerService.chargeBalance(1L, 10000L);

    // Then
    assertEquals(10000L, infoDto.getBalance());
    verify(memberRepository, times(1)).findById(1L);
    verify(buyerBalanceRepository, times(1)).findByMemberId(1L);
  }

  @Test
  @DisplayName("이미 취소된 주문에 대해 취소 요청을 할 시 에러 발생")
  void cancelOrder_WithCanceledOrder(){
    // Given
    Order order = Order.builder()
        .id(1L)
        .buyerId(1L)
        .orderStatus(OrderStatus.CANCELED)
        .build();

    when(memberRepository.findById(anyLong()))
        .thenReturn(Optional.of(member));

    when(orderRepository.findById(anyLong()))
        .thenReturn(Optional.of(order));

    // When
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      buyerService.cancelOrder(1L, 1L);
    });

    // Then
    assertEquals(ErrorCode.ORDER_ALREADY_CANCELED, exception.getErrorCode());
  }

  @Test
  @DisplayName("주문 상태가 정상일 때, 주문 취소 요청이 정상 적으로 처리됨")
  void cancelOrder_WithValidOrder(){
    // Given
    Order order = Order.builder()
        .id(1L)
        .buyerId(1L)
        .totalPrice(1000L)
        .orderStatus(OrderStatus.COMPLETED)
        .build();

    List<OrderItem> orderItems = new ArrayList<>();
    OrderItem orderItem = OrderItem.builder()
        .itemId(1L)
        .quantity(1)
        .price(1000)
        .build();
    orderItems.add(orderItem);
    order.setOrderItems(orderItems);

    Stock stock = Stock.builder()
        .id(1L)
        .quantity(5)
        .build();

    Item item = Item.builder()
        .id(1L)
        .sellerId(2L)
        .stock(stock)
        .build();

    Member member = Member.builder()
        .id(1L)
        .build();

    SellerRevenue sellerRevenue = SellerRevenue.builder()
        .revenue(1000L)
        .build();

    when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));

    when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

    when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

    when(stockRepository.findByIdForUpdate(anyLong())).thenReturn(Optional.of(stock));

    when(sellerRevenueRepository.findByMemberId(anyLong())).thenReturn(Optional.of(sellerRevenue));

    when(buyerBalanceRepository.findByMemberId(anyLong())).thenReturn(Optional.of(buyerBalance));

    String result = buyerService.cancelOrder(1L, 1L);

    // Then
    assertEquals("주문 취소가 완료되었습니다 :)", result);
    assertEquals(6, stock.getQuantity());
    assertEquals(1000, buyerBalance.getBalance());
    assertEquals(0, sellerRevenue.getRevenue());
  }
}