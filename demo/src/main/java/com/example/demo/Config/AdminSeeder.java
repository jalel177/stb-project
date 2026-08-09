package com.example.demo.Config;

import com.example.demo.Entities.User;
import com.example.demo.Entities.Account;
import com.example.demo.Enum.Role;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.findByUsername("admin").isPresent()) {
            log.info("Admin account already exists, skipping seed");
            return;
        }

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        Account account = new Account();
        account.setAccountNumber("100000");
        account.setOwnerName("admin");
        account.setBalance(new BigDecimal("1000"));
        account.setDailyLimit(new BigDecimal("100000"));
        account.setUser(admin);
        accountRepository.save(account);

        log.info("Seeded default admin account (username: admin)");
    }
}