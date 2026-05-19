package com.example.ecommerce.config;

import com.example.ecommerce.entity.Role;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.RoleRepository;
import com.example.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Seed roles
        Role roleUser = roleRepository.findByName("ROLE_USER").orElseGet(() -> {
            log.info("Creating ROLE_USER...");
            return roleRepository.save(Role.builder().name("ROLE_USER").build());
        });

        Role roleAdmin = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> {
            log.info("Creating ROLE_ADMIN...");
            return roleRepository.save(Role.builder().name("ROLE_ADMIN").build());
        });

        // Seed admin account if not exists
        if (!userRepository.existsByEmail("admin@example.com")) {
            log.info("Creating default admin account: admin@example.com / admin123");
            User admin = User.builder()
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("Administrator")
                    .enabled(true)
                    .roles(Set.of(roleUser, roleAdmin))
                    .build();
            userRepository.save(admin);
        }
    }
}
