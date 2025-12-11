package com.example.ecommerce.service;

import com.example.ecommerce.dto.AddressRequest;
import com.example.ecommerce.dto.AddressResponse;

import java.util.List;

public interface AddressService {

    AddressResponse create(AddressRequest request);

    AddressResponse update(Long id, AddressRequest request);

    void delete(Long id);

    List<AddressResponse> getMyAddresses();

    AddressResponse getMyAddressById(Long id);

    AddressResponse setDefault(Long id);

    AddressResponse getMyDefaultAddress();
}
