package com.example.ecommerceproject.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.ecommerceproject.constant.Category;
import com.example.ecommerceproject.constant.ItemSellStatus;
import com.example.ecommerceproject.constant.Role;
import com.example.ecommerceproject.domain.dto.request.ItemFormRequestDto;
import com.example.ecommerceproject.domain.dto.request.OrderRequestDto;
import com.example.ecommerceproject.domain.dto.request.OrderRequestDto.ItemOrderDto;
import com.example.ecommerceproject.domain.dto.response.OrderResponseDto;
import com.example.ecommerceproject.domain.dto.request.SignUpFormDto;
import com.example.ecommerceproject.service.BuyerService;
import com.example.ecommerceproject.service.SellerService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BuyerServiceConcurrencyTest {

  @Autowired
  private BuyerService buyerService;

  @Autowired
  private MemberService memberService;

  @Autowired
  private SellerService sellerService;

  private static final int THREAD_COUNT = 10;

  private OrderRequestDto orderRequestDto;

  @BeforeEach
  public void setUp(){
    // 판매자 등록
    memberService.signUp(SignUpFormDto.builder()
        .name("판매자")
        .email("test@gmail.com")
        .password("sdfsjlk1@")
        .address("대한민국")
        .phoneNumber("010-1234-5678")
        .role(Role.SELLER)
        .build());

    // 구매자 등록
    for(int i = 2; i <= 11; i++){
      memberService.signUp(SignUpFormDto.builder()
          .name("구매자" + i)
          .email("test" + i + "@gmail.com")
          .password("sdfsjlk1@")
          .address("대한민국")
          .phoneNumber("010-1234-5678")
          .role(Role.BUYER)
          .build());
    }

    sellerService.addItem(1L, ItemFormRequestDto.builder()
        .itemName("테스트 상품")
        .price(1000)
        .stockNumber(2)
        .itemDetail("테스트 상품입니다.")
        .category(Category.BEAUTY)
        .itemSellStatus(ItemSellStatus.SELL).build());

    for(int i = 2; i <= 11; i++){
      buyerService.chargeBalance((long) i, 10000L);
    }

    orderRequestDto = orderRequestDto.builder()
        .itemOrders(Collections.singletonList(new ItemOrderDto(1L, 1)))
        .build();

  }

  @Test
  @DisplayName("2개 남은 상품에 대해 10명의 사용자가 동시에 주문 요청을 보내는 경우 테스트")
  public void test_orderItems_concurrency() throws InterruptedException{

    // 고정된 쓰레드의 숫자만큼 쓰레드 풀을 생성
    ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

    // 일정 개수(여기선 10개) 쓰레드가 끝난 후에 다음 쓰레드가 실행될 수 있도록 대기하고
    // 끝나면 다음 쓰레드가 실행되도록 함
    CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

    // submit으로 리턴 받은 비동기 수행 결과값을 저장할때 사용(Future)
    List<Future<OrderResponseDto>> futures = new ArrayList<>();

    for(int i = 0; i < THREAD_COUNT; i++){
      Long buyerId = (long) i+2; // 구매자 아이디 2부터 11까지

      // executorService.submit은 작업을 쓰레드 풀의 큐에 추가하는 것으로
      // 여러 쓰레드가 동시에 동작하도록 요청을 쓰레드 풀에 분산시키는 것
      // 이를 통해 실제 서비스 환경에서 사용자들이 동시에 요청을 보낼 떄와 유사한 상황을 재현할 수 있으므로 동시성 문제 테스트에 적합
      futures.add(executorService.submit(() -> {
        try{
          System.out.println("고객" + buyerId + "예약 시작");
          return buyerService.orderItems(buyerId, orderRequestDto);
        } finally{
          // 쓰레드가 끝날때마다 카운트를 감소시킴
          latch.countDown();
        }
      }));
    }

    // 카운트가 0이 되면 대기가 풀리고, 이후 스레드가 실행됨
    latch.await(); // 모든 쓰레드 완료 시 까지 대기한다는 의미

    long successCount = futures.stream()
        .filter(future -> {
          try {
            return future.get() != null;
          } catch (Exception e) {
            return false;
          }
        })
        .count();

    assertEquals(2, successCount); // 성공한 주문 수가 2여야 함
  }
}
