package com.example.ecommerceproject.domain.dto.response;

import com.example.ecommerceproject.domain.model.Order;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDto {

  private long orderId;

  private long userId;

  private LocalDateTime orderDate;

  private long totalPrice;

  private List<OrderItemDto> items;

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class OrderItemDto {

    private long itemId;
    private int quantity;
    private int price;
  }

  public static OrderResponseDto of(Order order) {

    LocalDateTime orderDate = LocalDateTime.now();

    List<OrderItemDto> orderItemDtoList =
        order.getOrderItems().stream()
            .map(orderItem -> new OrderItemDto(orderItem.getItemId(), orderItem.getQuantity(),
                orderItem.getPrice()))
            .collect(Collectors.toList());

    return OrderResponseDto.builder()
        .orderId(order.getId())
        .userId(order.getBuyerId())
        .orderDate(orderDate)
        .totalPrice(order.getTotalPrice())
        .items(orderItemDtoList)
        .build();
  }
}
