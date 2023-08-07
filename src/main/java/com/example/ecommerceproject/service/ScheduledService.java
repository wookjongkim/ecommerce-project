package com.example.ecommerceproject.service;

import com.example.ecommerceproject.constant.ItemSellStatus;
import com.example.ecommerceproject.domain.model.Item;
import com.example.ecommerceproject.repository.ItemRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduledService {

  private final ItemRepository itemRepository;

  @Scheduled(cron = "0 0 3 * * ?") // 매일 새벽 3시마다
  public void deleteItemNotSoldForThreeYears() {
    List<ItemSellStatus> statuses = Arrays.asList(ItemSellStatus.SOLD_OUT,
        ItemSellStatus.SELL_STOPPED);
    LocalDateTime threeYearsAgo = LocalDateTime.now().minusYears(3);

    List<Item> items = itemRepository.findAllBySaleStatusInAndUpdatedAtBefore(
        statuses, threeYearsAgo);

    // 대량의 데이터를 한번에 삭제하려면 DB에 무리가 갈수 있으므로 나누어 진행
    int batchSize = 100;

    int totalSize = items.size();

    for(int i = 0; i < totalSize; i += batchSize){
      int end = Math.min(i + batchSize, totalSize);
      List<Item> batch = items.subList(i, end);
      itemRepository.deleteAllInBatch(batch);
    }
  }
}
