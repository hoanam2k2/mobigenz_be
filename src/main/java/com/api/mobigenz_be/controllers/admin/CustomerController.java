package com.api.mobigenz_be.controllers.admin;

import com.api.mobigenz_be.DTOs.*;
import com.api.mobigenz_be.entities.Account;
import com.api.mobigenz_be.entities.Customer;
import com.api.mobigenz_be.entities.ResponseObject;
import com.api.mobigenz_be.entities.Role;
import com.api.mobigenz_be.repositories.AccountRepository;
import com.api.mobigenz_be.repositories.CustomerRepository;
import com.api.mobigenz_be.services.AccountService;
import com.api.mobigenz_be.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.api.mobigenz_be.services.CustomerService;

import static org.springframework.http.HttpStatus.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CustomerRepository customerRepository;


    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("customers/getAll")
    public ResponseEntity<ResponseDTO> getPageCustomers(
            @RequestParam(value = "offset", defaultValue = "") int offset,
            @RequestParam(value = "limit", defaultValue = "") int limit
    ) {
        PageDTO<CustomerDTO> items = this.customerService.getAll(offset, limit);
        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .status(OK)
                        .data(Map.of("customers", items))
                        .statusCode(OK.value())
                        .timeStamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping("customers/findByStatus")
    public ResponseEntity<ResponseDTO> findByStatus(
            @RequestParam(value = "offset", defaultValue = "")  int offset
            ,@RequestParam(value = "limit",defaultValue = "") int limit
            ,@RequestParam(value = "status",defaultValue = "") int status) {
        try {
            offset = offset < 0 ? 0 : offset;
            Pageable pageable;

            List<Sort.Order> orders = new ArrayList<>();
            pageable = PageRequest.of(offset, limit, Sort.by("status"));
            Page<Customer> pageCustomer = this.customerService.findByStatus(pageable, status);
            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .status(OK)
                            .data(Map.of("customer", pageCustomer))
                            .statusCode(OK.value())
                            .timeStamp(LocalDateTime.now())
                            .build()
            );
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }



    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }
        return Sort.Direction.ASC;
    }

    @PutMapping("customers/findByKey")
    public ResponseEntity<ResponseDTO> findByKey(
            @RequestParam(value = "offset", defaultValue = "") int offset
            , @RequestParam(value = "limit", defaultValue = "") int limit
            , @RequestBody SearchDTO searchDTO) {
        try {
            offset = offset < 0 ? 0 : offset;
            Pageable pageable;

            List<Sort.Order> orders = new ArrayList<>();
            List<ListSortDTO> listSortDTO = searchDTO.getListSortDTO();
            pageable = PageRequest.of(offset, limit, Sort.by("id"));
            Page<Customer> pageCustomer = this.customerService.findByKey(pageable, searchDTO.getValueSearch());
            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .status(OK)
                            .data(Map.of("customers", pageCustomer))
                            .statusCode(OK.value())
                            .timeStamp(LocalDateTime.now())
                            .build()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("customers/getCustomerByAccountId")
    public ResponseEntity<ResponseDTO> getByAccountId(@RequestParam(value = "accountId") Integer accountId) {
        Customer customer = this.customerService.findByAccountId(accountId);
        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .status(OK)
                        .data(Map.of("customers", customer))
                        .statusCode(OK.value())
                        .timeStamp(LocalDateTime.now())
                        .build()
        );
    }


    @GetMapping("customers/customerName")
    public ResponseEntity<ResponseDTO> getByCustomerName(@RequestParam(value = "customerName") String customerName) {
        List<Customer> customer = this.customerService.findByCustomerName(customerName);
        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .status(OK)
                        .data(Map.of("customers", customer))
                        .statusCode(OK.value())
                        .timeStamp(LocalDateTime.now())
                        .build()
        );
    }


    @GetMapping("customers/email")
    public ResponseEntity<ResponseDTO> getByEmail(@RequestParam(value = "email") String email) {
        Customer customer = this.customerService.findByEmail(email);
        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .status(OK)
                        .data(Map.of("customers", customer))
                        .statusCode(OK.value())
                        .timeStamp(LocalDateTime.now())
                        .build()
        );
    }


    @PostMapping("customers")
    public ResponseEntity<ResponseObject> insertCustomer(@RequestBody Customer customer) {
        try {
            Account acc = this.accountService.findAccountByEmailorPhone(customer.getEmail(), customer.getPhoneNumber());
            if (acc != null) {
                if (acc.getPhoneNumber().equalsIgnoreCase(customer.getPhoneNumber())) {
                    return ResponseEntity.status(BAD_REQUEST).body(
                            new ResponseObject("false", "S??? ??i???n tho???i ???? ???????c ????ng k?? t??i kho???n!", acc.getPhoneNumber())
                    );
                }
                if (acc.getEmail().equalsIgnoreCase(customer.getEmail())) {
                    return ResponseEntity.status(BAD_REQUEST).body(
                            new ResponseObject("false", "Email ???? ???????c ????ng k?? t??i kho???n!", acc.getEmail())
                    );
                }
            } else {
                String password = passwordEncoder.encode("Customer@123");
                Set<Role> roles = new HashSet<>();
                Role role = this.roleService.getRoleById(3);
                roles.add(role);
                Account account = new Account();
                account.setEmail(customer.getEmail());
                account.setPhoneNumber(customer.getPhoneNumber());
                account.setRoles(roles);
                account.setPassword(password);
                account.setStatus(0);
                this.accountService.add(account);
                customer.setAccount(account);
                CustomerDTO customerDTO = this.customerService.create(customer);
                return ResponseEntity.status(OK).body(
                        new ResponseObject("true", "Th??m kh??ch h??ng th??nh c??ng!", "")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(BAD_REQUEST).body(
                new ResponseObject("false", "Th??m kh??ch h??ng th???t b???i!", "")
        );
    }

    @GetMapping("customers/{id}")
    public ResponseEntity<ResponseDTO> getCustomersById(@PathVariable("id") Integer id) {
        try {
            CustomerDTO customerDTO = this.customerService.getById(id);
            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .status(OK)
                            .data(Map.of("customer", customerDTO))
                            .statusCode(OK.value())
                            .timeStamp(LocalDateTime.now())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .status(NO_CONTENT)
                            .data(Map.of("message", "Customer not exist"))
                            .statusCode(NO_CONTENT.value())
                            .timeStamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    @PutMapping("customers")
    public ResponseEntity<ResponseObject> updateCustomer(@RequestBody Customer customer) {
        try {
            Customer cus = this.customerRepository.checkCustomer(customer.getId(), customer.getPhoneNumber());
            if (cus != null) {
                    return ResponseEntity.status(BAD_REQUEST).body(
                            new ResponseObject("false", "S??? ??i???n tho???i ???? t???n t???i!", cus.getPhoneNumber())
                    );
                }else {
                Optional<Customer> customer1 = this.customerRepository.findById(customer.getId());
                if (customer1.isPresent()){
                    Account account = customer1.get().getAccount();
                    if (account != null) {
                        account.setCtime(LocalDateTime.now());
                        account.setPhoneNumber(customer.getPhoneNumber());
                        this.accountService.add(account);
                        customer.setBirthday(customer.getBirthday());
                        customer.setAccount(account);
                    }
                    this.customerService.update(customer);
                    List<CustomerDTO> customerDTOList = this.customerService.getAllCus();
                    return ResponseEntity.status(OK).body(
                            new ResponseObject("true", "C???p nh???t kh??ch h??ng th??nh c??ng!", customerDTOList)
                    );
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(BAD_REQUEST).body(
                new ResponseObject("false", "C???p nh???t th???t b???i!", "")
        );
    }

    @DeleteMapping("customers/{id}")
    public ResponseEntity<ResponseObject> deleteCustomer(@PathVariable(value = "id") Integer id) {
        Customer customer = this.customerRepository.findCusById(id);
        if (customer != null) {
            this.customerService.delete(customer);
            List<CustomerDTO> customerDTOList = this.customerService.getAllCus();
            return ResponseEntity.status(OK).body(
                    new ResponseObject("true", "X??a th??nh c??ng!", customerDTOList)
            );

        }
        return ResponseEntity.status(OK).body(
                new ResponseObject("true", "X??a th???t b???i!", "")
        );
    }

    @GetMapping("searchCustomer")
    public ResponseEntity<ResponseDTO> searchByAll(@RequestParam("search") String search) {
        List<CustomerDTO> customerDTOList = this.customerService.searchByAll(search);
        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .status(OK)
                        .data(Map.of("customer", customerDTOList))
                        .statusCode(OK.value())
                        .timeStamp(LocalDateTime.now())
                        .build()
        );
    }

//    @GetMapping("searchCustomerByName")
//    public ResponseEntity<ResponseDTO> searchCustomerByName( @RequestParam("customerName") String customerName) {
//        List<CustomerDTO> customerDTOList = this.customerService.searchByName(customerName);
//        return ResponseEntity.ok(
//                ResponseDTO.builder()
//                        .status(OK)
//                        .data(Map.of("customer", customerDTOList))
//                        .statusCode(OK.value())
//                        .timeStamp(LocalDateTime.now())
//                        .build()
//        );
//    }
//
//    @GetMapping("searchCustomerByEmail")
//    public ResponseEntity<ResponseDTO> searchCustomerByEmail( @RequestParam("email") String email) {
//        List<CustomerDTO> customerDTOList = this.customerService.searchByEmail(email);
//        return ResponseEntity.ok(
//                ResponseDTO.builder()
//                        .status(OK)
//                        .data(Map.of("customer", customerDTOList))
//                        .statusCode(OK.value())
//                        .timeStamp(LocalDateTime.now())
//                        .build()
//        );
//    }
//
//    @GetMapping("searchCustomerByPhoneNumber")
//    public ResponseEntity<ResponseDTO> searchCustomerByPhoneNumber( @RequestParam("phoneNumber") String phoneNumber) {
//        List<CustomerDTO> customerDTOList = this.customerService.searchByPhoneNumber(phoneNumber);
//        return ResponseEntity.ok(
//                ResponseDTO.builder()
//                        .status(OK)
//                        .data(Map.of("customer", customerDTOList))
//                        .statusCode(OK.value())
//                        .timeStamp(LocalDateTime.now())
//                        .build()
//        );
//    }
//
//    @GetMapping("searchCustomerByAddress")
//    public ResponseEntity<ResponseDTO> searchCustomerByAddress( @RequestParam("address") String address) {
//        List<CustomerDTO> customerDTOList = this.customerService.searchByAddress(address);
//        return ResponseEntity.ok(
//                ResponseDTO.builder()
//                        .status(OK)
//                        .data(Map.of("customer", customerDTOList))
//                        .statusCode(OK.value())
//                        .timeStamp(LocalDateTime.now())
//                        .build()
//        );
//    }
}




