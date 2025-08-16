package com.clone.paypal.transaction_service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    @Autowired private TransactionService transactionService;
    @Autowired private TransactionRepository transactionRepository;

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody TransactionRequest request) {
        // Pass the description from the request to the service method
        Transaction newTransaction = transactionService.performTransaction(
                request.getSenderId(),
                request.getRecipientEmail(),
                request.getAmount(),
                request.getDescription()
        );
        return ResponseEntity.ok(newTransaction);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Transaction>> getTransactionsByUserId(@PathVariable Long userId) {
        List<Transaction> transactions = transactionRepository.findBySenderIdOrRecipientId(userId, userId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        return transactionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/user/{userId}")
    @Transactional
    public ResponseEntity<Void> deleteTransactionsByUserId(@PathVariable Long userId) {
        transactionRepository.deleteBySenderIdOrRecipientId(userId, userId);
        return ResponseEntity.ok().build();
    }
}