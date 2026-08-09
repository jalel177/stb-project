package com.example.demo.Service;



import com.example.demo.DTO.UserDetails;
import com.example.demo.Entities.User;
import com.example.demo.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDetails getOwnProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserDetails.AccountSummaryDTO> accounts = user.getAccounts().stream()
                .map(a -> new UserDetails.AccountSummaryDTO(
                        a.getId(), a.getAccountNumber(), a.getBalance(), a.getStatus().name()))
                .toList();

        return new UserDetails(user.getId(), user.getUsername(), user.getRole(), accounts);
    }
}
