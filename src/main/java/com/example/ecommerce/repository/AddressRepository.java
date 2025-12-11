package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Address;
import com.example.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByUserOrderByCreatedAtDesc(User user);

    Optional<Address> findByUserAndDefaultAddressTrue(User user);

}
