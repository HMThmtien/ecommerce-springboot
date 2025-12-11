package com.example.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private BigDecimal totalAmount;

    // Trạng thái xử lý đơn hàng
    // NEW, PROCESSING, SHIPPING, COMPLETED, CANCELLED
    @Column(name = "order_status")
    private String status;

    // Trạng thái thanh toán: PENDING, PAID, FAILED, REFUNDED
    @Column(name = "payment_status")
    private String paymentStatus;

    // Phương thức thanh toán: COD, BANK_TRANSFER, VNPAY, MOMO (mock)
    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "paid_at")
    private Instant paidAt;

    private Instant createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;

    @Column(name = "shipping_name")
    private String shippingName;

    @Column(name = "shipping_phone")
    private String shippingPhone;

    @Column(name = "shipping_address")
    private String shippingAddress;
}

