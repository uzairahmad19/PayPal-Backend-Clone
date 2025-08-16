package com.clone.paypal.user_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        restTemplate.postForObject("http://WALLET-SERVICE/api/wallets",
                new WalletCreationRequest(savedUser.getId()),
                Map.class);

        return ResponseEntity.ok(savedUser);
    }

    public static class LoginRequest {
        public String email;
        public String password;
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(loginRequest.password, user.getPassword())) {
                return ResponseEntity.ok(new LoginResponse("dummy-jwt-token-for-" + user.getId(), user.getId(), user.getFullName()));
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        return userOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<User> getAuthenticatedUser(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer dummy-jwt-token-for-")) {
            try {
                Long userId = Long.parseLong(authorizationHeader.substring("Bearer dummy-jwt-token-for-".length()));
                Optional<User> userOptional = userRepository.findById(userId);
                return userOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String q) {
        List<User> users = userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(q, q, q);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/analytics/{id}")
    public ResponseEntity<?> getUserAnalytics(@PathVariable Long id) {
        try {
            String transactionServiceUrl = "http://TRANSACTION-SERVICE/api/transactions/user/" + id;
            ResponseEntity<Map> transactionResponse = restTemplate.getForEntity(transactionServiceUrl, Map.class);

            String walletServiceUrl = "http://WALLET-SERVICE/api/wallets/user/" + id;
            ResponseEntity<Map> walletResponse = restTemplate.getForEntity(walletServiceUrl, Map.class);

            Map<String, Object> analytics = Map.of(
                    "transactions", transactionResponse.getBody() != null ? transactionResponse.getBody() : Map.of(),
                    "wallet", walletResponse.getBody() != null ? walletResponse.getBody() : Map.of(),
                    "summary", Map.of(
                            "totalTransactions", 0,
                            "totalSpent", 0,
                            "totalReceived", 0
                    )
            );

            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch user analytics"));
        }
    }

    public static class PasswordChangeRequest {
        public String currentPassword;
        public String newPassword;
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @RequestBody PasswordChangeRequest passwordChangeRequest) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(passwordChangeRequest.currentPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(passwordChangeRequest.newPassword));
                userRepository.save(user);
                return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid current password");
            }
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            // 1. Delete wallet
            restTemplate.delete("http://WALLET-SERVICE/api/wallets/user/" + id);
            // 2. Delete transactions
            restTemplate.delete("http://TRANSACTION-SERVICE/api/transactions/user/" + id);
            // 3. Delete notifications
            restTemplate.delete("http://NOTIFICATION-SERVICE/api/notifications/user/" + id);
            // 4. Delete user
            userRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to delete user"));
        }
    }
}