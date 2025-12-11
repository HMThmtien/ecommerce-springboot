package com.example.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // chủ sở hữu
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String fullName;     // tên người nhận

    @Column(nullable = false)
    private String phone;        // sđt nhận hàng

    @Column(nullable = false)
    private String province;     // Tỉnh/Thành phố

    @Column(nullable = false)
    private String district;     // Quận/Huyện

    @Column(nullable = false)
    private String ward;         // Phường/Xã

    @Column(nullable = false)
    private String street;       // Địa chỉ chi tiết (số nhà, tên đường)

    @Column(name = "is_default", nullable = false)
    private boolean defaultAddress;  // địa chỉ mặc định

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;
}
