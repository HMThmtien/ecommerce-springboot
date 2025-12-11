package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.OrderItemResponse;
import com.example.ecommerce.dto.OrderResponse;
import com.example.ecommerce.entity.*;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.*;
import com.example.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;

    // Lấy user hiện tại từ SecurityContext
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    private OrderResponse mapToResponse(Order order) {
        OrderResponse res = new OrderResponse();
        res.setId(order.getId());
        res.setStatus(order.getStatus());
        res.setTotalAmount(order.getTotalAmount());
        res.setCreatedAt(order.getCreatedAt());

        List<OrderItemResponse> itemResponses = new ArrayList<>();
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                OrderItemResponse ir = new OrderItemResponse();
                ir.setProductId(item.getProduct().getId());
                ir.setProductName(item.getProduct().getName());
                ir.setQuantity(item.getQuantity());
                ir.setPrice(item.getPrice());
                itemResponses.add(ir);
            }
        }
        res.setItems(itemResponses);
        return res;
    }

    @Override
    @Transactional
    public OrderResponse checkoutFromCart(Long addressId) {
        User user = getCurrentUser();
        List<CartItem> cartItems = cartItemRepository.findByUser(user);

        if (cartItems.isEmpty()) {
            throw new BadRequestException("Giỏ hàng trống, không thể checkout");
        }

        Address address;
        if (addressId != null) {
            address = addressRepository.findById(addressId)
                    .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
            if (!address.getUser().getId().equals(user.getId())) {
                throw new BadRequestException("Bạn không thể sử dụng địa chỉ của người khác");
            }
        } else {
            address = addressRepository.findByUserAndDefaultAddressTrue(user)
                    .orElseThrow(() -> new BadRequestException("Bạn chưa thiết lập địa chỉ mặc định"));
        }

        // Kiểm tra tồn kho & tính tổng tiền
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem ci : cartItems) {
            Product p = ci.getProduct();

            if (ci.getQuantity() > p.getStock()) {
                throw new BadRequestException("Sản phẩm '" + p.getName() + "' không đủ tồn kho");
            }

            BigDecimal line = p.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity()));
            total = total.add(line);
        }

        String fullAddress = address.getStreet() + ", " +
                address.getWard() + ", " +
                address.getDistrict() + ", " +
                address.getProvince();

        // Tạo Order
        Order order = Order.builder()
                .user(user)
                .totalAmount(total)
                // trạng thái xử lý đơn hàng ban đầu
                .status("NEW")
                // trạng thái thanh toán ban đầu
                .paymentStatus("PENDING")
                // mặc định COD (thanh toán khi nhận hàng), sẽ cho đổi sau
                .paymentMethod("COD")
                .createdAt(Instant.now())
                .shippingName(address.getFullName())
                .shippingPhone(address.getPhone())
                .shippingAddress(fullAddress)
                .build();


        order = orderRepository.save(order);

        // Tạo OrderItems + trừ tồn kho
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem ci : cartItems) {
            Product p = ci.getProduct();

            // trừ stock
            p.setStock(p.getStock() - ci.getQuantity());
            productRepository.save(p);

            OrderItem oi = OrderItem.builder()
                    .order(order)
                    .product(p)
                    .quantity(ci.getQuantity())
                    .price(p.getPrice())
                    .build();
            orderItems.add(oi);
        }

        orderItemRepository.saveAll(orderItems);
        order.setItems(orderItems);

        // Xóa giỏ hàng sau khi checkout
        cartItemRepository.deleteByUser(user);

        return mapToResponse(order);
    }



    @Override
    public List<OrderResponse> getMyOrders() {
        User user = getCurrentUser();
        List<Order> orders = orderRepository.findByUserOrderByCreatedAtDesc(user);
        return orders.stream().map(this::mapToResponse).toList();
    }

    @Override
    public OrderResponse getMyOrderById(Long orderId) {
        User user = getCurrentUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Không thể xem đơn hàng của người khác");
        }

        return mapToResponse(order);
    }

    @Override
    public List<OrderResponse> getAllOrdersForAdmin() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public OrderResponse getOrderDetailForAdmin(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        return mapToResponse(order);
    }

    @Override
    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        order.setStatus(status);
        orderRepository.save(order);
    }
}
