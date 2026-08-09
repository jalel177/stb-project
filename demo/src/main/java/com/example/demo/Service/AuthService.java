package com.example.demo.Service;

import com.example.demo.DTO.AuthRequest;
import com.example.demo.DTO.AuthResponse;
import com.example.demo.DTO.SignupRequest;
import com.example.demo.Entities.Account;
import com.example.demo.Entities.User;
import com.example.demo.Enum.AccountStatus;
import com.example.demo.Enum.Role;
import com.example.demo.Repository.AccountRepository;
import com.example.demo.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;

    public User registerUser(SignupRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.CUSTOMER);
        User savedUser = userRepository.save(user);

        Account account = new Account();
        account.setAccountNumber(generateAccountNumber());
        account.setOwnerName(savedUser.getUsername());
        account.setBalance(new BigDecimal("1000.00"));
        account.setStatus(AccountStatus.ACTIVE);
        account.setDailyLimit(new BigDecimal("100000"));
        account.setUser(savedUser);
        accountRepository.save(account);

        return savedUser;
    }

    private String generateAccountNumber() {
        int random = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(random);
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Account> accounts = accountRepository.findByUser_Username(user.getUsername());
        boolean hasClosedAccount = accounts.stream()
                .anyMatch(a -> a.getStatus() == AccountStatus.CLOSED);

        if (hasClosedAccount) {
            throw new RuntimeException("Your account has been closed. Please contact support.");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token, user.getRole(), user.getUsername());
    }
}