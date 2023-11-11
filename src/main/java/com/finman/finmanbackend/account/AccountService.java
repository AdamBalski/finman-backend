package com.finman.finmanbackend.account;

import com.finman.finmanbackend.user.User;
import com.finman.finmanbackend.user.UserRepository;
import com.finman.finmanbackend.util.validator.HttpValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AccountService {
    AccountRepository accountRepository;
    UserRepository userRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }
    public List<Account> getAccounts(String email) {
        return accountRepository.findByUserEmail(email);
    }

    public void put(String email, String accountName) {
        if(accountName.length() > 100) {
            throw new HttpValidationException("ACCOUNT_NAME_TOO_LONG");
        }
        User user = userRepository
                .findOneByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, null, null));

        accountRepository.save(new Account(null, user, accountName));
    }
}
