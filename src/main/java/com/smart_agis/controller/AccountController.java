package com.smart_agis.controller;

import com.smart_agis.entity.Account;
import com.smart_agis.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountRepository accountRepository;

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        if(account.getBalance() == null ){
            account.setBalance(BigDecimal.ZERO);
        }
        account.setStatus("ACTIVE");
        return ResponseEntity.ok(accountRepository.save(account));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Account>> getAccountByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(accountRepository.findByCustomerId(customerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {

        return accountRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //Expose update balance API -- debit and credit

    @PutMapping("/{accountId}/debit")
    public ResponseEntity<Account> debit(@PathVariable Long accountId, @RequestParam BigDecimal balance){

        Account account = accountRepository.findById(accountId).orElseThrow();
        account.setId(accountId);
        account.setBalance(account.getBalance().subtract(balance));

        return ResponseEntity.ok(accountRepository.save(account));
    }

    public ResponseEntity<Account> credit(@PathVariable Long accountId, @RequestParam BigDecimal balance){
        Account account = accountRepository.findById(accountId).orElseThrow();
        account.setBalance(account.getBalance().add(balance));
        account.setId(accountId);
        return ResponseEntity.ok(accountRepository.save(account));
    }
}
