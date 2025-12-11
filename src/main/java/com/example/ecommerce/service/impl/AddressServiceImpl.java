package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.AddressRequest;
import com.example.ecommerce.dto.AddressResponse;
import com.example.ecommerce.entity.Address;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.AddressRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    private AddressResponse mapToResponse(Address a) {
        AddressResponse res = new AddressResponse();
        res.setId(a.getId());
        res.setFullName(a.getFullName());
        res.setPhone(a.getPhone());
        res.setProvince(a.getProvince());
        res.setDistrict(a.getDistrict());
        res.setWard(a.getWard());
        res.setStreet(a.getStreet());
        res.setDefaultAddress(a.isDefaultAddress());
        return res;
    }

    @Override
    public AddressResponse create(AddressRequest request) {
        User user = getCurrentUser();

        Address address = Address.builder()
                .user(user)
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .province(request.getProvince())
                .district(request.getDistrict())
                .ward(request.getWard())
                .street(request.getStreet())
                .defaultAddress(false) // tạm thời false, xử lý bên dưới
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        // nếu user chưa có địa chỉ nào, auto set default
        List<Address> existing = addressRepository.findByUserOrderByCreatedAtDesc(user);
        if (existing.isEmpty()) {
            address.setDefaultAddress(true);
        } else if (Boolean.TRUE.equals(request.getDefaultAddress())) {
            // nếu user tick defaultAddress = true khi tạo -> clear default cũ
            existing.forEach(a -> {
                if (a.isDefaultAddress()) {
                    a.setDefaultAddress(false);
                    addressRepository.save(a);
                }
            });
            address.setDefaultAddress(true);
        }

        Address saved = addressRepository.save(address);
        return mapToResponse(saved);
    }

    @Override
    public AddressResponse update(Long id, AddressRequest request) {
        User user = getCurrentUser();

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Bạn không thể sửa địa chỉ của người khác");
        }

        address.setFullName(request.getFullName());
        address.setPhone(request.getPhone());
        address.setProvince(request.getProvince());
        address.setDistrict(request.getDistrict());
        address.setWard(request.getWard());
        address.setStreet(request.getStreet());
        address.setUpdatedAt(Instant.now());

        // xử lý default
        if (Boolean.TRUE.equals(request.getDefaultAddress())) {
            // clear những default khác
            addressRepository.findByUserOrderByCreatedAtDesc(user)
                    .forEach(a -> {
                        if (a.isDefaultAddress() && !a.getId().equals(address.getId())) {
                            a.setDefaultAddress(false);
                            addressRepository.save(a);
                        }
                    });
            address.setDefaultAddress(true);
        }

        Address saved = addressRepository.save(address);
        return mapToResponse(saved);
    }

    @Override
    public void delete(Long id) {
        User user = getCurrentUser();

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Bạn không thể xoá địa chỉ của người khác");
        }

        boolean wasDefault = address.isDefaultAddress();
        addressRepository.delete(address);

        // nếu xoá địa chỉ default -> set default cho 1 địa chỉ khác (nếu còn)
        if (wasDefault) {
            List<Address> remain = addressRepository.findByUserOrderByCreatedAtDesc(user);
            if (!remain.isEmpty()) {
                Address first = remain.get(0);
                first.setDefaultAddress(true);
                addressRepository.save(first);
            }
        }
    }

    @Override
    public List<AddressResponse> getMyAddresses() {
        User user = getCurrentUser();
        return addressRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public AddressResponse getMyAddressById(Long id) {
        User user = getCurrentUser();
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Bạn không thể xem địa chỉ của người khác");
        }
        return mapToResponse(address);
    }

    @Override
    public AddressResponse setDefault(Long id) {
        User user = getCurrentUser();
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Bạn không thể đổi default cho địa chỉ của người khác");
        }

        // clear default cũ
        addressRepository.findByUserOrderByCreatedAtDesc(user)
                .forEach(a -> {
                    if (a.isDefaultAddress()) {
                        a.setDefaultAddress(false);
                        addressRepository.save(a);
                    }
                });

        address.setDefaultAddress(true);
        address.setUpdatedAt(Instant.now());
        Address saved = addressRepository.save(address);
        return mapToResponse(saved);
    }

    @Override
    public AddressResponse getMyDefaultAddress() {
        User user = getCurrentUser();
        Address address = addressRepository.findByUserAndDefaultAddressTrue(user)
                .orElseThrow(() -> new BadRequestException("Bạn chưa thiết lập địa chỉ mặc định"));
        return mapToResponse(address);
    }
}
