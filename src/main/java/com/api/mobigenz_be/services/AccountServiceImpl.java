package com.api.mobigenz_be.services;

import com.api.mobigenz_be.DTOs.AccountDTO;
import com.api.mobigenz_be.DTOs.CustomerDTO;
import com.api.mobigenz_be.DTOs.PageDTO;
import com.api.mobigenz_be.entities.Account;
import com.api.mobigenz_be.entities.Customer;
import com.api.mobigenz_be.repositories.AccountRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ModelMapper modelMapper;

    public PageDTO<AccountDTO> getAll(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        Page<Account> page = this.accountRepository.getAllById(pageable);
        List<AccountDTO> accountDTOList = page.stream().map(u -> this.modelMapper.map(u, AccountDTO.class)).collect(Collectors.toList());
        return new PageDTO<AccountDTO>(
                page.getTotalPages(),
                page.getTotalElements(),
                page.getNumber(),
                page.getSize(),
                accountDTOList,
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious()
        );
    }

    @Override
    @Transactional
    public AccountDTO add(Account account) {
        List<Account> lstAcc = this.accountRepository.findAll();
        for(Account x: lstAcc){
            if (x.getEmail() == account.getEmail()){
                return null;
            }
        }
        account.setStatus(1);
        account.setCtime(LocalDateTime.now());
        this.accountRepository.save(account);
        return new AccountDTO();
//        return this.modelMapper.map(account, AccountDTO.class);
    }



    @Override
    public AccountDTO update(Account account) {
        account = this.accountRepository.save(account);
        return this.modelMapper.map(account, AccountDTO.class);
    }

    @Override
    public AccountDTO delete(Account account) {
        account.setStatus(0);
        this.accountRepository.save(account);
        return  this.modelMapper.map(account, AccountDTO.class);
    }

    @Override
    public Account findById(Integer id) {
        return this.accountRepository.findAccountById(id);
    }

    @Override
    public Account findByEmail(String email) {
        return this.accountRepository.findAccountByEmail(email);
    }

    public List<AccountDTO> getAllAcc() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Account> page = this.accountRepository.getAllById(pageable);


        List<AccountDTO> accountDTOList = page.stream().map(u ->
                this.modelMapper.map(u, AccountDTO.class)).collect(Collectors.toList());
        return accountDTOList;
    }

    @Override
    public Page<Account> findByKey(Pageable pageable, String valueSearch) {
        return this.accountRepository.findByKey(pageable,valueSearch);
    }

    @Override
    public Optional<Account>  getAccountLogin(String email, String password) {
        return this.accountRepository.getAccountLogin(email, password);
    }

    @Override
    public Page<Account> findByStatus(Pageable pageable, Integer status) {
        return this.accountRepository.findAccountByStatus(pageable, status);
    }

    @Override
    public Account findAccountByEmailorPhone(String email, String phoneNumber) {
        return this.accountRepository.findAccountByEmailorPhone(email,phoneNumber);
    }

}
