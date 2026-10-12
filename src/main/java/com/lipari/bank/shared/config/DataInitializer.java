package com.lipari.bank.shared.config;

import com.lipari.bank.auth.BankUserRepository;
import com.lipari.bank.auth.RoleRepository;
import com.lipari.bank.auth.model.BankUser;
import com.lipari.bank.auth.model.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final BankUserRepository bankUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public ApplicationRunner initData() {
        return args -> {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_ADMIN")));
            Role advisorRole = roleRepository.findByName("ROLE_ADVISOR")
                    .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_ADVISOR")));
            Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
                    .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_CUSTOMER")));

            if (!bankUserRepository.existsByUsername("admin")) {
                BankUser admin = new BankUser();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setEnabled(true);
                admin.setRoles(Set.of(adminRole));
                bankUserRepository.save(admin);
                log.info("Created default admin user");
            }

            if (!bankUserRepository.existsByUsername("advisor")) {
                BankUser advisor = new BankUser();
                advisor.setUsername("advisor");
                advisor.setPassword(passwordEncoder.encode("advisor123"));
                advisor.setEnabled(true);
                advisor.setRoles(Set.of(advisorRole));
                bankUserRepository.save(advisor);
                log.info("Created default advisor user");
            }

            if (!bankUserRepository.existsByUsername("customer")) {
                BankUser customer = new BankUser();
                customer.setUsername("customer");
                customer.setPassword(passwordEncoder.encode("customer123"));
                customer.setEnabled(true);
                customer.setRoles(Set.of(customerRole));
                bankUserRepository.save(customer);
                log.info("Created default customer user");
            }
        };
    }
}
