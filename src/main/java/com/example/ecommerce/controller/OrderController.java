package com.example.ecommerce.controller;

import com.example.ecommerce.dto.OrderResponse;
import com.example.ecommerce.payload.ApiResponse;
import com.example.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // USER: checkout từ cart
    @PostMapping("/api/orders/checkout")
    public ApiResponse<OrderResponse> checkout() {
        OrderResponse res = orderService.checkoutFromCart();
        return ApiResponse.ok("Tạo đơn hàng thành công", res);
    }

    // USER: xem danh sách đơn hàng của chính mình
    @GetMapping("/api/orders/my")
    public ApiResponse<List<OrderResponse>> getMyOrders() {
        return ApiResponse.ok("Lấy danh sách đơn hàng thành công", orderService.getMyOrders());
    }

    // USER: xem chi tiết 1 đơn hàng của chính mình
    @GetMapping("/api/orders/my/{orderId}")
    public ApiResponse<OrderResponse> getMyOrderDetail(@PathVariable Long orderId) {
        return ApiResponse.ok("Lấy chi tiết đơn hàng thành công",
                orderService.getMyOrderById(orderId));
    }

    // ADMIN: xem tất cả order
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/admin/orders")
    public ApiResponse<List<OrderResponse>> getAllOrders() {
        return ApiResponse.ok("Lấy danh sách đơn hàng (admin) thành công",
                orderService.getAllOrdersForAdmin());
    }

    // ADMIN: xem chi tiết 1 order bất kỳ
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/admin/orders/{orderId}")
    public ApiResponse<OrderResponse> getOrderDetailAdmin(@PathVariable Long orderId) {
        return ApiResponse.ok("Lấy chi tiết đơn hàng (admin) thành công",
                orderService.getOrderDetailForAdmin(orderId));
    }

    // ADMIN: cập nhật trạng thái đơn hàng (ví dụ PENDING, PAID, CANCELLED...)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/api/admin/orders/{orderId}/status")
    public ApiResponse<Void> updateStatus(@PathVariable Long orderId,
                                          @RequestParam String status) {
        orderService.updateOrderStatus(orderId, status);
        return ApiResponse.ok("Cập nhật trạng thái đơn hàng thành công", null);
    }
}
