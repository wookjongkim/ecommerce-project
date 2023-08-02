package com.example.ecommerceproject.domain.model;

import com.example.ecommerceproject.constant.OrderStatus;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseTimeEntity{

  @Id
  @Column(name = "order_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long buyerId;

  private Long totalPrice;

  private OrderStatus orderStatus;

  @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
  @JoinColumn(name = "order_id")
  private List<OrderItem> orderItems;
}
