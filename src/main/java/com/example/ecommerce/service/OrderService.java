package com.example.ecommerce.service;

import com.example.ecommerce.dto.OrderResponse;
import com.example.ecommerce.dto.PaymentRequest;

import java.util.List;

public interface OrderService {

    OrderResponse checkoutFromCart(Long addressId);

    List<OrderResponse> getMyOrders();

    OrderResponse getMyOrderById(Long orderId);

    List<OrderResponse> getAllOrdersForAdmin();

    OrderResponse getOrderDetailForAdmin(Long orderId);

    void updateOrderStatus(Long orderId, String status);

    OrderResponse payOrder(Long orderId, PaymentRequest request);

}
