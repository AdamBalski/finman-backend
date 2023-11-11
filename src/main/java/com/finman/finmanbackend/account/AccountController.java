package com.finman.finmanbackend.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {
    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/get-accounts")
    public List<Account> getAccounts() {
        return accountService.getAccounts(getEmail());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create-account")
    public void createAccount(@RequestBody String accountName) {
        accountService.put(getEmail(), accountName);
    }

    private String getEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
