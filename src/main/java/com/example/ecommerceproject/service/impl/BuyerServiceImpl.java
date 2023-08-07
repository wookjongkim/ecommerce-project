package com.example.ecommerceproject.service.impl;

import com.example.ecommerceproject.constant.Category;
import com.example.ecommerceproject.constant.ItemSellStatus;
import com.example.ecommerceproject.constant.OrderStatus;
import com.example.ecommerceproject.domain.dto.response.BuyerItemResponseDto;
import com.example.ecommerceproject.domain.dto.response.MemberInfoDto;
import com.example.ecommerceproject.domain.dto.request.OrderRequestDto;
import com.example.ecommerceproject.domain.dto.request.OrderRequestDto.ItemOrderDto;
import com.example.ecommerceproject.domain.dto.response.OrderResponseDto;
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
import com.example.ecommerceproject.repository.spec.ItemSpecification;
import com.example.ecommerceproject.service.BuyerService;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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

  private final SellerRevenueRepository sellerRevenueRepository;
  private final MemberRepository memberRepository;
  private final ItemRepository itemRepository;
  private final StockRepository stockRepository;
  private final OrderRepository orderRepository;

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

  @Override
  @Transactional
  public OrderResponseDto orderItems(Long buyerId, OrderRequestDto orderRequestDto) {
    List<Item> itemList = retrieveItemList(orderRequestDto);
    List<ItemOrderDto> itemOrders = orderRequestDto.getItemOrders();

    // 주문하려는 상품 중 SOLD_OUT or SELL_STOPPED 상태의 상품이 있는지 조회
    validateItemStatus(itemList);

    // 주문하려는 상품의 재고가 충분한지 확인
    validateStockAvailability(itemList, itemOrders);

    // 총 구매 금액 계산
    long totalPrice = calculateTotalPrice(itemList, itemOrders);

    // 구매자의 잔고가 충분한지 확인
    validateBuyerBalance(buyerId, totalPrice);

    // 구매 하고자 하는 상품의 재고를 감소시킴
    reduceStock(itemList, itemOrders);

    // 각 판매자의 대한 수익을 계산한 후, 이를 판매자의 계좌 반영
    increaseSellerRevenues(getSellersRevenue(itemList, itemOrders));

    // 주문 정보를 생성 후 저장
    Order order = saveOrder(buyerId, itemList, itemOrders, totalPrice);

    // Response 형식에 맞게 Convert
    return OrderResponseDto.of(order);
  }

  private List<Item> retrieveItemList(OrderRequestDto orderRequestDto) {
    List<Long> itemIds = orderRequestDto.getItemOrders().stream()
        .map(ItemOrderDto::getItemId)
        .collect(Collectors.toList());

    List<Item> items = itemRepository.findByIdIn(itemIds);

    //  In의 경우 결과를 반환하는 순서가 보장되지 않으므로 주문 순서대로 Item을 정렬,
    items.sort(Comparator.comparing(item -> itemIds.indexOf(item.getId())));

    return items;
  }

  private void validateItemStatus(List<Item> itemList) {
    if (isOrderStatusUnorderable(itemList)) {
      throw new BusinessException(ErrorCode.UNORDERABLE_ITEM_INCLUDED);
    }
  }

  private boolean isOrderStatusUnorderable(List<Item> itemList) {
    return itemList.stream()
        .anyMatch(item -> item.getSaleStatus() == ItemSellStatus.SOLD_OUT
            || item.getSaleStatus() == ItemSellStatus.SELL_STOPPED);
  }

  private void validateStockAvailability(List<Item> itemList, List<ItemOrderDto> itemOrders) {
    if (isOutOfStock(itemList, itemOrders)) {
      throw new BusinessException(ErrorCode.OUT_OF_STOCK_ITEM_INCLUDED);
    }
  }

  private boolean isOutOfStock(List<Item> itemList, List<ItemOrderDto> itemOrders) {
    return IntStream.range(0, itemList.size())
        .anyMatch(i -> {
          // 여기서 stock에 대해 조회(findByIdForUpdate)시 row-level에 락을 검
          Stock stock = stockRepository.findByIdForUpdate(itemList.get(i).getStock().getId())
              .orElseThrow(() -> new BusinessException(ErrorCode.STOCK_NOT_FOUND));

          return stock.getQuantity() < itemOrders.get(i).getQuantity();
        });
  }

  private long calculateTotalPrice(List<Item> itemList, List<ItemOrderDto> itemOrders) {
    return IntStream.range(0, itemList.size())
        .mapToLong(i -> itemList.get(i).getPrice() * itemOrders.get(i).getQuantity())
        .sum();
  }

  private void validateBuyerBalance(Long buyerId, long totalPrice) {
    // 이 과정에서 구매자의 계좌 조회시(findByMemberId) row-level의 락을 검
    BuyerBalance buyerBalance = buyerBalanceRepository.findByMemberId(buyerId)
        .orElseThrow(() -> new BusinessException(ErrorCode.BUYER_BALANCE_NOT_FOUND));

    if (totalPrice > buyerBalance.getBalance()) {
      throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE);
    }

    buyerBalance.setBalance(buyerBalance.getBalance() - totalPrice);
  }

  private HashMap<Long, Long> getSellersRevenue(List<Item> itemList,
      List<ItemOrderDto> itemOrders) {
    HashMap<Long, Long> map = new HashMap<>();

    IntStream.range(0, itemList.size())
        .forEach(i -> {
          Long sellerId = itemList.get(i).getSellerId();
          Long profit = (long) itemList.get(i).getPrice() * itemOrders.get(i).getQuantity();
          map.put(sellerId, map.getOrDefault(sellerId, 0L) + profit);
        });

    return map;
  }

  private void reduceStock(List<Item> itemList, List<ItemOrderDto> itemOrders) {
    IntStream.range(0, itemList.size())
        .forEach(i -> {
          Stock stock = itemList.get(i).getStock();
          int currentQuantity = stock.getQuantity();
          int orderQuantity = itemOrders.get(i).getQuantity();

          stock.setQuantity(currentQuantity - orderQuantity);
        });
  }

  private void increaseSellerRevenues(HashMap<Long, Long> sellerRevenues) {
    sellerRevenues.forEach((sellerId, profit) -> {
      // 판매자의 계좌 조회시(findByMemberId), row-level의 락을 검
      SellerRevenue sellerRevenue = sellerRevenueRepository.findByMemberId(sellerId)
          .orElseThrow(() -> new BusinessException(ErrorCode.SELLER_NOT_FOUND));

      sellerRevenue.setRevenue(sellerRevenue.getRevenue() + profit);
    });
  }

  private Order saveOrder(Long buyerId, List<Item> itemList, List<ItemOrderDto> itemOrders,
      long totalPrice) {

    // OrderItem은 현재 주문 시점의 가격과 이름을 저장하고 있는 테이블
    List<OrderItem> orderItemList = IntStream.range(0, itemList.size())
        .mapToObj(i -> OrderItem.of(itemList.get(i), itemOrders.get(i).getQuantity()))
        .collect(Collectors.toList());

    Order order = Order.builder()
        .buyerId(buyerId)
        .orderStatus(OrderStatus.COMPLETED)
        .totalPrice(totalPrice)
        .orderItems(orderItemList)
        .build();

    orderRepository.save(order);

    return order;
  }

  @Override
  @Transactional
  public String cancelOrder(Long buyerId, Long orderId) {

    Member member = memberRepository.findById(buyerId)
        .orElseThrow(() -> new BusinessException(ErrorCode.BUYER_NOT_FOUND));

    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

    // 이미 취소된 주문인지 검증
    validateOrderStatus(order);

    // 해당 주문 시 구매했던 상품들 조회
    List<OrderItem> orderItems = order.getOrderItems();

    // 주문 취소한 상품들의 재고를 올리고, 구매자의 잔고를 올리고, 판매자의 수익은 감소 시킴
    addStocks(orderItems);

    decreaseSellerRevenue(orderItems);

    increaseBuyerBalance(member, order);

    order.setOrderStatus(OrderStatus.CANCELED);

    return "주문 취소가 완료되었습니다 :)";
  }

  private void validateOrderStatus(Order order) {
    if (order.getOrderStatus() == OrderStatus.CANCELED) {
      // 이미 취소 된 주문인 경우
      throw new BusinessException(ErrorCode.ORDER_ALREADY_CANCELED);
    }
  }

  private void addStocks(List<OrderItem> orderItems) {

    orderItems.forEach(orderItem -> {
      Item item = itemRepository.findById(orderItem.getItemId())
          .orElseThrow(() -> new BusinessException(ErrorCode.ITEM_NOT_EXIST));

      // 재고에 대해 락을 검
      Stock stock = stockRepository.findByIdForUpdate(item.getStock().getId())
          .orElseThrow(() -> new BusinessException(ErrorCode.STOCK_NOT_FOUND));

      stock.setQuantity(stock.getQuantity() + orderItem.getQuantity());
    });
  }

  private void decreaseSellerRevenue(List<OrderItem> orderItems) {
    orderItems.forEach(orderItem -> {
      Item item = itemRepository.findById(orderItem.getItemId())
          .orElseThrow(() -> new BusinessException(ErrorCode.ITEM_NOT_EXIST));

      Member member = memberRepository.findById(item.getSellerId())
          .orElseThrow(() -> new BusinessException(ErrorCode.SELLER_NOT_FOUND));

      SellerRevenue sellerRevenue = sellerRevenueRepository.findByMemberId(member.getId())
          .orElseThrow(() -> new BusinessException(ErrorCode.SELLER_REVENUE_NOT_FOUND));

      sellerRevenue.setRevenue(sellerRevenue.getRevenue() - (orderItem.getPrice() * orderItem.getQuantity()));
    });
  }

  private void increaseBuyerBalance(Member member, Order order) {

    // 구매자 계좌에 락을 검
    BuyerBalance buyerBalance = buyerBalanceRepository.findByMemberId(member.getId())
        .orElseThrow(() -> new BusinessException(ErrorCode.BUYER_BALANCE_NOT_FOUND));

    buyerBalance.setBalance(buyerBalance.getBalance() + order.getTotalPrice());
  }

  @Override
  @Transactional(readOnly = true)
  public OrderResponseDto lookupOrder(Long buyerId, Long orderId) {
    Member member = memberRepository.findById(buyerId)
        .orElseThrow(() -> new BusinessException(ErrorCode.BUYER_NOT_FOUND));

    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

    if(order.getBuyerId() != buyerId){
      // 구매자가 본인이 주문한 것이 아닌 주문 정보를 조회하려는 경우 예외 발생
      throw new BusinessException(ErrorCode.UNAUTHORIZED_ORDER_ACCESS);
    }

    return OrderResponseDto.of(order);
  }
}
