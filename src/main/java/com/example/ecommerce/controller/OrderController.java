package com.example.ecommerce.controller;

import com.example.ecommerce.dto.OrderResponse;
import com.example.ecommerce.dto.PaymentRequest;
import com.example.ecommerce.payload.ApiResponse;
import com.example.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")   // ✅ base path cho tất cả order API
public class OrderController {

    private final OrderService orderService;

    // USER: checkout từ cart
    @PostMapping("/checkout")
    public ApiResponse<OrderResponse> checkout(@RequestParam(required = false) Long addressId) {
        OrderResponse res = orderService.checkoutFromCart(addressId);
        return ApiResponse.ok("Tạo đơn hàng thành công", res);
    }

    // USER thanh toán đơn
    @PostMapping("/{orderId}/pay")
    public ApiResponse<OrderResponse> payOrder(@PathVariable Long orderId,
                                               @RequestBody @Valid PaymentRequest request) {
        OrderResponse res = orderService.payOrder(orderId, request);
        return ApiResponse.ok("Thanh toán đơn hàng thành công", res);
    }

    // USER: xem danh sách đơn hàng của chính mình
    @GetMapping("/my")
    public ApiResponse<List<OrderResponse>> getMyOrders() {
        return ApiResponse.ok("Lấy danh sách đơn hàng thành công", orderService.getMyOrders());
    }

    // USER: xem chi tiết 1 đơn hàng của chính mình
    @GetMapping("/my/{orderId}")
    public ApiResponse<OrderResponse> getMyOrderDetail(@PathVariable Long orderId) {
        return ApiResponse.ok("Lấy chi tiết đơn hàng thành công",
                orderService.getMyOrderById(orderId));
    }

    // ADMIN: xem tất cả order
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ApiResponse<List<OrderResponse>> getAllOrders() {
        return ApiResponse.ok("Lấy danh sách đơn hàng (admin) thành công",
                orderService.getAllOrdersForAdmin());
    }

    // ADMIN: xem chi tiết 1 order bất kỳ
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/{orderId}")
    public ApiResponse<OrderResponse> getOrderDetailAdmin(@PathVariable Long orderId) {
        return ApiResponse.ok("Lấy chi tiết đơn hàng (admin) thành công",
                orderService.getOrderDetailForAdmin(orderId));
    }

    // ADMIN: cập nhật trạng thái đơn hàng
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{orderId}/status")
    public ApiResponse<Void> updateStatus(@PathVariable Long orderId,
                                          @RequestParam String status) {
        orderService.updateOrderStatus(orderId, status);
        return ApiResponse.ok("Cập nhật trạng thái đơn hàng thành công", null);
    }
}

