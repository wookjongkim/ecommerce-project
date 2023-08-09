package com.example.ecommerceproject.domain.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyerBalance {

  @Id
  @Column(name = "balance_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 연관관계 따로 맺지 않았음
  private Long memberId;

  private Long balance;
}
