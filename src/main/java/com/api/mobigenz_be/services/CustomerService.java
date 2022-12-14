package com.api.mobigenz_be.services;

import com.api.mobigenz_be.DTOs.AccountDTO;
import com.api.mobigenz_be.DTOs.CustomerDTO;
import com.api.mobigenz_be.DTOs.PageDTO;
import com.api.mobigenz_be.entities.Account;
import com.api.mobigenz_be.entities.Customer;
import com.api.mobigenz_be.entities.Employee;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.api.mobigenz_be.repositories.CustomerRepository;


import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private ModelMapper modelMapper;

    public PageDTO<CustomerDTO> getAll(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        Page<Customer> page = this.customerRepo.getAll(pageable);
        List<CustomerDTO> customerDTOList = page.stream().map(u -> this.modelMapper.map(u, CustomerDTO.class)).collect(Collectors.toList());
        return new PageDTO<CustomerDTO>(
                page.getTotalPages(),
                page.getTotalElements(),
                page.getNumber(),
                page.getSize(),
                customerDTOList,
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious()
        );
    }

    public Page<Customer> findByKey(Pageable pageable, String valueSearch) {
        return this.customerRepo.findByKey(pageable,valueSearch);
    }


    public CustomerDTO create(Customer customer) throws SQLException {
        customer.setCustomerType(0);
        customer.setStatus(1);
        customer.setCtime(LocalDate.now());
        this.customerRepo.saveAndFlush(customer);
        return this.modelMapper.map(customer, CustomerDTO.class);
    }

//    public CustomerDTO add(Customer customer) throws Exception {
//        try {
//            this.customerRepo.save(customer);
//            return this.modelMapper.map(customer, CustomerDTO.class);
//        } catch (Exception exception) {
//            throw new Exception("Create Customer False");
//        }
//    }


    public CustomerDTO update(Customer customer) {
        customer = this.customerRepo.save(customer);
        return this.modelMapper.map(customer, CustomerDTO.class);
    }

    public List<Customer> findByCustomerName(String customerName){
        return this.customerRepo.findByCustomerName(customerName);

    }

    public Customer findByEmail(String email){
        return this.customerRepo.findByEmail(email);

    }

    public CustomerDTO delete(Customer customer) {
        customer.setStatus(0);
        this.customerRepo.save(customer);
        return this.modelMapper.map(customer, CustomerDTO.class);
    }

    public Customer findCusById(Integer id){
        return this.customerRepo.findCusById(id);
    }


    public List<CustomerDTO> getAllCus() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Customer> page = this.customerRepo.getAll(pageable);


        List<CustomerDTO> customerDTOList = page.stream().map(u ->
                this.modelMapper.map(u, CustomerDTO.class)).collect(Collectors.toList());
        return customerDTOList;
    }

    public List<CustomerDTO> searchByAll(String search) {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Customer> page = this.customerRepo.findByAll(search, pageable);


        List<CustomerDTO> customerDTOList = page.stream().map(u -> this.modelMapper.map(u, CustomerDTO.class)).collect(Collectors.toList());
        return customerDTOList;
    }

    public CustomerDTO getById(Integer id) throws Exception {
        Optional<Customer> customer = this.customerRepo.findById(id);
        if (customer.isPresent()) {
            return this.modelMapper.map(customer.get(), CustomerDTO.class);
        }
        throw new Exception("Not found category by id");
    }


    public Customer findByAccountId(Integer accountId){
        return this.customerRepo.findByAccountId(accountId);
    }


    public Customer getCustomerByCustomerID(Integer idCustomer){
        return this.customerRepo.getCustomerByCustomerID(idCustomer);
    }

    public Page<Customer> findByStatus(Pageable pageable, Integer status) {
        return this.customerRepo.findCustomerByStatus(pageable, status);
    }

}