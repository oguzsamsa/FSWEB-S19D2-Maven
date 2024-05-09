package com.workintech.s18d4.controller;

import com.workintech.s18d4.dto.AccountResponse;
import com.workintech.s18d4.dto.CustomerResponse;
import com.workintech.s18d4.entity.Account;
import com.workintech.s18d4.entity.Customer;
import com.workintech.s18d4.service.AccountService;
import com.workintech.s18d4.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/account")
@AllArgsConstructor
public class AccountController {

    private AccountService accountService;
    private CustomerService customerService;

    @GetMapping
    public List<Account> findAll(){
        return accountService.findAll();
    }

    @GetMapping("/{id}")
    public Account findById(@PathVariable long id){
        return accountService.find(id);
    }

    @PostMapping("/{customerId}")
    public AccountResponse save(@PathVariable long customerId, @RequestBody Account account){
        Customer customer = customerService.find(customerId);
        if (customer!=null){
            customer.addAccount(account);
            account.setCustomer(customer);
            accountService.save(account);
        } else {
            throw new RuntimeException("Customer is not found");
        }

        return new AccountResponse(account.getId(), account.getAccountName(), account.getMoneyAmount(), new CustomerResponse(customer.getId(), customer.getEmail(), customer.getSalary()));


    }

    @PutMapping("/{customerId}")
    public AccountResponse update(@PathVariable long customerId, @RequestBody Account account){
        Customer customer = customerService.find(customerId);
        Account toBeUpdated = null;
        for (Account account1: customer.getAccounts()){
            if (account.getId() == account1.getId()){
                toBeUpdated = account1;
            }
        }

        if(toBeUpdated == null){
            throw new RuntimeException("Account (" + account.getId() + ") not found for the customer: " + customerId);
        }
        int indexOfFound = customer.getAccounts().indexOf(toBeUpdated);
        customer.getAccounts().set(indexOfFound, account);
        account.setCustomer(customer);
        accountService.save(account);

        return new AccountResponse(account.getId(), account.getAccountName(), account.getMoneyAmount(), new CustomerResponse(customer.getId(), customer.getEmail(), customer.getSalary()));
    }

    @DeleteMapping("/{id}")
    public AccountResponse remove(@PathVariable long id){
        Account account = accountService.find(id);
        if (account == null) {
            throw new RuntimeException("no account found");
        }

        accountService.delete(id);
        return new AccountResponse(account.getId(), account.getAccountName(), account.getMoneyAmount(), new CustomerResponse(account.getCustomer().getId(), account.getCustomer().getEmail(), account.getCustomer().getSalary()));

    }
}
